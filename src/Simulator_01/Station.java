package Simulator_01;

import java.util.ArrayList;

public class Station 
{
	public int routeID;	
	public String stationID;
	public String stationName;
	public double nextStationDistance;
	public double initialDistance;
	public int numberOfPassenger;
	public static double[] routeLength = {123780.0, 54000.0,49000.0};
	//public static String[] query = { "Andheri", "Kanjurmarg", "Mankhurd"};
	public static int[] query = { 14, 14, 14};
	
	public Station(int routeID, String stationID, String stationName, double nextStationDistance,
			double initialDistance) {
		super();
		this.routeID = routeID;
		this.stationID = stationID;
		this.stationName = stationName;
		this.nextStationDistance = nextStationDistance;
		this.initialDistance = initialDistance;
	}
	
	static int numberOfRoutes = 3;
	static ArrayList<String> TerminalsUp = new ArrayList<>(numberOfRoutes);
	static ArrayList<String> TerminalsDown = new ArrayList<>(numberOfRoutes);
	static String[] routeName = new String[numberOfRoutes];
	static ArrayList<ArrayList<Station>> routeList = new ArrayList<ArrayList<Station>>(numberOfRoutes);
}
