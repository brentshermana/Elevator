import java.sql.Time;

public class PassengerReleased {
	private PassengerRequest passengerRequest;
	private Time timeArrived;  // time when the passenger was arrived

	public PassengerRequest getPassengerRequest() {
		return passengerRequest;
	}
	public void setPassengerRequest(PassengerRequest passengerRequest) {
		this.passengerRequest = passengerRequest;
	}

	public Time getTimeArrived() {
		return timeArrived;
	}
	public void setTimeArrived(Time timeArrived) {
		this.timeArrived = timeArrived;
	}

	public PassengerReleased(PassengerRequest passengerRequest, Time timeArrived) {

		this.passengerRequest = passengerRequest;
		this.timeArrived = timeArrived;
	}

	public String toString () {
		StringBuffer sb = new StringBuffer();
		sb.append(passengerRequest.getFloorFrom());
		sb.append("->");
		sb.append(passengerRequest.getFloorTo());
		sb.append(" ");
		sb.append(passengerRequest.getTimePressedButton());
		sb.append(" ");
		sb.append(timeArrived);
		return sb.toString();
	}
}
