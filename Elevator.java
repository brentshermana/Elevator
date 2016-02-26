import java.sql.Time;
import java.util.ArrayList;

public abstract class Elevator {
	protected int capacity;  // how many pounds can be present in the elevator
	// at any point of time

	protected int timeMoveOneFloor;    // how much time ( in seconds) does it take
	// to move one
	// floor, i.e. from the floor 5 to the floor 6 or from the floor 6 to the
	// floor 5. Assuming that the elevator moves equally fast in each of the
	// directions
	protected int floors;  // how many floors the elevator has to serve.
	protected int doorDelta; // how much time it takes to open or close doors.
	// assume that Opening doors takes same time as closing them and that amount
	// in seconds is equal to doorDelta.
	protected int currentFloor = 1;  // the floor where the elevator is right now
	@SuppressWarnings("deprecation")
	protected Time currentTime = new Time(8, 0, 0);  // Elevator starts "working"
	protected int startingFloor = 1;

	protected boolean verbose = false;

	protected Queue<PassengerRequest> servingQueue;

	/**
	 * Constructor to initialize Elevator object
	 * @param capacity - max value of the weight in the elevator in pounds
	 * @param timeMoveOneFloor - seconds to move the elevator up or down 1 floor
	 * @param floors   - number of floors in the building
	 * @param doorDelta  - how much time it takes to open/close elevator doors
	 */
	public Elevator(int capacity, int timeMoveOneFloor, int floors,
			int doorDelta, boolean verbose) {
		this.capacity = capacity;
		this.timeMoveOneFloor = timeMoveOneFloor;
		this.floors = floors;
		this.doorDelta = doorDelta;
		this.verbose = verbose;
	}

	/**
	 * Initialize the passenger queue. Note, even though all requests are given
	 * all at once. The elevator only has knowledge about requests up until
	 * "currentTime" variable. Elements from "requests" queue should be called
	 * by calling remove() function.
	 * @param requests Queue of requests
	 */

	/**
	 * Move elevator from one floor to another. Note that if no passenger
	 * left returning Arraylist should have 0 elements (it shouldn't be null),
	 * otherwise return an array list with passengers which left on that particular floor.
	 *
	 * @return  List of passengers which left on the next floor where elevator stopped.
	 */
	abstract ArrayList<PassengerReleased> move();

	abstract void initialize(Queue<PassengerRequest> queue);


	/**
	 *
	 * @return true if there are requests, false otherwise
	 */
	abstract boolean continueOperate();

	/**
	 * Function to "operate" elevator unless there are no more requests
	 * @return ArrayList with objects containing information when the passenger
	 * requested the elevator and when his request was fulfilled. This function
	 * will be called during grading/testing. Do not modify it.
	 * -- Ivan said I can modify it in my child class
	 */
	public ArrayList<PassengerReleased> operate() {
		ArrayList<PassengerReleased> released = new ArrayList<PassengerReleased>();
		while (this.continueOperate()) {
			ArrayList<PassengerReleased> moved = this.move();
			released.addAll(moved);
		}
		return released;
	}


}
