/*
*	This class will print out the results of the Elevator
*/
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.Time;

public class Print {
	@SuppressWarnings("deprecation")
	public static void print (ArrayList<PassengerReleased> released) {
		try {
			int previousFloor = 0;
			PrintWriter writer = new PrintWriter("results.txt", "UTF-8");
			writer.println("Floor at / floor from / floor to | Requested / arrived");
			for (int i = 0; i < released.size(); i++) {
				writer.println((previousFloor) + " / " +
					(released.get(i).getPassengerRequest().getFloorFrom()) + " / " +
					(released.get(i).getPassengerRequest().getFloorTo()) + " | " +
					released.get(i).getPassengerRequest().getTimePressedButton() + " / " +
					released.get(i).getTimeArrived());

				previousFloor = released.get(i).getPassengerRequest().getFloorTo();
			}

			int startSeconds = TimeManip.timeToSeconds(new Time(8, 0, 0));
			int finishSeconds = TimeManip.timeToSeconds(released.get(released.size() - 1).getTimeArrived());
			int seconds = finishSeconds - startSeconds;
			writer.println("Total cost (in seconds): " + Integer.toString(seconds));


			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported Encoding");
		}
	}
}
