package Simulator_01;

public class Event 
{
	public double TimeStamp, Distance;
	public String TypeOfEvent, StationName, Direction;
	public int TrainID;

	public Event(int TrainID, String TypeOfEvent, String StationName, double Distance, double TimeStamp, String Direction) {
		this.TrainID = TrainID;
		this.TypeOfEvent = TypeOfEvent;
		this.StationName = StationName;
		this.Distance  = Distance;
		this.TimeStamp = TimeStamp;
		this.Direction = Direction;
	}
}
