import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import Enums.Direction;

public class ElevatorTest {
    private Elevator elevator;
    private final int CAPACITY = 5;

    @Before
    public void setUp() {
        elevator = new Elevator(CAPACITY);
    }

    @Test
    public void testInitialState() {
        assertEquals(0, elevator.getPassengerCount());
        assertEquals(CAPACITY, elevator.getCapacity());
        assertFalse(elevator.isFull());
    }

    @Test
    public void testAddPassenger() {
        Passenger passenger = new Passenger(1, 5);
        elevator.addPassenger(passenger);
        assertEquals(1, elevator.getPassengerCount());
    }

    @Test
    public void testAddPassengerWhenFull() {
        // Fill the elevator to capacity
        for (int i = 0; i < CAPACITY; i++) {
            elevator.addPassenger(new Passenger(1, 5));
        }
        assertTrue(elevator.isFull());
        
        // Try to add one more passenger
        elevator.addPassenger(new Passenger(1, 5));
        assertEquals(CAPACITY, elevator.getPassengerCount()); // Should not increase
    }

    @Test
    public void testDropOffPassengers() {
        // Add passengers going to different floors
        elevator.addPassenger(new Passenger(1, 5));
        elevator.addPassenger(new Passenger(1, 3));
        elevator.addPassenger(new Passenger(1, 5));
        
        elevator.dropOffPassengers(5);
        assertEquals(1, elevator.getPassengerCount()); // Only passenger going to floor 3 should remain
    }

    @Test
    public void testGetNextRequestedFloorGoingUp() {
        elevator.addPassenger(new Passenger(1, 5));
        elevator.addPassenger(new Passenger(1, 3));
        elevator.addPassenger(new Passenger(1, 7));
        
        assertEquals(3, elevator.getNextRequestedFloor(2, Direction.UP));
    }

    @Test
    public void testGetNextRequestedFloorGoingDown() {
        elevator.addPassenger(new Passenger(1, 5));
        elevator.addPassenger(new Passenger(1, 3));
        elevator.addPassenger(new Passenger(1, 1));
        
        assertEquals(3, elevator.getNextRequestedFloor(4, Direction.DOWN));
    }

    @Test
    public void testGetNextRequestedFloorNoValidFloor() {
        elevator.addPassenger(new Passenger(1, 5));
        elevator.addPassenger(new Passenger(1, 6));
        
        assertEquals(-1, elevator.getNextRequestedFloor(7, Direction.UP));
    }
} 