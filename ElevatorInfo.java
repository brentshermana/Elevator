import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Arrays;

import javafx.util.Callback;

public class ElevatorInfo extends Elevator {
	// data members for program logic:
	public int currentWeight; // how much we're holding

	public Building building;

	// used for tracking what time it is at any point
	public Time sqlStartingTime;

	public long systemStartingTime;

	// tracks what buttons are pressed inside the elevator car
	public LinkedList<Integer> buttonsPressed;

	// tracks released passengers
	public ArrayList<PassengerReleased> released;

	// tracks current passengers:
	public LinkedList<PassengerRequest> occupants = new LinkedList<PassengerRequest>();

	public static ElevatorInfo ElevatorInfoInstance;

	private boolean freezeMainThread = true;

	public int releaseCap;

	public int timesMoved = 0;

	/*
	 * //inherited members:
	 *
	 * protected int capacity; //total capacity, in pounds protected int timeMoveOneFloor; //time to
	 * move up or down one floor protected int floors; //floors in building protected int doorDelta;
	 * //time to open/close doors protected int currentFloor = 1; //the floor where the elevator is
	 * right now
	 *
	 * @SuppressWarnings("deprecation") protected Time currentTime= new Time(8, 0, 0); // Elevator
	 * starts "working" protected int startingFloor = 1;
	 *
	 * protected boolean verbose = false; //what is this.
	 *
	 * protected Queue<PassengerRequest> servingQueue; // all requests that will be made
	 */

	private Callback<ArrayList<PassengerReleased>, Void> callback;

	public ElevatorInfo(int capacity, int timeMoveOneFloor, int floors, int doorDelta, boolean verbose) {
		super(capacity, timeMoveOneFloor, floors, doorDelta, verbose);

		sqlStartingTime = currentTime;
		currentFloor = 1; //now we're base - one!
		currentWeight = 0;

		building = new Building(this.floors);

		buttonsPressed = new LinkedList<Integer>();

		released = new ArrayList<PassengerReleased>();

		systemStartingTime = System.currentTimeMillis();
	}

	// inherited methods:
	ArrayList<PassengerReleased> move() {
		return new ArrayList<PassengerReleased>();
	}

	boolean continueOperate() {
		if (this.releaseCap <= released.size()) { // check if all have been served - CJL
			Print.print(released); // print results if they have - CJL
			return false;
		}
		return true; // if not all have been served continue to operate - CJL
	}

	public void printStatus () {
		if (verbose) {
			System.out.println("\nCurrently Released: " + this.released.size() + " out of " + this.releaseCap);
			System.out.println("Currently holding: " + this.occupants.size());
			System.out.println("Buttons Pressed " + Arrays.toString(buttonsPressed.toArray()));
			System.out.println("Currently on Floor " + this.currentFloor);
			System.out.println("Current Time: " + currentTime);

			System.out.println();
		}
	}

	@Override
	public ArrayList<PassengerReleased> operate() {
		systemStartingTime = System.currentTimeMillis();
		ElevatorInfoInstance = this;
		new Thread() {
            @Override
            public void run() {
                ApplicationStartGUI.launch(ApplicationStartGUI.class);
            }
        }.start();
        while(freezeMainThread){
        	try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
		return released;
	}

	// other methods:
	public boolean atGroundFloor() {
		return currentFloor == 1;
	}

	public boolean atMaxFloor() {
		return currentFloor == floors;
	}

	public int remainingSpace() {
		return capacity - currentWeight;
	}

	// call this before anything that requires the elevator's currentTime member to be accurate
	public void updateTime() {
		this.currentTime = TimeManip.systemTimeToSqlTime(this.systemStartingTime, this.sqlStartingTime,
				System.currentTimeMillis());
	}

	// internal class:
	public class ElevatorFloor {
		public ElevatorRequest request;

		public ElevatorFloor() {
			request = ElevatorRequest.nothing;
		}

		public boolean hasRequest() {
			if (request != ElevatorRequest.nothing) {
				return true;
			}
			return false;
		}

		public boolean hasUpRequest() {
			if (request == ElevatorRequest.up || request == ElevatorRequest.both) {
				return true;
			}
			return false;
		}

		public boolean hasDownRequest() {
			if (request == ElevatorRequest.down || request == ElevatorRequest.both) {
				return true;
			}
			return false;
		}
	}

	public void initialize(Queue<PassengerRequest> theQueue) {
		servingQueue = theQueue;
		releaseCap = theQueue.size();
	}

	public static ElevatorInfo getInstance() {
		return ElevatorInfoInstance;
	}

	public void finished() {
		freezeMainThread = false;
	}

	public enum ElevatorRequest {
		nothing, up, down, both
	}

}
