import java.util.LinkedList;
import java.util.Arrays;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class DumbBrain extends Brain {

    public DumbBrain () {
        goingUp = true; //because we start on the bottom floor
        nextMovementCmd = null;
    }

    @Override
    public Cmd decide (Cmd prevCommand, ElevatorInfo theElevator) {

        theElevator.printStatus();

        //check if all persons have been served, if they have report and exit
        if (!theElevator.continueOperate()) {
            theElevator.finished();
			return Cmd.finish;
        }

        //modify 'goingUp' boolean, if needed:
        if (theElevator.atMaxFloor()) {
            goingUp = false;
        } else if (theElevator.atGroundFloor()) {
            goingUp = true;
        }

        //we don't need much 'thinking' if we should open or close the doors:
        if (shouldOpenDoors(theElevator, prevCommand)) {
            //because we'e about to open the doors, we need to decide what the next movement command is in advance:
            nextMovementCmd = decideNextMovementCommand(theElevator);
            //then, of course, open the doors
            return Cmd.open;
        } else if (prevCommand == Cmd.open) {
            return Cmd.close;
        } else if (prevCommand == Cmd.close) {
            //we will have a nextMovementCmd assigned, simply execute it
            Cmd temp = nextMovementCmd;
            nextMovementCmd = null;
            return temp;
        }

        //return move command:
        if (goingUp) {
            if (theElevator.verbose) {
                System.out.println("returning cmd up");
            }
            return Cmd.up;
        } else {
            if (theElevator.verbose) {
                System.out.println("returning cmd down");
            }
            return Cmd.down;
        }
    }

    //simple for this brain, but will get more sophisticated for
    //our completed product:
    @Override
    public Cmd decideNextMovementCommand (ElevatorInfo theElevator) {
        //return the nextMovement Cmd:
        //we know we're just going to keep going in whatever direction we're currently going
        if (goingUp) {
            return Cmd.up;
        } else {
            return Cmd.down;
        }
    }

    @Override
    public boolean shouldOpenDoors (ElevatorInfo theElevator, Cmd prevCommand) {
        int theFloor = theElevator.currentFloor;
        if (prevCommand == Cmd.close || prevCommand == Cmd.open) {
            //if we previously opened the doors but there
            //are still people waiting, we are at capacity.
            //to continue opening the doors would cause an infinite loop
            return false;
        }
        if (theElevator.building.hasUpRequest(theFloor) && goingUp) {
            return true;
        } else if (theElevator.building.hasDownRequest(theFloor) && !goingUp) {
            return true;
        } else if (theElevator.buttonsPressed.contains(theFloor)) {
            return true;
        }
        return false;
    }
}
