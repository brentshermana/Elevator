import java.util.ArrayList;
import java.util.Iterator;

import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ElevatorController extends Group {

	ElevatorInfo theElevator;

	Brain theBrain;

	@FXML
	AnchorPane page;

	@FXML
	StackPane elevatorPane;

	@FXML
	Text genericText;

	ArrayList<Queue<PassengerRequest>> peopleWaiting;

	Text[] peopleWaitingText;

	private static final String peopleText = "People: ";

	@FXML
	Text peopleCount;

	@FXML
	Rectangle elevator;

	@FXML
	Button start;

	// STATIC VARIABLES
	private static final long WAITING_TIME = 10;// millisec

	public static final int WINDOW_WIDTH = 600;

	public static final int WINDOW_HEIGHT = 500;

	private int MOVE_ONE_FLOOR_TIME;// millisec

	private int OPEN_DOOR_TIME;

	private static final int ELEVATOR_WIDTH = 100;

	private static final int ELEVATOR_HEIGHT = 110;

	private static final int ELEVATOR_START_AT = 340;// botton

	private static final int ELEVATOR_X_POSITION = 470;// right corner

	private static final int PEOPLE_OFFSET = 200;

	private static final int START_WIDTH = 30;

	private static final int START_HEIGHT = 30;

	private static final int START_X_POSITION = 15;

	private static final int START_Y_POSITION = 15;

	private static final String START_TEXT = "Start";

	private static final int TEXT_WIDTH = 100;

	private static final int TEXT_X_POSITION = (WINDOW_WIDTH - (WINDOW_WIDTH / 2)) - (TEXT_WIDTH / 2); // center

	private static final int TEXT_Y_POSITION = 50;

	private static String TEXT_VALUE = "";

	// DYNAMIC CONTROL VARIABLES
	private int moveElevatorNextLevel;

	private int elevatorMoveRange = 300;

	private int numberOfPassengerRequestsInside = 0;

	public ElevatorController() {
		genericText = new Text();
		page = new AnchorPane();
		elevatorPane = new StackPane();
		peopleCount = new Text(peopleText);
		elevator = new Rectangle();
		start = new Button(START_TEXT);

		elevatorPane.setLayoutX(ELEVATOR_X_POSITION);
		elevatorPane.setLayoutY(ELEVATOR_START_AT);

		start.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Thread th = new Thread(new Runnable() {
					@Override
					public void run() {
						System.out.println("Button pressed: Starting....");
						update(Cmd.start);
					}
				});
				th.start();
			}
		});

		elevatorPane.getChildren().addAll(peopleCount, elevator);
		peopleCount.toFront();
		peopleCount.setFill(Color.WHITE);
		peopleCount.setTextAlignment(TextAlignment.CENTER);
		elevatorPane.setAlignment(peopleCount, Pos.TOP_CENTER);

		page.getChildren().add(genericText);
		page.getChildren().add(elevatorPane);
		page.getChildren().add(start);
		this.getChildren().add(page);

		initialize();
	}

	// not to be confused with the 'initialize' inherited from 'Elevator'
	public void initialize() {
		// Everything starts here ...

		theElevator = ElevatorInfo.getInstance();
		// initialize control variables based on ElevatorInfo parameters:
		MOVE_ONE_FLOOR_TIME = theElevator.timeMoveOneFloor;
		OPEN_DOOR_TIME = theElevator.doorDelta / 2;
		moveElevatorNextLevel = elevatorMoveRange / theElevator.floors;

		try {
			Class<?> clazz = Class.forName(Params.brainType);
			theBrain = (Brain) clazz.newInstance();
		} catch (Exception e) {
			System.out.println("Error with Brain instantiation");
			System.exit(-1);
		}


		// my JavaFX setup stuff:
		peopleWaitingText = new Text[theElevator.floors];
		peopleWaiting = new ArrayList<Queue<PassengerRequest>>(theElevator.floors);
		for (int i = 0; i < peopleWaitingText.length; i++) {
			peopleWaiting.add(new Queue<PassengerRequest>());
			peopleWaitingText[i] = createTextPeopleCount(i);
		}

		// Your JavaFX setup stuff:
		genericText.setLayoutX(TEXT_X_POSITION);
		genericText.setLayoutY(TEXT_Y_POSITION);
		genericText.setWrappingWidth(TEXT_WIDTH);
		genericText.setText(TEXT_VALUE);
		genericText.setTextAlignment(TextAlignment.CENTER);

		peopleCount.setWrappingWidth(TEXT_WIDTH);

		start.setLayoutX(START_X_POSITION);
		start.setLayoutY(START_Y_POSITION);
		start.setMinSize(START_WIDTH, START_HEIGHT);

		elevator.setWidth(ELEVATOR_WIDTH);
		elevator.setHeight(ELEVATOR_HEIGHT);
		elevator.setLayoutY(ELEVATOR_START_AT);
		elevator.setLayoutX(ELEVATOR_X_POSITION);

		elevator.setFill(Color.BLUE);

		page.setMaxWidth(WINDOW_WIDTH);
		page.setMaxHeight(WINDOW_HEIGHT);
	}

	private Text createTextPeopleCount(int index) {
		Text count = new Text();
		count.setLayoutX(ELEVATOR_X_POSITION - 1.5 * PEOPLE_OFFSET);
		// discounting the Height of the component to let down of the line of the elevator
		count.setLayoutY((ELEVATOR_START_AT - (index * moveElevatorNextLevel) + count.getLayoutBounds().getHeight()));
		// count.setLayoutY(ELEVATOR_START_AT - (index * moveElevatorNextLevel));
		count.setWrappingWidth(TEXT_WIDTH * 4);
		count.setTextAlignment(TextAlignment.LEFT);
		count.setText(peopleText + "0");
		page.getChildren().add(count);
		return count;
	}

	/*
	 * This method is called every time on animation is finished. you can put all your logic here
	 * ...
	 */
	private void update(Cmd prevCommand) {
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				//BOOKMARK
				if (prevCommand == Cmd.start) {
					theElevator.systemStartingTime = System.currentTimeMillis();
				}

				if (theElevator.verbose) {
					System.out.println("Update called with Cmd: " + prevCommand);
				}
				theElevator.updateTime();

				// begin cleanup...

				// check theElevator's servingQueue to see if more
				// passenger requests should be added to the Building
				// TODO: test this!
				updatePassengersInBuilding(theElevator);

				// update theElevator values based on last command
				if (prevCommand == Cmd.up) {
					theElevator.currentFloor++;
				} else if (prevCommand == Cmd.down) {
					theElevator.currentFloor--;
				} else if (prevCommand == Cmd.open) {
					// we need to decide where we're going to go next, because
					// that influences which external button we depress, which
					// influences which set of people we're going to let in!
					// DONE! (in brain)

					// LET PEOPLE OUT:
					letPassengersLeaveCar(theElevator);
					// LET PEOPLE IN:
					letPassengersEnterCar(theElevator, theBrain.nextMovementCmd);
					// DEPRESS buttons for this floor inside and outside elevator:
					depressButtonsForThisFloor(theElevator, theBrain.nextMovementCmd);

					// depress button inside car
					int index = theElevator.buttonsPressed.indexOf(theElevator.currentFloor);
					if (index != -1) {
						theElevator.buttonsPressed.remove(index);
					}
				}
				updateBuildingText(theElevator.building);
				setText((theElevator.currentFloor) + " Floor");
				// end cleanup^^^

				Cmd nextCmd = theBrain.decide(prevCommand, theElevator);
				// execute command recived from Brain
				executeCommand(nextCmd, prevCommand);
			}
		});
		th.start();
	}

	private void executeCommand(Cmd theCmd, Cmd prevCommand) {
		switch (theCmd) {
			case wait:
				if (prevCommand == Cmd.open) {
					closeDoors();
				} else {
					waitFor();
				}
				break;
			case up:
				theElevator.timesMoved++;
				moveUp();
				break;
			case down:
				theElevator.timesMoved++;
				moveDown();
				break;
			case open:
				openDoors();
				break;
			case close:
				closeDoors();
				break;
			case start:
				System.out.println("Error: 'executeCommand' in ElevatorController shouldn't recieve 'start' Cmd");
				break;
			case finish:
				System.out.println("Finished operating!");
				Platform.exit();
				break;
			default:
				System.out.println("Error: 'executeCommand' does not recognize command: " + theCmd);
				break;
		}
	}

	// depressButtonsForThisFloor(theElevator, theBrain.nextMovementCmd);
	private void depressButtonsForThisFloor(ElevatorInfo theElevator, Cmd nextMovement) throws RuntimeException {
		int floor = theElevator.currentFloor;
		// depress button within car
		int indexOfButtonPressed = theElevator.buttonsPressed.indexOf(floor);
		if (!(indexOfButtonPressed == -1)) {
			theElevator.buttonsPressed.remove(indexOfButtonPressed);
		}
		// depressing button outside of car:
		/*
		 * BUT only if there aren't any more people waiting to enter! If there were four people
		 * waiting to get in, and only three could enter, we shouldn't change the button's status
		 */
		Building b = theElevator.building;
		if (nextMovement == Cmd.up) {
			if (b.getPeopleWaitingToGoUp(floor).isEmpty()) {
				theElevator.building.removeUpRequest(theElevator.currentFloor);
			}
		} else if (nextMovement == Cmd.down) {
			if (b.getPeopleWaitingToGoDown(floor).isEmpty()) {
				theElevator.building.removeDownRequest(theElevator.currentFloor);
			}
		} else {
			// shouldn't happen: 'up' and 'down' are the only two moves we have!
			throw new RuntimeException();
		}
	}

	public void letPassengersEnterCar(ElevatorInfo theElevator, Cmd nextMovementCmd) {
		/*
		 * People will only enter if they want to go up and the 'up' button just depressed, or if
		 * they want to go down, and the down button was just depressed. We assign the Queue based
		 * on this logic
		 */
		Queue<PassengerRequest> theQueue;
		if (nextMovementCmd == Cmd.up) {
			theQueue = theElevator.building.getPeopleWaitingToGoUp(theElevator.currentFloor);
		} else {
			// we can safely assume that Cmd is Cmd.down
			theQueue = theElevator.building.getPeopleWaitingToGoDown(theElevator.currentFloor);
		}

		boolean anyEntered = false;
		// while there are still more passengers to add, and the elevator isn't too full...
		while (theQueue.peek() != null && theQueue.peek().getWeight() <= theElevator.remainingSpace()) {
			// ...add them ...
			PassengerRequest temp = theQueue.remove();
			theElevator.currentWeight += temp.getWeight();
			theElevator.occupants.addLast(temp);
			int floorTo = temp.getFloorTo();
			// ... and have them press the appropriate button, if not already pushed
			if (!theElevator.buttonsPressed.contains(floorTo)) {
				theElevator.buttonsPressed.add(floorTo);
			}
			PassengerRequestEnterElevator();
		}
	}

	public void letPassengersLeaveCar(ElevatorInfo theElevator) {
		theElevator.updateTime();
		ArrayList<PassengerReleased> r = new ArrayList<PassengerReleased>();
		Iterator<PassengerRequest> it = theElevator.occupants.iterator();
		while (it.hasNext()) {
			PassengerRequest temp = it.next();
			if (temp.getFloorTo() == theElevator.currentFloor) {
				r.add(new PassengerReleased(temp, theElevator.currentTime));
				theElevator.currentWeight -= temp.getWeight();
				it.remove();
				PassengerRequestLeaveElevator();
			}
		}
		theElevator.released.addAll(r);
		displayReleased(theElevator);
	}

	public void displayReleased(ElevatorInfo theElevator) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				genericText.setText("Released: " + theElevator.released.size());
			}
		});
	}

	public void updatePassengersInBuilding(ElevatorInfo e) {
		e.updateTime();
		if (theElevator.verbose) {
			System.out.println("current time is " + e.currentTime);
		}


		boolean addedAnyPeople = false;
		// while servingQueue still has more people, and their times are before/equal to the current
		// time...
		while (e.servingQueue.peek() != null
				&& e.servingQueue.peek().getTimePressedButton().compareTo(e.currentTime) <= 0) {
			// ...delegate requests from main queue to queues in 'building' and press appropriate
			// buttons

			PassengerRequest temp = e.servingQueue.remove();
			boolean upPress = (temp.getFloorFrom() < temp.getFloorTo());
			if (upPress) {
				e.building.getPeopleWaitingToGoUp(temp.getFloorFrom()).add(temp);
				e.building.addUpRequest(temp.getFloorFrom());
			} else {
				e.building.getPeopleWaitingToGoDown(temp.getFloorFrom()).add(temp);
				e.building.addDownRequest(temp.getFloorFrom());
			}
		}
	}

	// This happens when the start button is pressed
	public void update(ActionEvent ev) { // TODO: call animation from start button
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Button pressed: Starting....");
				theElevator.systemStartingTime = System.currentTimeMillis();
				update(Cmd.start);
			}
		});
		th.start();
	}

	// ELEVATOR CONTROL
	public void moveUp() { // moves the elevator up a set number of pixels.
		updateLevel(true, moveElevatorNextLevel);
	}

	public void moveDown() { // moves the elevator down the same number of pixels
		updateLevel(false, moveElevatorNextLevel);
	}

	public void openDoors() { // changes the color of the elevator from blue to red
		updateColor(Color.BLUE, Color.RED);
		// elevator.setFill(Color.BLUE); //you can do this, if you don't wanna animation color
		// transition
	}

	public void closeDoors() { // changes the color of the elevator from red to blue
		updateColor(Color.RED, Color.BLUE);
	}

	// PEOPLE CONTROL
	public void PassengerRequestEnterElevator() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				numberOfPassengerRequestsInside++;
				peopleCount.setText(Integer.toString(numberOfPassengerRequestsInside));
			}
		});
	}

	public void PassengerRequestLeaveElevator() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				numberOfPassengerRequestsInside--;
				peopleCount.setText(Integer.toString(numberOfPassengerRequestsInside));
			}
		});
	}

	public void updateBuildingText(Building b) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (int i = 1; i <= peopleWaitingText.length; i++) {
					int up = b.getPeopleWaitingToGoUp(i).size();
					int down = b.getPeopleWaitingToGoDown(i).size();
					String req = b.getRequests(i).requestString();
					peopleWaitingText[i-1].setText("Floor: " + Integer.toString(i) + "   Up: " + Integer.toString(up)
							+ "   Down: " + Integer.toString(down) + "   Request: " + req);
				}
			}
		});
	}

	// TEXT CONTROL
	public void setText(String str) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// genericText.setText(str);
				peopleCount.setText(str);
			}
		});
	}

	// ANIMATION AND THREAD CONTROL
	public void waitFor() { // runs an animation which does nothing but wait for a certain amount of
							// time
		try {
			Thread.sleep(WAITING_TIME);
			update(Cmd.wait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param up
	 *            define the direction
	 * @param level
	 *            define number or pixels of animation
	 */
	private void updateLevel(boolean up, int level) {
		if (up) {
			if (theElevator.verbose) {
				System.out.println("moving up now");
			}

		} else {
			if (theElevator.verbose) {
				System.out.println("Moving Down Now...");
			}
		}

		Timeline timeline = new Timeline();
		timeline.setCycleCount(1);
		int levelTo = 0;
		Cmd command = Cmd.down;
		if (up) {
			command = Cmd.up;
			levelTo = (int) (elevatorPane.getLayoutY() - level);
		} else {
			levelTo = (int) (elevatorPane.getLayoutY() + level);
		}
		KeyValue kv = new KeyValue(elevatorPane.layoutYProperty(), levelTo);
		KeyFrame kf = new KeyFrame(Duration.millis(MOVE_ONE_FLOOR_TIME), kv);
		timeline.getKeyFrames().add(kf);

		playAnimationAndWaitForFinish(timeline, command);
	}

	/**
	 * @param from
	 *            Color initial
	 * @param to
	 *            Color for transition ends
	 */
	private void updateColor(Color from, Color to) {
		FillTransition ft = new FillTransition(Duration.millis(OPEN_DOOR_TIME), elevator, from, to);
		ft.setCycleCount(1);
		Cmd command = Cmd.open;
		if (from == Color.RED) {
			command = Cmd.close;
		}
		playAnimationAndWaitForFinish(ft, command);
	}

	/**
	 * Method responsible for manage animation timeline and events. It will just leave the method
	 * when animation finish.
	 *
	 * @param animation
	 */
	private synchronized void playAnimationAndWaitForFinish(final Animation animation, final Cmd command) {
		if (Platform.isFxApplicationThread()) {
			throw new IllegalThreadStateException("Cannot be executed on main JavaFX thread");
		}
		final Thread currentThread = Thread.currentThread();
		final EventHandler<ActionEvent> originalOnFinished = animation.getOnFinished();
		animation.setOnFinished(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (originalOnFinished != null) {
					originalOnFinished.handle(event);
				}
				synchronized (currentThread) {
					currentThread.notify();
					update(command);
				}
			}
		});
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				animation.play();
			}
		});
		synchronized (currentThread) {
			try {
				currentThread.wait();
			} catch (InterruptedException ex) {
				ex.getMessage();
			}
		}
	}

	/**
	 * Called by Main.class
	 *
	 * @param stage
	 */
	public void setStage(Stage stage) {
		stage.setWidth(WINDOW_WIDTH);
		stage.setHeight(WINDOW_HEIGHT);
	}
}
