package Simulator_01;

import java.io.IOException;
import java.util.Random;



public class EventHandler 
{	
	public static void initialiseTrain() // Bring the Trains to the Terminals ready to Initiate
	{
		int count;
		for (int i = 0; i < Train.numberOfTrains.length; i++) 
		{
			count=0;
			for (int j = 0; j < Train.numberOfTrains[i];j++) 
			{
				String Direction = "Up";
				String TypeOfEvent = "Init_"+Station.routeName[i];
				String StationName = Station.routeList.get(i).get(0).stationName;
				double Distance = Station.routeList.get(i).get(0).initialDistance;
				double TimeStamp = count;
				count += 1800;
				
				MainActivity.eventList.add(new Event(j, TypeOfEvent, StationName, Distance, TimeStamp, Direction));
				Train.currentStation.get(i).add(new Event(j, TypeOfEvent, StationName, Distance, TimeStamp, Direction));
				j++;
				
				if(Train.numberOfTrains[i]==j)	break;
				
				StationName = Station.routeList.get(i).get(Station.routeList.get(i).size()-1).stationName;
				Distance = Station.routeList.get(i).get(Station.routeList.get(i).size()-1).initialDistance;
				Direction = "Down";
				
				MainActivity.eventList.add(new Event(j, TypeOfEvent, StationName, Distance, TimeStamp, Direction));
				Train.currentStation.get(i).add(new Event(j, TypeOfEvent, StationName, Distance, TimeStamp, Direction));
			}
		}		
	}
	
	public static void Arrive(int TrainID, String TypeOfEvent, String StationName, double Distance, double TimeStamp, String Direction)
	{
		int routeID = new MainActivity().findRouteIndex(TypeOfEvent.split("_")[1]);
		Train.currentStation.get(routeID).set(TrainID, new Event(TrainID, TypeOfEvent, StationName, Distance,  TimeStamp, Direction));
		MainActivity.eventList.add(new Event(TrainID, "Waiting_"+TypeOfEvent.split("_")[1], StationName, Distance,  TimeStamp, Direction));
		
	}
	
	public static void Waiting(int TrainID, String TypeOfEvent, String StationName, double Distance, double TimeStamp, String Direction) throws IOException
	{	
		int routeID = new MainActivity().findRouteIndex(TypeOfEvent.split("_")[1]), rand = new Random().nextInt(100), wrc=100-MainActivity.wrD;
		int estDistError = MainActivity.LocationError==0? 0 : new Random().nextInt(2*MainActivity.LocationError) - MainActivity.LocationError;
		Train.currentStation.get(routeID).set(TrainID, new Event(TrainID, TypeOfEvent, StationName, Distance, TimeStamp, Direction));
		
		for (int i = 0; i < Passenger.ListOfRoutePassenger.size(); i++) 
		{
			for (int j = 0; j < Passenger.ListOfRoutePassenger.get(i).size(); j++) 
			{				
				Passenger pass = Passenger.ListOfRoutePassenger.get(i).get(j);
				PassengerInfo p = new PassengerInfo();
				if(!(pass.route.equals(Station.routeName[i])))	continue;
				
				if(pass.src.equals(StationName) && pass.route.equals(TypeOfEvent.split("_")[1]) && pass.status == "OnStation" && TimeStamp >= pass.arrTime)
				{
					if(pass.direction == Direction) {
						Passenger.ListOfRoutePassenger.get(i).get(j).status = "OnTrain";
						Passenger.ListOfRoutePassenger.get(i).get(j).currStation = StationName;
						Passenger.ListOfRoutePassenger.get(i).get(j).trainNo = TrainID;
						p.currTime = TimeStamp;
						p.stationName = StationName;
						Passenger.ListOfRoutePassenger.get(i).get(j).passInfo.add(p);
						
						/** When wrc % passenger provide input correctly ( NO direction error)*/
						if(rand <=wrc) {
							TrainSpotting trS = new TrainSpotting(pass.id, TimeStamp, new MainActivity().getInitialDistance(StationName,i)+estDistError, StationName, TypeOfEvent.split("_")[1], Direction);
							TrainSpotting.trainSpotting_onTrain.add(trS);
						}
						else {
							TrainSpotting trS = new TrainSpotting(pass.id, TimeStamp, new MainActivity().getInitialDistance(StationName,i)+estDistError, StationName, TypeOfEvent.split("_")[1], Direction=="Up"?"Down":"Up");
							TrainSpotting.trainSpotting_onTrain.add(trS);
						}
						
						for (int k = 0; k < Train.routeTrainList.get(i).size(); k++) {
							Train t = Train.routeTrainList.get(i).get(k);
							if(t.trainID == TrainID)	t.numberOfPassenger++;						
						}
					}
					else	// passenger who do not board train but provide input
					{
						if(rand <wrc) {
							TrainSpotting trS = new TrainSpotting(pass.id, TimeStamp, new MainActivity().getInitialDistance(StationName,i)+estDistError, StationName, TypeOfEvent.split("_")[1], Direction);
							TrainSpotting.trainSpotting_onTrain.add(trS);
						}
						else {
							TrainSpotting trS = new TrainSpotting(pass.id, TimeStamp, new MainActivity().getInitialDistance(StationName,i)+estDistError, StationName, TypeOfEvent.split("_")[1], Direction=="Up"?"Down":"Up");
							TrainSpotting.trainSpotting_onTrain.add(trS);
						}
					}
				}
			}
		}
		
		for (int i = 0; i < Passenger.ListOfRoutePassenger.size(); i++) 
		{
			for (int j = 0; j < Passenger.ListOfRoutePassenger.get(i).size(); j++) 
			{				
				Passenger pass = Passenger.ListOfRoutePassenger.get(i).get(j);
				PassengerInfo p = new PassengerInfo();
				if(!(pass.route.equals(Station.routeName[i])))	continue;
				if(pass.dest.equals(StationName) && pass.trainNo == TrainID && pass.status == "OnTrain")
				{
					Passenger.ListOfRoutePassenger.get(i).get(j).status = "OutOfStation";
					Passenger.ListOfRoutePassenger.get(i).get(j).currStation = StationName;
					p.currTime = TimeStamp;
					p.stationName = StationName;
					Passenger.ListOfRoutePassenger.get(i).get(j).passInfo.add(p);
					
					for (int k = 0; k < Train.routeTrainList.get(i).size(); k++) {
						Train t = Train.routeTrainList.get(i).get(k);
						if(t.trainID == TrainID)	t.numberOfPassenger--;						
					}
				}
			}
		}
		
		for (int i = 0; i < Passenger.ListOfRoutePassenger.size(); i++) 
		{
			for (int j = 0; j < Passenger.ListOfRoutePassenger.get(i).size(); j++) 
			{				
				Passenger pass = Passenger.ListOfRoutePassenger.get(i).get(j);
				PassengerInfo p = new PassengerInfo();
				if(!(pass.route.equals(Station.routeName[i])))	continue;
				if(pass.trainNo == TrainID && pass.status == "OnTrain" && pass.direction == Direction)
				{
					
					/** Train user provide correct (it has to be correct) input on every station   (GPS wala thing) */
					TrainSpotting trS = new TrainSpotting(pass.id, TimeStamp, new MainActivity().getInitialDistance(StationName,i)+estDistError, StationName, TypeOfEvent.split("_")[1], Direction);
					TrainSpotting.trainSpotting_onTrain.add(trS);
					
					Passenger.ListOfRoutePassenger.get(i).get(j).currStation = StationName;
					p.currTime = TimeStamp;
					p.stationName = StationName;
					Passenger.ListOfRoutePassenger.get(i).get(j).passInfo.add(p);
				}				
			}
		}
		
		TimeStamp += Train.haltTime;
		MainActivity.eventList.add(new Event(TrainID, "Departed_"+TypeOfEvent.split("_")[1], StationName, Distance, TimeStamp, Direction));
	}
	
	public static void Departed(int TrainID, String TypeOfEvent, String stationName, double Distance, double TimeStamp, String Direction) throws IOException
	{
		int stationIndex=0, routeIndex=0;
		double runningTime=0, delay= new Random().nextInt(MainActivity.RandomDelay), nextTime;
		
		routeIndex = new MainActivity().findRouteIndex(TypeOfEvent.split("_")[1]);
		stationIndex = new MainActivity().findStationIndex(stationName, routeIndex);
		Train.currentStation.get(routeIndex).set(TrainID, new Event(TrainID, TypeOfEvent, stationName, Distance, TimeStamp, Direction));
		
		if(Direction == "Up")
		{
			if(Station.TerminalsDown.contains(stationName))
			{
				Direction = "Down";
				Train.routeTrainList.get(routeIndex).add(new Train(TrainID, 0));
				MainActivity.eventList.add(new Event(TrainID, "Init_"+TypeOfEvent.split("_")[1] , stationName, Distance, TimeStamp, Direction));
			}
			else
			{
				runningTime = Station.routeList.get(routeIndex).get(stationIndex+1).nextStationDistance/Train.speed;
				nextTime = TimeStamp + runningTime + delay;
				
				Distance += Station.routeList.get(routeIndex).get(stationIndex+1).nextStationDistance;
				stationName = Station.routeList.get(routeIndex).get(stationIndex+1).stationName;
				MainActivity.eventList.add(new Event(TrainID, "Arrive_"+TypeOfEvent.split("_")[1] , stationName, Distance, nextTime, Direction));
				
			}			
		}
		else
		{
			if(Station.TerminalsUp.contains(stationName))
			{
				Direction = "Up";
				Train.routeTrainList.get(routeIndex).add(new Train(TrainID, 0));
				MainActivity.eventList.add(new Event(TrainID, "Init_"+TypeOfEvent.split("_")[1] , stationName, Distance, TimeStamp, Direction));
			}
			else
			{
				runningTime = Station.routeList.get(routeIndex).get(stationIndex).nextStationDistance/Train.speed;
				nextTime = TimeStamp + runningTime + delay;
				
				Distance -= Station.routeList.get(routeIndex).get(stationIndex).nextStationDistance;
				stationName = Station.routeList.get(routeIndex).get(stationIndex-1).stationName;
				MainActivity.eventList.add(new Event(TrainID, "Arrive_"+TypeOfEvent.split("_")[1] , stationName, Distance, nextTime, Direction));
			}	
		}
	}
	
	public static void initialisePassenger()
	{
		int src=0, dest;
		for (int i = 0; i < Passenger.totalNumOfPassenger.length; i++) 
		{
			for (int j = 0; j < Passenger.totalNumOfPassenger[i]; j++) 
			{				
				Passenger.ListOfRoutePassenger.get(i).get(j).id = Passenger.passengerId++;
				src = new Random().nextInt(Station.routeList.get(i).size()-1);
				dest = new Random().nextInt(Station.routeList.get(i).size()-1);
				while(src == dest)	dest= new Random().nextInt(Station.routeList.get(i).size()-1);
				Passenger.ListOfRoutePassenger.get(i).get(j).arrTime = new Random().nextInt(MainActivity.endTimeCounter);
				Passenger.ListOfRoutePassenger.get(i).get(j).src = Station.routeList.get(i).get(src).stationName;
				Passenger.ListOfRoutePassenger.get(i).get(j).dest = Station.routeList.get(i).get(dest).stationName;
				Passenger.ListOfRoutePassenger.get(i).get(j).route = Station.routeName[i];
				Passenger.ListOfRoutePassenger.get(i).get(j).direction = (src < dest)? "Up" : "Down";				
				Passenger.ListOfRoutePassenger.get(i).get(j).status = "OnStation";
				Passenger.ListOfRoutePassenger.get(i).get(j).route = Station.routeName[i];
			}
		}		
	}
	
}	
		
		
		
