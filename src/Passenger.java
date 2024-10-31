import Enums.Direction;

class Passenger {
    private int destinationFloor; // The floor to which the passenger wants to go
    private int startFloor; // The floor from which the passenger is start from

    // Constructor to create a passenger with a specific destination floor
    public Passenger(int startFloor, int destinationFloor) {
        this.startFloor = startFloor;
        this.destinationFloor = destinationFloor;
    }

    // Get the destination floor of the passenger
    public int getDestinationFloor() {
        return destinationFloor;
    }

    public int getStartFloor() {
        return startFloor;
    }

    public Direction getDirection() {
        if (startFloor < destinationFloor) {
            return Direction.UP;
        } else {
            return Direction.DOWN;
        }
    }
}