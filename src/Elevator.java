import java.util.LinkedList;
import java.util.Queue;

import Enums.Direction;
import Enums.Colors;
class Elevator {
    private Queue<Passenger> passengerQueue; // Queue of passengers on the elevator
    private Queue<Passenger> passengerCallQueue; // Queue of passenger calls
    private int capacity; // The maximum number of passengers the elevator can hold

    // Constructor to initialize the elevator's state
    public Elevator(int elevatorCapacity) {
        passengerQueue = new LinkedList<>();
        passengerCallQueue = new LinkedList<>();
        capacity = elevatorCapacity;
    }

    public boolean isFull() {
        return passengerQueue.size() >= capacity;
    }

    public void addPassenger(Passenger passenger) {
        if (!isFull()) {
            passengerQueue.offer(passenger);
        } else {
            System.out.println("Elevator is at full capacity. Cannot add more passengers.");
        }
    }

    public void addPassengerCall(Passenger passenger) {
        passengerCallQueue.offer(passenger);
    }

    public void dropOffPassengers(int floor) {
        int[] passengersToDropOff = { 0 };
        passengerQueue.removeIf(passenger -> {
            if (passenger.getDestinationFloor() == floor) {
                passengersToDropOff[0]++;
                return true;
            }
            return false;
        });

        if (passengersToDropOff[0] > 0) {
            System.out.println(Colors.ANSI_GREEN + passengersToDropOff[0] + " passenger(s) dropped off at floor " + floor + ". " + Colors.ANSI_RESET);
        }
    }

    public void pickUpPassengers(int floor) {
        if (passengerCallQueue == null || passengerCallQueue.isEmpty()) {
            return;
        }

        int passengersPickedUp = 0;
        Queue<Passenger> remainingCalls = new LinkedList<>();

        while (!passengerCallQueue.isEmpty() && !isFull()) {
            Passenger passenger = passengerCallQueue.poll();
            if (passenger.getStartFloor() == floor) {
                addPassenger(passenger);
                passengersPickedUp++;
            } else {
                remainingCalls.offer(passenger);
            }
        }

        // Add back passengers that weren't picked up
        while (!remainingCalls.isEmpty()) {
            passengerCallQueue.offer(remainingCalls.poll());
        }

        if (passengersPickedUp > 0) {
            System.out.println(Colors.ANSI_GREEN + passengersPickedUp + " passenger(s) picked up at floor " + floor + ". " + Colors.ANSI_RESET);
        }
    }

    public int getPassengerCount() {
        return passengerQueue.size();
    }

    public int getPassengerCallCount() {
        return passengerCallQueue.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public int getNextRequestedFloor(int currentFloor, Direction direction) {
        int nextDropFloor = -1;
        int nextCallFloor = -1;

        if (passengerQueue.isEmpty() && passengerCallQueue.isEmpty()) {
            return -1;
        }

        nextDropFloor = passengerQueue.stream()
                .mapToInt(Passenger::getDestinationFloor)
                .filter(floor -> direction == Direction.UP ? floor > currentFloor : floor < currentFloor)
                .reduce(direction == Direction.UP ? Integer::min : Integer::max)
                .orElse(-1);

        nextCallFloor = passengerCallQueue.stream()
                .mapToInt(Passenger::getStartFloor)
                .filter(floor -> direction == Direction.UP ? floor > currentFloor : floor < currentFloor)
                .reduce(direction == Direction.UP ? Integer::min : Integer::max)
                .orElse(-1);

        if (direction == Direction.UP) {
            if (nextDropFloor == -1)
                return nextCallFloor;
            if (nextCallFloor == -1)
                return nextDropFloor;
            return Math.min(nextDropFloor, nextCallFloor);
        } else {
            if (nextDropFloor == -1)
                return nextCallFloor;
            if (nextCallFloor == -1)
                return nextDropFloor;
            return Math.max(nextDropFloor, nextCallFloor);
        }
    }
}