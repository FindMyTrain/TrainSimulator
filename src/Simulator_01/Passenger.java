package Simulator_01;

import java.util.ArrayList;
import java.util.Random;

public class Passenger 
{
	public int id,trainNo;
	public double arrTime;
	String src, dest, currStation, status, direction, route;
	ArrayList<PassengerInfo> passInfo = new ArrayList<PassengerInfo>();
	static int passengerId =1;
	static int p = MainActivity.passNumber;
	static int[] totalNumOfPassenger = {(new Random().nextInt(p/2) + p),(new Random().nextInt(p/2) + p),(new Random().nextInt(p/2) + p)};
	public static ArrayList<ArrayList<Passenger>> ListOfRoutePassenger = new ArrayList<ArrayList<Passenger>>(Station.numberOfRoutes);
			
	
	public Passenger(int id, int trainNo, double arrTime, String src, String dest, String currStation, String status, String direction, String route) 
	{
		this.id = id;
		this.trainNo = trainNo;
		this.arrTime = arrTime;
		this.src=src;
		this.dest=dest;
		this.status=status;
		this.currStation=currStation;
		this.direction=direction;
		this.route = route;
	}
}

class PassengerInfo
{
	String stationName;
	double currTime;
}