import java.util.LinkedList;
import java.util.Iterator;
import java.sql.Time;

public class SmartBrain extends Brain {
    private int weightBuffer = Params.maxWeight;

    private Time timeBeganWaiting;

    public SmartBrain () {
        goingUp = true;
        nextMovementCmd = null;
    }
    @Override
    public Cmd decide (Cmd prevCommand, ElevatorInfo theElevator) {

        updateGoingUp(theElevator);

        theElevator.printStatus();

        if (!theElevator.continueOperate()) {
            theElevator.finished();
			return Cmd.finish;
        }

        if (noRequests(theElevator)) {
            return Cmd.wait;
        }

        //if thedoors are open, close them
        if (prevCommand == Cmd.open) {
            return Cmd.close;
        }
        //we've already made a decision regarding where to go
        else if (nextMovementCmd != null) {
            Cmd temp = nextMovementCmd;
            nextMovementCmd = null;
            return temp;
        }

        //ensures doors are opened if desired
        if (shouldOpenDoors(theElevator, prevCommand)) {
            nextMovementCmd = decideNextMovementCommand(theElevator);
            return Cmd.open;
        }

        return goingUp ? Cmd.up : Cmd.down;
    }
    @Override
    public Cmd decideNextMovementCommand (ElevatorInfo theElevator) {
        return goingUp ? Cmd.up : Cmd.down;
    }
    @Override
    public boolean shouldOpenDoors (ElevatorInfo theElevator, Cmd prevCommand){
        int theFloor = theElevator.currentFloor;
        if (prevCommand == Cmd.close || prevCommand == Cmd.open) {
            //if we previously opened the doors but there
            //are still people waiting, we are at capacity.
            //to continue opening the doors would cause an infinite loop
            return false;
        }
        if (theElevator.building.hasUpRequest(theFloor) && goingUp && !nearMaxCapacity(theElevator)) {
            return true;
        } else if (theElevator.building.hasDownRequest(theFloor) && !goingUp && !nearMaxCapacity(theElevator)) {
            return true;
        } else if (theElevator.buttonsPressed.contains(theFloor)) {
            return true;
        }
        return false;
    }
    //called each frame to modify 'goingUp' boolean
    private void updateGoingUp (ElevatorInfo theElevator) {
        int theFloor = theElevator.currentFloor;
        if (goingUp) {
            if (theElevator.atMaxFloor() || noRequestsAboveThisFloor(theElevator)) {
                goingUp = false;
            }
        } else {
            if (theElevator.atGroundFloor() || noRequestsBelowThisFloor(theElevator)) {
                goingUp = true;
            }
        }
    }
    //DONE
    private boolean noRequestsAboveThisFloor (ElevatorInfo theElevator) {
        Building b = theElevator.building;
        //check building's buttons
        if (b.hasUpRequest(theElevator.currentFloor)) {
            return false;
        }
        for (int i = theElevator.currentFloor + 1; i <= theElevator.floors; i++) {
            if (b.hasRequest(i)) {
                return false;
            }
        }
        //check elevator shaft's buttons
        Iterator<Integer> buttonIterator = theElevator.buttonsPressed.iterator();
        while (buttonIterator.hasNext()) {
            int theButton = buttonIterator.next();
            if (theButton > theElevator.currentFloor) {
                return false;
            }
        }
        return true;
    }
    private boolean noRequestsBelowThisFloor (ElevatorInfo theElevator) {
        Building b = theElevator.building;
        //check building's buttons
        if (b.hasDownRequest(theElevator.currentFloor)) {
            return false;
        }
        for (int i = theElevator.currentFloor - 1; i >= 1; i--) {
            if (b.hasRequest(i)) {
                return false;
            }
        }
        //check elevator shaft's buttons
        Iterator<Integer> buttonIterator = theElevator.buttonsPressed.iterator();
        while (buttonIterator.hasNext()) {
            int theButton = buttonIterator.next();
            if (theButton < theElevator.currentFloor) {
                return false;
            }
        }
        return true;
    }
    private boolean nearMaxCapacity (ElevatorInfo e) {
        return (e.currentWeight >= e.capacity - (3/4 * Params.maxWeight));
    }
    public boolean noRequests (ElevatorInfo theElevator) {
        Building b = theElevator.building;
        if (!theElevator.buttonsPressed.isEmpty()) {
            return false;
        }
        for (int i = 1; i <= theElevator.floors; i++) {
            if (b.hasRequest(i)) {
                return false;
            }
        }
        return true;
    }
}
