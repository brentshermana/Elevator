import java.util.LinkedList;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class Brain {
    boolean goingUp;
    Cmd nextMovementCmd;

    public Brain() {

    }
    public Cmd decide (Cmd prevCommand, ElevatorInfo theElevator) {
        //to be overriden
        return null;
    }
    public Cmd decideNextMovementCommand (ElevatorInfo theElevator) {
        //to be overriden
        return null;
    }
    public boolean shouldOpenDoors (ElevatorInfo theElevator, Cmd prevCommand){
        //to be overriden
        return false;
    }
}
