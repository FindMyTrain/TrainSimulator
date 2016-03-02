package Simulator_01;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class MainActivity 
{
	public static double timeCounter = 0;
	public static int endTimeCounter = 86400;// It's 1 day to stop the simulation. (2592000 = 30 days)
	public static int tId=0;
	public static double nowTime;
	public static int passNumber;
	public static double queryDelay = 30;
	public static int wrD = 0;
	public static int LocationError = 300;	// wrong location in case on GPS in meters
	public static int RandomDelay = 120;	//delay between stations in seconds
	public static Comparator<Event> Comparator = new Comparator<Event>() {

		@Override
		public int compare(Event c1, Event c2) {
			if (c1.TimeStamp < c2.TimeStamp)
				return -1;
			else if (c1.TimeStamp > c2.TimeStamp)
				return 1;
			return 0;
		}
	};

	public static Queue<Event> eventList = new PriorityQueue<>(5000, Comparator); 
	public static ArrayList<ArrayList<Event>> simOutput = new ArrayList<ArrayList<Event>>();

	
	public static void main(String[] args) throws IOException 
	{
		passNumber = Integer.parseInt(args[0]);
		LocationError = Integer.parseInt(args[1]);
		File result = new File("result"+LocationError+".txt");			
		if (!result.exists())	result.createNewFile();
		BufferedWriter result_BW = new BufferedWriter(new FileWriter(result.getAbsoluteFile()));
		new MainActivity().createStations();
		new MainActivity().createTrains();
		new MainActivity().createPassenger();
		EventHandler.initialisePassenger();
		EventHandler.initialiseTrain();
		new MainActivity().simulate();
		
		String str = getStdDev(PosnConf.posError);
		result_BW.append("Standard Deviation of +ve Error is " + str.split("_")[0] + " & mean of +ve Error is " + str.split("_")[1]+"\n");
		str = getStdDev(PosnConf.negError);
		result_BW.append("Standard Deviation of -ve Error is " + str.split("_")[0] + " & mean of -ve Error is " + str.split("_")[1]+"\n");
		result_BW.close();
		
	}
	
	/*************************************** Create Stations for each route ******************************************/
	public void createStations() 
	{
		int stationID;
		int routeID=0;
		String[] files = {"Western.csv", "Central.csv", "Harbour.csv"};
		ArrayList<Station> stationList = new ArrayList<Station>();		
		try 
		{
			for (String f : files) 
			{				
				File file = new File(f);
				if (!file.exists()) 
				{
					System.out.println("Input file not exist");
					System.exit(0);
				}
				
				@SuppressWarnings("resource")
				BufferedReader br = new BufferedReader(new FileReader(file));
				
				String line;
				String route= br.readLine().split(",")[0];
				Station.routeName[routeID++] = route;
				stationID=0;
				double dist = 0.0;
				while ((line = br.readLine()) != null) 
				{	
					String[] stn = line.split(",");
					if(stn[1]=="")	stn[1]= "1";
					dist += Double.parseDouble(stn[1]);
					Station st = new Station(routeID, route+"_"+(++stationID), stn[0], Double.parseDouble(stn[1]),dist);
					
					stationList.add(st);
				}
				Station.routeList.add(stationList);
				Station.TerminalsUp.add(stationList.get(0).stationName);
				Station.TerminalsDown.add(stationList.get(stationList.size()-1).stationName);
				
				stationList = new ArrayList<>();
			}
		}catch(IOException e) {}
	}
	
	/*************************************** Create Train for each route ******************************************/
	public void createTrains() 
	{
		ArrayList <Train> listOfTrains;
		
		for (int i = 0; i < Train.numberOfTrains.length; i++) 
		{
			listOfTrains = new ArrayList<Train>(Train.numberOfTrains[i]);
			Train.currentStation.add(new ArrayList<Event>(Train.numberOfTrains[i]));
			for (int j = 1; j <= Train.numberOfTrains[i]; j++) 
			{
				Train tr = new Train(j,0);
				listOfTrains.add(tr);
			}
			Train.routeTrainList.add(listOfTrains);
		}
	}
	
	/**************** Create Passenger for each route in Mumbai *******************/
	public void createPassenger() 
	{
		ArrayList <Passenger> listOfPassenger;
		for (int i = 0; i < Passenger.totalNumOfPassenger.length; i++) 
		{
			listOfPassenger = new ArrayList<Passenger>(Passenger.totalNumOfPassenger[i]);
			for (int j = 0; j < Passenger.totalNumOfPassenger[i]; j++) 
			{
				Passenger tr = new Passenger(Passenger.passengerId, -1, -1, null, null,null,"OutOfStation","","");
				listOfPassenger.add(tr);
			}
			Passenger.ListOfRoutePassenger.add(listOfPassenger);
		}
	}
	
	/***************************************************************************/
	/************************** The Main Simulator *****************************/
	/***************************************************************************/
	public void simulate() throws IOException
	{		
		for(int r=0; r<3; r++) {
			ArrayList<Event> temp = new ArrayList<Event>();
			for (int i = 2000; i < endTimeCounter; i+=30) {
				eventList.add(new Event(-1, "Query_"+Station.routeName[r]+"_"+(i+30), Station.routeList.get(r).get(Station.query[r]).stationName, Station.routeList.get(r).get(Station.query[r]).initialDistance, i, "Up"));
			}
			simOutput.add(temp);
		}
		
		nowTime = 2000;
		while(timeCounter <= endTimeCounter)
		{
			Event e = eventList.poll();
			if(e == null)	return;
			int routeInd = findRouteIndex(e.TypeOfEvent.split("_")[1]);
			if((e.TypeOfEvent.split("_")[0] != ("Waiting")) && (e.TypeOfEvent.split("_")[0] != ("Query")) )
			{
				for (int k = 0; k < Train.routeTrainList.get(routeInd).size(); k++) {
					Train t = Train.routeTrainList.get(routeInd).get(k);
					if(t.trainID == e.TrainID)	{
						break;
					}
				}
			}	
			
			if(e.TypeOfEvent.split("_")[1].equals("Western"))	simOutput.get(0).add(e);
			if(e.TypeOfEvent.split("_")[1].equals("Central"))	simOutput.get(1).add(e);
			if(e.TypeOfEvent.split("_")[1].equals("Harbour"))	simOutput.get(2).add(e);
			
			switch(e.TypeOfEvent.split("_")[0])
			{
			case "Init" : 
				eventList.add(new Event(e.TrainID, "Waiting_"+e.TypeOfEvent.split("_")[1] , e.StationName, e.Distance, e.TimeStamp, e.Direction));
				break;
			case "Arrive" :
				EventHandler.Arrive(e.TrainID, e.TypeOfEvent, e.StationName, e.Distance, e.TimeStamp, e.Direction);
				break;
			case "Waiting" :
				EventHandler.Waiting(e.TrainID, e.TypeOfEvent, e.StationName, e.Distance, e.TimeStamp, e.Direction);				
				break;
			case "Departed" :
				EventHandler.Departed(e.TrainID, e.TypeOfEvent, e.StationName, e.Distance, e.TimeStamp, e.Direction);
				break;
			case "Query" :
				solveQuery(e);
			default :
				break;
				
			}
			timeCounter = e.TimeStamp;
		}
	}
	
	public void solveQuery(Event e) throws IOException 
	{
		nowTime = e.TimeStamp;
		new Analysis().distinguishSpottings();
		new Analysis().getSpottingNow(e);
		
		Event gndTruth = findGroundTruth(e);
		if(gndTruth != null && gndTruth.Distance != 0 && gndTruth.Distance!=Station.routeLength[findRouteIndex(e.TypeOfEvent.split("_")[1])] && Math.abs(gndTruth.Distance - e.Distance) > 500.0) calculateError(e,gndTruth);
		
		TrainSpotting.trainSpotting.clear();
		
		TrainSpottingNow.trainSpottingNowUp.clear();
		TrainSpottingNow.trainSpottingNowDown.clear();
		TrainSpottingNow.trainSpottingNow.clear();
		
		PosnConf.posConfidenceUp.clear();
		PosnConf.posConfidenceDown.clear();
		PosnConf.peaks.clear();
	}
	
	public Event findGroundTruth(Event e) {
		Event gndTruth = null;
		int routeID = findRouteIndex(e.TypeOfEvent.split("_")[1]);
		if(e.Direction=="Up")	gndTruth = new Event(e.TrainID, e.TypeOfEvent, e.StationName, 0, e.TimeStamp, e.Direction);
		else gndTruth = new Event(e.TrainID, e.TypeOfEvent, e.StationName, Station.routeLength[routeID], e.TimeStamp, e.Direction);
		ArrayList<Event> trn = Train.currentStation.get(routeID);
		for (Event tr : trn) {
			if(tr.TypeOfEvent.split("_")[0].equals("Departed")) {
				if(tr.Direction == "Up")	{
					tr.Distance += (e.TimeStamp-tr.TimeStamp)*Train.speed;
					tr.TimeStamp = e.TimeStamp;
				}
				else	{
					tr.Distance -= (e.TimeStamp-tr.TimeStamp)*Train.speed;
					tr.TimeStamp = e.TimeStamp;
				}
				
				if(tr.Distance < 0) {
					tr.Distance  = 0;
					tr.StationName = Station.routeList.get(routeID).get(0).stationName;
				}
				if(tr.Distance > Station.routeLength[routeID])	{
					tr.Distance = Station.routeLength[routeID];
					tr.StationName = Station.routeList.get(routeID).get(Station.routeList.get(routeID).size()-1).stationName;
				}
			}
			else {
				tr.TimeStamp = e.TimeStamp;
			}
		}
		double min = 10000000;
		for(Event tr : trn) {
			if(tr.Direction == e.Direction && tr.Distance <= e.Distance && Math.abs(tr.Distance-e.Distance) < Math.abs(min)){
				gndTruth = tr;
				min = tr.Distance-e.Distance;
			}
		}
		return gndTruth;
	}
	
	public void calculateError(Event e, Event gndTruth) {
		int routeID = findRouteIndex(e.TypeOfEvent.split("_")[1]);
		double min = 100000000;
		PosnConf estTrain = new PosnConf(min, e.Direction, 0, 0, false);
		PosnConf.peaks.add(new PosnConf(0, e.Direction, 1, 1, true));
		PosnConf.peaks.add(new PosnConf(Station.routeLength[routeID], e.Direction, 1, 1, true));
		for(PosnConf p : PosnConf.peaks) {
			if(p.direction == e.Direction && Math.abs(p.distanceNow - e.Distance) < Math.abs(min) && p.distanceNow <= e.Distance) {
				min = (e.Distance - p.distanceNow);
				estTrain = p;
			}
		}
		System.out.println(Math.abs(gndTruth.Distance - estTrain.distanceNow));
		if(estTrain.distanceNow - gndTruth.Distance > 0)	PosnConf.posError.add(estTrain.distanceNow - gndTruth.Distance);
		else PosnConf.negError.add(gndTruth.Distance - estTrain.distanceNow);
	}
	
	
	public void calculateErrorDEBUG(Event e, Event gndTruth) {
		int routeID = findRouteIndex(e.TypeOfEvent.split("_")[1]);
		PosnConf estTrain = null;
		double min = 100000000;
		estTrain = new PosnConf(min, e.Direction, 0, 0, false);
		PosnConf.peaks.add(new PosnConf(0, e.Direction, 1, 1, true));
		PosnConf.peaks.add(new PosnConf(Station.routeLength[routeID], e.Direction, 1, 1, true));
		for(PosnConf p : PosnConf.peaks) {
			if(p.direction == e.Direction && Math.abs(p.distanceNow - e.Distance) < Math.abs(min) && p.distanceNow <= e.Distance) {
				min = (e.Distance - p.distanceNow);
				estTrain = p;
			}
		}
		if(Math.abs(estTrain.distanceNow - gndTruth.Distance) > 200)  {
			System.out.println(e.TimeStamp+" : " + Station.routeName[routeID]+" : " + estTrain.direction + " : " + gndTruth.Distance+" --- " + estTrain.distanceNow+ " --- " +Math.abs(estTrain.distanceNow - gndTruth.Distance));
			
			System.out.println("----------------------- " + e.Distance + " -------------------------------");
			for (Event tr : Train.currentStation.get(routeID)) {
				System.out.println("GND : " +tr.Distance + tr.Direction);
			}
			System.out.println("------------------------------------------------------");		
			
			System.out.println(gndTruth.Distance + " --- " + gndTruth.Direction);
			for(PosnConf p : PosnConf.peaks) {
				System.out.println("ESTD : " + p.distanceNow + " --- " + p.direction);
			}
			System.out.println("------------------------------------------------------");
		}
		if(estTrain.distanceNow - gndTruth.Distance > 0)	PosnConf.posError.add(estTrain.distanceNow - gndTruth.Distance);
		else PosnConf.negError.add(gndTruth.Distance - estTrain.distanceNow);
	}
		
	
	public static String getStdDev (ArrayList<Double> list) {
		double mean = 0, var= 0;
		for (int j = 0; j <list.size(); j++) {
			mean += list.get(j);
		}
		mean /= list.size();
		for (int j = 0; j <list.size(); j++) {
			var += (list.get(j) - mean) * (list.get(j) - mean);
		}
		var /= list.size();
		return(Math.sqrt(var)+"_"+mean);
	}
	
	/**************** Returns time in hh:mm:ss format *******************/
	public static String getHours(double t)
    {
		int time = (int) t;
    	String res = "";    	
    	res +=  (time/(24*3600) == 0)? "": ((time/(24*3600)/10==0)?"0"+time/(24*3600)+":":time/(24*3600)+":");
    	time %=(24*3600);
    	res +=  (time/(3600) == 0)? "00:": ((time/(3600)/10==0)?"0"+time/(3600)+":":time/(3600)+":");
    	time %=(3600);
    	res +=  (time/(60) == 0)? "00:": ((time/60/10==0)?"0"+time/(60)+":":time/(60)+":");
    	time %=(60);
    	res +=((time/10==0)?"0"+time:time+"");
    	return res;
    }
	
	/**************** Returns distance of station from its initial terminal *******************/
	public double getInitialDistance(String stationName, int routeIndex)
	{
		for (int j = 0; j < Station.routeList.get(routeIndex).size(); j++) 
		{
			Station st = Station.routeList.get(routeIndex).get(j);
			if(st.stationName.equals(stationName))	return st.initialDistance;
		}

		return -1.0;
	}
	
	/**************** Returns the index of route *******************/
	public int findRouteIndex(String route)	{
		switch(route)
		{
		case "Western" : return 0;
		case "Central" : return 1;
		case "Harbour" : return 2;
		}
		return -1;
	}
	
	
	public int findStationIndex(String stationName,int routeIndex)	{
		int stationIndex=-1;
		for (int i = 0; i < Station.routeList.get(routeIndex).size(); i++) {
			if((Station.routeList.get(routeIndex).get(i).stationName).equals(stationName))
			{
				stationIndex = i;
				break;
			}
		}
		return stationIndex;
	}
}
