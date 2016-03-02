package Simulator_01;

import java.io.IOException;
import java.util.ArrayList;

public class Analysis
{
	/************** Distinguish spotting on the basis of routes *************/
	public void distinguishSpottings()
	{
		ArrayList<TrainSpotting> one = new ArrayList<TrainSpotting>();
		ArrayList<TrainSpotting> two = new ArrayList<TrainSpotting>();
		ArrayList<TrainSpotting> three = new ArrayList<TrainSpotting>();
		for (int i = 0; i < TrainSpotting.trainSpotting_onTrain.size(); i++) 
		{
			TrainSpotting spot = TrainSpotting.trainSpotting_onTrain.get(i);
			switch(new MainActivity().findRouteIndex(spot.route))
			{
			case 0: one.add(spot);
				break;
			case 1: two.add(spot);
				break;
			case 2: three.add(spot);
				break;
			}			
		}
		TrainSpotting.trainSpotting.add(one);
		TrainSpotting.trainSpotting.add(two);
		TrainSpotting.trainSpotting.add(three);
	}
	
	/************** Map spottings to current time IOException *************/
	public void getSpottingNow(Event e) throws IOException
	{
		double distNow=0.0, distOld=0.0, confidence = 0.0;
		int j=0;
		ArrayList<TrainSpottingNow> tsNowUp, tsNowDown, tsNow;
		String stationName,dist_Station;
		for (ArrayList<TrainSpotting> spotRoute : TrainSpotting.trainSpotting) 
		{	
			tsNowUp = new ArrayList<TrainSpottingNow>();
			tsNowDown = new ArrayList<TrainSpottingNow>();
			tsNow = new ArrayList<TrainSpottingNow>();
			for (TrainSpotting tSpot : spotRoute) 
			{
				confidence = getConfidenceFromPast(tSpot.timeStamp, MainActivity.nowTime);
				if(confidence > 0) {
					dist_Station = findDistanceNow(tSpot,MainActivity.nowTime);
					distOld = new MainActivity().getInitialDistance(tSpot.stationName,j);
					if(tSpot.direction.equals("Up"))	distNow = Double.parseDouble(dist_Station.split("_")[0]) + distOld;
					else	distNow = distOld - Double.parseDouble(dist_Station.split("_")[0]) ;
					
					if(Double.parseDouble(dist_Station.split("_")[0]) > 0){
						stationName = dist_Station.split("_")[1];
						tsNow.add(new TrainSpottingNow(tSpot.userID, tSpot.timeStamp, stationName, distNow, tSpot.route, tSpot.direction, confidence));
						if(tSpot.direction.equals("Up"))	tsNowUp.add(new TrainSpottingNow(tSpot.userID, tSpot.timeStamp, stationName, distNow, tSpot.route, tSpot.direction, confidence));
						else	tsNowDown.add(new TrainSpottingNow(tSpot.userID, tSpot.timeStamp, stationName, distNow, tSpot.route, tSpot.direction, confidence));
					}
				}
			}
			TrainSpottingNow.trainSpottingNowUp.add(tsNowUp);		// notes only for up direction spottings
			TrainSpottingNow.trainSpottingNowDown.add(tsNowDown);	// notes only for down direction spottings
			TrainSpottingNow.trainSpottingNow.add(tsNow);			// notes all spottings
			j++;
		}
		
		getPositionConfidence(e);
	}
	
	
	/************** Find probability of every spottings at 100 mtrs distance interval *************/
	public void getPositionConfidence(Event e) throws IOException
	{
		
		double confDist, overallConf, dist, distPres = 0, posnConf, tempConf;
		int numberOfUser,j=0;
		ArrayList<PosnConf> temp;
		PosnConf psC;
		
		
		/***************** Computes Position Confidence of Up Direction Trains ********************/		
		for (ArrayList<TrainSpottingNow> spotRouteNow : TrainSpottingNow.trainSpottingNowUp) 
		{
			dist = 0;
			temp = new ArrayList<PosnConf>();
			while(dist<=Station.routeLength[j])
			{
				posnConf = 0;
				tempConf =1;
				numberOfUser = 0;
				for (int i = 0; i < spotRouteNow.size(); i++) 
				{
					TrainSpottingNow spotNow = spotRouteNow.get(i);
					distPres = spotNow.distanceNow;	
					if (Math.abs(distPres - dist) < 2000) 
					{
						confDist = getConfidenceFromDistance(Math.abs(dist - distPres));
						overallConf = spotNow.confidence * confDist;
						tempConf *= (1 - overallConf);
						numberOfUser++;
					}						
				}
				
				posnConf = (1-tempConf);
				temp.add(new PosnConf(dist,"Up", posnConf, numberOfUser, true));			
					
				dist += 100;
			}
			PosnConf.posConfidenceUp.add(temp);
			j++;
		}		
		for (int i = 0; i < PosnConf.posConfidenceUp.size(); i++) 
		{
			for (int k = 0; k < PosnConf.posConfidenceUp.get(i).size(); k++) 
			{
				psC = PosnConf.posConfidenceUp.get(i).get(k);
				posnConf = psC.posConf;
				psC.posConf = posnConf;
			}
		}
		
		
		
		j=0;
		
		/***************** Computes Position Confidence of Down Direction Trains ********************/		
		for (ArrayList<TrainSpottingNow> spotRouteNow : TrainSpottingNow.trainSpottingNowDown) 
		{
			dist = 0;
			temp = new ArrayList<PosnConf>();
			while(dist<=Station.routeLength[j])
			{
				posnConf = 0;
				tempConf =1;
				numberOfUser = 0;
				for (int i = 0; i < spotRouteNow.size(); i++) 
				{
					TrainSpottingNow spotNow = spotRouteNow.get(i);
					distPres = spotNow.distanceNow;	
					if (Math.abs(distPres - dist) < 500) 
					{
						confDist = getConfidenceFromDistance(Math.abs(dist - distPres));
						overallConf = spotNow.confidence * confDist;
						tempConf *= (1 - overallConf);
						numberOfUser++;
					}						
				}
				
				posnConf = (1-tempConf);
				temp.add(new PosnConf(dist, "Down", posnConf, numberOfUser, true));			
					
				dist += 100;
			}
			PosnConf.posConfidenceDown.add(temp);
			j++;
		}		
		for (int i = 0; i < PosnConf.posConfidenceDown.size(); i++) 
		{
			for (int k = 0; k < PosnConf.posConfidenceDown.get(i).size(); k++) 
			{
				psC = PosnConf.posConfidenceDown.get(i).get(k);
				posnConf = psC.posConf;
				psC.posConf = posnConf;
			}
		}
		
		
		
		computePeaks();		// compute the peaks in positionConfidence to get location of train
		getBestPeak(e);
	}
		
	/**********************Get Median Value for single estimated train location*****************************/
	public void getBestPeak(Event e) {	
		ArrayList<PosnConf> median = new ArrayList<>();
		boolean flag = false;
		int routeID = new MainActivity().findRouteIndex(e.TypeOfEvent.split("_")[1]);
		for (PosnConf pc : PosnConf.posConfidenceUp.get(routeID)) 
		{
			if(pc.isPeak) {
				median.add(pc);
				flag = true;
			}
			else if(flag) {
				PosnConf.peaks.add(getMedian(median));
				flag = false;
				median.clear();
			}
		}
		if(median.size() > 0) {
			PosnConf.peaks.add(getMedian(median));
			flag = false;
			median.clear();
		}
		
		median = new ArrayList<>();
		flag = false;
		
		for (PosnConf pc : PosnConf.posConfidenceDown.get(routeID)) 
		{
			if(pc.isPeak) {
				median.add(pc);
				flag = true;
			}
			else if(flag) {
				PosnConf.peaks.add(getMedian(median));
				flag = false;
				median.clear();
			}
		}
		if(median.size() > 0) {
			PosnConf.peaks.add(getMedian(median));
			flag = false;
			median.clear();
		}
	}
		
	
	public PosnConf getMedian(ArrayList<PosnConf> pc) {
		double dis;
		if(pc.size()%2 != 0) return pc.get(pc.size()/2);
		else {
			dis = (pc.get(pc.size()/2).distanceNow + pc.get((pc.size()/2)-1).distanceNow)/2;
			return new PosnConf(dis, pc.get(0).direction, pc.get(0).posConf, pc.get(0).numberOfUser, pc.get(0).isPeak);
		}
	}
	
	/******* Compute the peaks in positionConfidence to get location of train ********/
	public void computePeaks()
	{
		int jStart,jEnd;
		PosnConf psC;
		for (int i = 0; i < PosnConf.posConfidenceUp.size(); i++)  // Up direction trains location detection
		{
			for (int j = 0; j < PosnConf.posConfidenceUp.get(i).size(); j++) 
			{
				psC = PosnConf.posConfidenceUp.get(i).get(j);
				if(psC.posConf == 0){
					psC.isPeak=false;
					continue;
				}
				if(j<PosnConf.peakThreshold)	jStart=0;
				else	jStart= j-PosnConf.peakThreshold;
				
				if(j + PosnConf.peakThreshold>=PosnConf.posConfidenceUp.get(i).size())
					jEnd=PosnConf.posConfidenceUp.get(i).size()-1;
				else	jEnd = j+PosnConf.peakThreshold;
				
				for (int k = jStart; k <= jEnd; k++) {
					if(psC.posConf<PosnConf.posConfidenceUp.get(i).get(k).posConf)	{
						psC.isPeak = false;
						break;
					}
				}					
			}
		}		
		
		for (int i = 0; i < PosnConf.posConfidenceDown.size(); i++) 		 // Down direction trains location detection
		{
			for (int j = 0; j < PosnConf.posConfidenceDown.get(i).size(); j++) {
				psC = PosnConf.posConfidenceDown.get(i).get(j);
				if(psC.posConf == 0){
					psC.isPeak=false;
					continue;
				}
				if(j<PosnConf.peakThreshold)	jStart=0;
				else	jStart= j-PosnConf.peakThreshold;
				
				if(j + PosnConf.peakThreshold>=PosnConf.posConfidenceDown.get(i).size())
					jEnd=PosnConf.posConfidenceDown.get(i).size()-1;
				else	jEnd = j+PosnConf.peakThreshold;
				
				for (int k = jStart; k <= jEnd; k++) {
					if(psC.posConf<PosnConf.posConfidenceDown.get(i).get(k).posConf)	{
						psC.isPeak = false;
						break;
					}
				}					
			}
		}			
	}
	
	/******************* Find the distance and station of the estimation of where the train should be now based on Spottings *************/
	public String findDistanceNow(TrainSpotting tSpot, double nowTime) 
	{
		double oldTime = tSpot.timeStamp, timeDiff = nowTime - oldTime, distTravelled = 0, temp, estDelay = MainActivity.RandomDelay/2;
		int routeIndex = new MainActivity().findRouteIndex(tSpot.route), stationIndex = new MainActivity().findStationIndex(tSpot.stationName, routeIndex );
		
		if(tSpot.direction.equals("Up"))
		{
			while(timeDiff > 20)
			{
				if(stationIndex < Station.routeList.get(routeIndex).size()-1)	{
					temp = (20 + (Station.routeList.get(routeIndex).get(stationIndex+1).nextStationDistance / Train.speed));
					if(timeDiff-temp > 0)	{
						timeDiff -= (temp+estDelay);
						distTravelled +=Station.routeList.get(routeIndex).get(++stationIndex).nextStationDistance;
					}else	break;
				}else	return -1.0+"_";
			}
		}
		else
		{
			while(timeDiff > 20)
			{
				if(stationIndex > 0)	{
					temp = (20 + (Station.routeList.get(routeIndex).get(stationIndex).nextStationDistance / Train.speed));
					if(timeDiff-temp > 0)	{
						timeDiff -= (temp+estDelay);
						distTravelled +=Station.routeList.get(routeIndex).get(stationIndex--).nextStationDistance;
					}else	break;
				}else	return -1.0+"_";
			}
		}
		
		if(timeDiff > 20) {
			return distTravelled + (timeDiff-20)*Train.speed +"_"+ Station.routeList.get(routeIndex).get(stationIndex).stationName;
		}
		else {
			return distTravelled+"_"+ Station.routeList.get(routeIndex).get(stationIndex).stationName;
		}
	}

	/************ Compute confidence in user input, adjusted for passage of time ***********/
	public double getConfidenceFromPast(double t, double nowtime) 
	{
		double timeDifference = nowtime - t;
		if(timeDifference < 60) return 1;
		if(timeDifference < 300) return 0.9;
		if(timeDifference < 900) return 0.8;
		if(timeDifference < 1800) return 0.6;
		if(timeDifference < 3600) return 0.3;
		if(timeDifference > 14400) return 0;
		return (0.3 - 0.3*timeDifference/14400);
	}
	
	/********* Compute confidence in user input, adjusted for distance **********/
	double getConfidenceFromDistance(double diffm) 
	{
	  if(diffm < 100) return 1;
	  if(diffm < 200) return 0.9;
	  if(diffm < 400) return 0.8;
	  if(diffm < 800) return 0.6; 
	  if(diffm < 1200) return 0.3; 
	  if(diffm > 2000) return 0;
	  return (0.3 - 0.3*diffm/(2000));
	}
}
