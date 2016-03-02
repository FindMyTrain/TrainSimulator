package Simulator_01;

import java.util.ArrayList;

public class TrainSpotting 
{
	public static ArrayList<ArrayList<TrainSpotting>> trainSpotting = new ArrayList<ArrayList<TrainSpotting>>();	
	public static ArrayList<TrainSpotting> trainSpotting_onTrain = new ArrayList<TrainSpotting>();
	public static ArrayList<Double> positionConfidence = new ArrayList<>();
	public String stationName, route, direction;	
	public double timeStamp, initialDistance;
	public int userID;
	
	public TrainSpotting(int userID, double timeStamp, double initialDistance, String stationName, String route, String direction) {
		super();
		this.stationName = stationName;
		this.route = route;
		this.direction = direction;
		this.timeStamp = timeStamp;
		this.initialDistance = initialDistance;
		this.userID = userID;
	}
}

class TrainSpottingNow 
{
	public static ArrayList<ArrayList<TrainSpottingNow>> trainSpottingNow = new ArrayList<ArrayList<TrainSpottingNow>>();
	public static ArrayList<ArrayList<TrainSpottingNow>> trainSpottingNowUp = new ArrayList<ArrayList<TrainSpottingNow>>();
	public static ArrayList<ArrayList<TrainSpottingNow>> trainSpottingNowDown = new ArrayList<ArrayList<TrainSpottingNow>>();
	public String stationName, route;	
	public double timeStamp, distanceNow, confidence;
	public int userID;
	public String direction;
	
	public TrainSpottingNow(int userID, double timeStamp,String stationName, 
			double distanceNow, String route, String direction, double confidence) {
		super();
		this.stationName = stationName;
		this.route = route;
		this.timeStamp = timeStamp;
		this.distanceNow = distanceNow;
		this.direction = direction;
		this.confidence = confidence;
		this.userID = userID;
	}
}


class PosnConf
{
	public static ArrayList<ArrayList<PosnConf>> posConfidenceUp = new ArrayList<ArrayList<PosnConf>>(3);
	public static ArrayList<ArrayList<PosnConf>> posConfidenceDown = new ArrayList<ArrayList<PosnConf>>(3);
	public static ArrayList<PosnConf> peaks = new ArrayList<PosnConf>();
	public static ArrayList<Double> posError = new ArrayList<>(), negError = new ArrayList<>();
	public static int peakThreshold = 100; 
	double posConf, distanceNow;	
	int numberOfUser;
	public boolean isPeak;
	public String direction;
	
	public PosnConf(double distanceNow, String direction, double posConf, int numberOfUser, boolean isPeak) {
		super();
		this.posConf = posConf;
		this.distanceNow = distanceNow;
		this.direction = direction;
		this.numberOfUser = numberOfUser;
		this.isPeak = isPeak;
	}
	
}

class TrainNow
{
	public static ArrayList<TrainNow> trainNow = new ArrayList<TrainNow>();
	public static ArrayList<TrainNow> trainTemp = new ArrayList<TrainNow>();
	public String route, direction;
	public int TrainID;
	public double distAtTimeNow,extraDist;
	
	public TrainNow(String route, int trainID, double distAtTimeNow, double extraDist, String direction) {
		super();
		this.route = route;
		this.direction = direction;
		TrainID = trainID;
		this.distAtTimeNow = distAtTimeNow;
		this.extraDist = extraDist;
	}
}








