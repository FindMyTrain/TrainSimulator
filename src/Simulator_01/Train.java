package Simulator_01;

import java.util.ArrayList;

public class Train 
{	
	static int[] numberOfTrains = {4,4,4}; // { Western , Central , Harbour }
	static double haltTime = 20; // Time in seconds
	static double speed = 15; // speed in m/seconds
	
	
	static ArrayList<ArrayList<Event>> currentStation = new ArrayList<ArrayList<Event>>(numberOfTrains.length);
	
	public int trainID;
	public int numberOfPassenger;
	public Train(int trainID,int numberOfPassenger) 
	{
		this.trainID = trainID;
		this.numberOfPassenger = numberOfPassenger;
	}
	
	static ArrayList<ArrayList<Train>> routeTrainList = new ArrayList<ArrayList<Train>>(Station.numberOfRoutes);	
}
