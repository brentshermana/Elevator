import java.sql.Time;
import java.util.Random;
import java.util.Arrays;

public class PassengerRequest {

	private Time time_pressed_button;  // time when the button was pressed
	private int floor_from; // the floor from which the elevator was called
	private int floor_to; // the floor where the passenger is headed to
	private int weight; // weight, in pounds

	public PassengerRequest () {

	}
	public PassengerRequest (Time pressedButton, int from, int to, int weight) {
		setWeight(weight);
		setFloorTo(to);
		setFloorFrom(from);
		setTimePressedButton(pressedButton);
	}

	public Time getTimePressedButton() {
		return time_pressed_button;
	}
	public void setTimePressedButton(Time time_pressed_button) {
		this.time_pressed_button = time_pressed_button;
	}
	public int getFloorFrom() {
		return floor_from;
	}
	public void setFloorFrom(int floor_from) {
		this.floor_from = floor_from;
	}
	public int getFloorTo() {
		return floor_to;
	}
	public void setFloorTo(int floor_to) {
		this.floor_to = floor_to;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}


	/*
	added for testing purposes, will generate a random sample of requests
	*/

	public static Queue<PassengerRequest> generateSample (Time timeFrom) throws IllegalStateException {
		if (Params.floors < 2) {
			//because floors from and to cannot be equal
			throw new IllegalStateException();
		} else if (Params.testDuration < 0) {
			//pretty self-explainatory
			throw new IllegalStateException();
		}


		Time[] theTimes = TimeManip.generateTimes(timeFrom, Params.testDuration, Params.peopleInTest, Params.sampleSeed);
		Arrays.sort(theTimes); // sort from low to high (aka first to last)

		Random r = new Random (Params.sampleSeed);

		int[] weights = new int[Params.peopleInTest];
		for (int i = 0; i < weights.length; i++) {
			int w = r.nextInt(Params.maxWeight - Params.minWeight);
			w += Params.minWeight;
			weights[i] = w;
		}

		int[] floorsFrom = new int[Params.peopleInTest];
		for (int i = 0; i < floorsFrom.length; i++) {
			floorsFrom[i] = r.nextInt(Params.floors) + 1;
		}

		int[] floorsTo = new int[Params.peopleInTest];
		for (int i = 0; i < floorsTo.length; i++) {
			int temp = r.nextInt(Params.floors) + 1;
			//ensure 'from' and 'to' are not the same
			if (temp == floorsFrom[i]) {
				temp++;
				if (temp > Params.floors) {
					temp = 1;
				}
			}
			floorsTo[i] = temp;
		}

		//finally, generate the queue
		Queue<PassengerRequest> theQueue = new Queue<PassengerRequest>();
		for (int i = 0; i < Params.peopleInTest; i++) {
			theQueue.add(new PassengerRequest(theTimes[i], floorsFrom[i], floorsTo[i], weights[i]));
		}
		return theQueue;
	}

}
