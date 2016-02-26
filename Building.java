import java.util.ArrayList;

//class represents everything going on outside the elevator chamber
public class Building {
    public enum ElevatorRequest { nothing, up, down, both }
    public ElevatorFloor[] requests;
    public ArrayList<Queue<PassengerRequest>> peopleWaitingToGoUp;
    public ArrayList<Queue<PassengerRequest>> peopleWaitingToGoDown;

    public Building (int floors) {
        requests = new ElevatorFloor[floors];
        peopleWaitingToGoUp = new ArrayList<Queue<PassengerRequest>>(floors);
        peopleWaitingToGoDown = new ArrayList<Queue<PassengerRequest>>(floors);
        for (int i = 0; i < floors; i++) {
            requests[i] = new ElevatorFloor();
            peopleWaitingToGoUp.add(new Queue<PassengerRequest>());
            peopleWaitingToGoDown.add(new Queue<PassengerRequest>());
        }
    }

    public ElevatorFloor getRequests (int floor) {
        return requests[floor-1];
    }

    public Queue<PassengerRequest> getPeopleWaitingToGoUp(int floor) {
        return peopleWaitingToGoUp.get(floor-1);
    }
    public Queue<PassengerRequest> getPeopleWaitingToGoDown(int floor) {
        return peopleWaitingToGoDown.get(floor-1);
    }

    public boolean hasRequest (int floor) {
        return requests[floor-1].hasRequest();
    }
    public boolean hasUpRequest (int floor) {
        return requests[floor-1].hasUpRequest();
    }
    public boolean hasDownRequest (int floor) {
        return requests[floor-1].hasDownRequest();
    }
    public void addUpRequest (int floor) {
        requests[floor-1].addUpRequest();
    }
    public void addDownRequest (int floor) {
        requests[floor-1].addDownRequest();
    }
    public void removeUpRequest (int floor) {
        requests[floor-1].removeUpRequest();
    }
    public void removeDownRequest (int floor) {
        requests[floor-1].removeDownRequest();
    }

    //internal classes
    public class ElevatorFloor {
        private ElevatorRequest request;

        public ElevatorFloor () {
            request = ElevatorRequest.nothing;
        }

        public String requestString() {
            return request.name();
        }

        //setter methods
        public void removeUpRequest () {
            if (request == ElevatorRequest.up) {
                request = ElevatorRequest.nothing;
            } else if (request == ElevatorRequest.both) {
                request = ElevatorRequest.down;
            }
        }
        public void removeDownRequest () {
            if (request == ElevatorRequest.down) {
                request = ElevatorRequest.nothing;
            } else if (request == ElevatorRequest.both) {
                request = ElevatorRequest.up;
            }
        }
        public void addUpRequest () {
            if (request == ElevatorRequest.down) {
                request = ElevatorRequest.both;
            } else if (request == ElevatorRequest.nothing) {
                request = ElevatorRequest.up;
            }
        }
        public void addDownRequest () {
            if (request == ElevatorRequest.up) {
                request = ElevatorRequest.both;
            } else if (request == ElevatorRequest.nothing) {
                request = ElevatorRequest.down;
            }
        }

        //booleans
        public boolean hasRequest () {
            if (request != ElevatorRequest.nothing) {
                return true;
            }
            return false;
        }
        public boolean hasUpRequest () {
            if (request == ElevatorRequest.up || request == ElevatorRequest.both) {
                return true;
            }
            return false;
        }
        public boolean hasDownRequest () {
            if (request == ElevatorRequest.down || request == ElevatorRequest.both) {
                return true;
            }
            return false;
        }
    }
}
