import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;
import java.util.LinkedList;


public class Main {
	public static void main (final String[] args) {
		//make the ElevatorInfo object:
        ElevatorInfo ei = new ElevatorInfo(Params.capacity, Params.moveTime, Params.floors, Params.doorTime, true);

        //make a queue for testing:
        Queue<PassengerRequest> theQueue = PassengerRequest.generateSample(new Time(8,0,0));
        //give that queue to the ElevatorInfo
        ei.initialize(theQueue);

        //this method runs the elevator and returns the generated PassengerReleased list
        ArrayList<PassengerReleased> releasedList = ei.operate();

        //do whatever with the list
		printPassengers(releasedList);

		System.out.println("\nTimes moved: " + ei.timesMoved);
		System.out.println("mean wait time: " + meanWaitTime(releasedList));
	}


	public static void printPassengers (ArrayList<PassengerReleased> list) {
		for (int i = 0; i < list.size(); i++) {
			System.out.println("Passenger " + i + ": " + list.get(i).toString());
		}
	}
	public static double meanWaitTime (ArrayList<PassengerReleased> list) {
        int sum = 0;
        for (int i = 0; i < list.size(); i++) {
            final PassengerReleased pr = list.get(i);
            sum += (TimeManip.difference(pr.getPassengerRequest().getTimePressedButton(), pr.getTimeArrived()));
        }
        return (double)sum / list.size();
    }
}
