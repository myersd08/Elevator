/*
 * This class controls the elevator.  It keeps track of the current floor, direction, and total floors.
 * It also has methods to add passengers to the elevator and move the elevator
 */

import Enums.Direction;
import Enums.ElevatorSpeed;
import Enums.Colors;

class ElevatorController {
    private Elevator elevator; // The elevator instance being controlled
    private ElevatorSpeed speed;
    private int movingTime;
    private Direction currentDirection;
    private int topFloor;
    private int currentFloor;

    // Constructor to initialize the elevator controller with the number of floors
    public ElevatorController(int numberOfFloors, int elevatorCapacity, ElevatorSpeed elevatorSpeed) {
        elevator = new Elevator(elevatorCapacity);
        speed = elevatorSpeed;
        // Set moving time based on elevator speed
        switch (speed) {
            case SLOW:
                movingTime = 3000;
                break;
            case MEDIUM:
                movingTime = 1000;
                break;
            case FAST:
                movingTime = 500;
                break;
            default:
                movingTime = 500; // Default to medium speed
        }
        currentDirection = Direction.UP;
        topFloor = numberOfFloors;
        currentFloor = 1;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public int getTopFloor() {
        return topFloor;
    }

    public boolean elevatorIsFull() {
        return elevator.isFull();
    }

    public boolean elevatorIsEmpty() {
        return elevator.getPassengerCount() == 0;
    }

    public int getElevatorCapacity() {
        return elevator.getCapacity();
    }

    public int getElevatorPassengerCount() {
        return elevator.getPassengerCount();
    }

    // Add a passenger to the elevator.
    // If the elevator is full, or the destination floor is the same as the current
    // floor, it will not add the passenger.
    public void addPassenger(int destinationFloor) {
        if (elevator.isFull()) {
            return;
        }
        if (destinationFloor == currentFloor) {
            System.out.println(Colors.ANSI_YELLOW + "You are already on this floor, maybe just go to the room you are wanting to go to?" + Colors.ANSI_RESET);
            return;
        }
        if (destinationFloor > topFloor || destinationFloor < 1) {
            System.out.println(Colors.ANSI_RED + "That floor is not in this building." + Colors.ANSI_RESET);
            return;
        }

        Passenger passenger = new Passenger(currentFloor, destinationFloor);

        // order matters! if the elevator is empty, the first person gets to establish
        // the direction
        if (elevator.getPassengerCount() == 0) {
            currentDirection = passenger.getDirection();
        }
        elevator.addPassenger(passenger);

        if (elevator.isFull()) {
            System.out.println(Colors.ANSI_YELLOW + "Elevator is full, so we cannot add any more passengers." + Colors.ANSI_RESET);
            System.out.println(Colors.ANSI_YELLOW + "(Someone is probably pushing the close button that doesn't actually do anything.)" + Colors.ANSI_RESET);
            return;
        }
    }

    // Add a passenger call to the passenger call queue
    public void addPassengerCall(int fromFloor, int destinationFloor) {
        elevator.addPassengerCall(new Passenger(fromFloor, destinationFloor));
    }

    // Move the elevator to the next floor (if there are passengers to drop off)
    public void move() {
        if (elevator.getPassengerCount() == 0 && elevator.getPassengerCallCount() == 0) {
            System.out.println(Colors.ANSI_YELLOW + "Elevator is empty and there are no calls, so it is staying put." + Colors.ANSI_RESET);
            return;
        }

        int startFloor = currentFloor;
        int stopFloor = elevator.getNextRequestedFloor(currentFloor, currentDirection);

        // Handle direction change if no stops in current direction
        if (stopFloor == -1) {
            currentDirection = (currentDirection == Direction.UP) ? Direction.DOWN : Direction.UP;
            stopFloor = elevator.getNextRequestedFloor(currentFloor, currentDirection);

            // If still no valid stop floor, something is wrong
            if (stopFloor == -1) {
                System.out.println(Colors.ANSI_RED + "Error: No valid destination floors found." + Colors.ANSI_RESET);
                return;
            }
        }

        // Validate stop floor is within building bounds
        if (stopFloor < 1 || stopFloor > topFloor) {
            System.out.println(Colors.ANSI_RED + "Error: Invalid stop floor " + stopFloor + Colors.ANSI_RESET);
            return;
        }

        System.out.printf("Elevator moving %s from floor %d to floor %d%n%n",
                currentDirection, currentFloor, stopFloor);

        simulateMovement(startFloor, stopFloor);

        System.out.printf("\rElevator has arrived on floor %d%n", currentFloor);
        // Drop off passengers and update status
        elevator.dropOffPassengers(currentFloor);
        // Pick up passengers
        elevator.pickUpPassengers(currentFloor);
    }

    private void simulateMovement(int startFloor, int stopFloor) {
        while (currentFloor != stopFloor && currentFloor <= topFloor && currentFloor >= 1) {
            // Clear line and show current floor with carriage return
            System.out.printf("\rCurrent Floor: %d    ", currentFloor);

            int floorTime = getFloorTime(startFloor, stopFloor, currentFloor, movingTime);

            try {
                Thread.sleep(floorTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(Colors.ANSI_RED + "\nElevator movement interrupted!" + Colors.ANSI_RESET);
                return;
            }

            currentFloor += (currentDirection == Direction.UP) ? 1 : -1;
        }
    }

    // simulate a soft start and stop by progressively increasing the time per floor
    // (makes it slower)
    private int getFloorTime(int startFloor, int stopFloor, int currentFloor, int topSpeed) {
        // Define speed multipliers as constants
        final int START_STOP_MULTIPLIER = 4;
        final int ACCELERATION_MULTIPLIER = 2;

        // Check if elevator is at starting position
        if (currentFloor == startFloor) {
            return topSpeed * START_STOP_MULTIPLIER; // Initial acceleration
        }

        // Check if we're in deceleration or acceleration zones
        if (Math.abs(currentFloor - stopFloor) == 1) {
            return topSpeed * START_STOP_MULTIPLIER; // Final deceleration
        }

        if (Math.abs(currentFloor - startFloor) == 1 ||
                Math.abs(currentFloor - stopFloor) == 2) {
            return topSpeed * ACCELERATION_MULTIPLIER; // Acceleration/deceleration zones
        }

        return topSpeed; // Cruising speed
    }
}