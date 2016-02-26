import java.util.LinkedList;
import java.util.Iterator;
import java.sql.Time;

public class PatientSmartBrain extends Brain {
    private int weightBuffer = Params.maxWeight;

    //when this is set to true, the elevator will make a single pass
    //up, then down, then reset this to false
    private boolean waiting;
    //if at least this percentage of the floors have
    //some sort of request, stop waiting and serve them
    private double serveCondition;
    //if that percentage isn't reached after this many seconds,
    //serve them anyways
    private int maxWaitingTime; //in seconds
    private Time beganWaiting;
    private int timesChangedDirection;
    private int maxDirectionChanges;

    public PatientSmartBrain () {
        goingUp = true;
        nextMovementCmd = null;

        waiting = true;
        maxDirectionChanges = Params.maxDirectionalChanges;
        serveCondition = Params.serveCondition;
        maxWaitingTime = Params.timeToWait;
        beganWaiting = Params.testStartTime;
        timesChangedDirection = 0;
    }
    @Override
    public Cmd decide (Cmd prevCommand, ElevatorInfo theElevator) {
        //should always check end condition first
        if (!theElevator.continueOperate()) {
            theElevator.finished();
            return Cmd.finish;
        }

        //see if we should start/stop waiting
        if (waiting) {
            double percentile = getRequestPercentage(theElevator);
            int timeWaitedSoFar = TimeManip.difference(beganWaiting, theElevator.currentTime);
            if (percentile >= serveCondition) {
                endWaiting(theElevator);
                if (theElevator.verbose) {
                    System.out.println("\nBecause request percentile " + percentile + " is >= condition " + serveCondition + ", we are now serving\n");
                }
            } else if (timeWaitedSoFar >= maxWaitingTime) {
                endWaiting(theElevator);
                if (theElevator.verbose) {
                    System.out.println("\nBecause time waited " + timeWaitedSoFar + " is >= time to wait " + maxWaitingTime + ", we are now serving\n");
                }
            } else {
                if (theElevator.verbose) {
                    System.out.println("\nBecause request percentile " + percentile + " is < condition " + serveCondition);
                    System.out.println("\nAnd because time waited " + timeWaitedSoFar + " is < time to wait " + maxWaitingTime + ", we are still waiting\n");
                }
                return Cmd.wait;
            }
        } else {
            if (timesChangedDirection >= maxDirectionChanges) {
                if (theElevator.verbose) {
                    System.out.println("\nBecause we have changed direction " + timesChangedDirection + ", which is equal to max changes " + maxDirectionChanges + ", we are now waiting");
                }
                //negate the last change in direction that was made...
                //once we begin running again, if it still ought to have been pointing
                //in the direction that it is currently, updateGoingUp() will
                //handle that
                goingUp = !goingUp;
                beginWaiting(theElevator);
                return Cmd.wait;
            } else if (noRequests(theElevator)) {
                if (theElevator.verbose) {
                    System.out.println("\nBecause there are no requests in or outside the elevator, we are now waiting\n");
                }
                beginWaiting(theElevator);
                return Cmd.wait;
            }
        }

        updateGoingUp(theElevator);

        theElevator.printStatus();

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
                timesChangedDirection++;
            }
        } else {
            if (theElevator.atGroundFloor() || noRequestsBelowThisFloor(theElevator)) {
                goingUp = true;
                timesChangedDirection++;
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


    //note that, because we make the distinction between up and down requests,
    //the max value that can be returned is 2.0 aka 200%
    public double getRequestPercentage (ElevatorInfo theElevator) {
        Building b = theElevator.building;
        int totalRequests = 0;
        for (int i = 1; i <= theElevator.floors; i++) {
            if (b.hasUpRequest(i)) {
                totalRequests++;
            }
            if (b.hasDownRequest(i)) {
                totalRequests++;
            }
        }
        return (double)totalRequests / theElevator.floors;
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
    //these two methods manage the various data members associated with waiting
    //conditions, to simplify code elsewhere
    private void beginWaiting (ElevatorInfo theElevator) {
        waiting = true;
        beganWaiting = theElevator.currentTime;
    }
    private void endWaiting (ElevatorInfo theElevator) {
        waiting = false;
        updateGoingUp(theElevator);
        timesChangedDirection = 0;
    }
}
