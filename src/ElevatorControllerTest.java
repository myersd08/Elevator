import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import Enums.Direction;
import Enums.ElevatorSpeed;

public class ElevatorControllerTest {
    private ElevatorController controller;
    private static final int TOTAL_FLOORS = 10;

    private static final int CAPACITY = 8;

    @Before
    public void setUp() {
        controller = new ElevatorController(TOTAL_FLOORS, CAPACITY, ElevatorSpeed.FAST);
    }

    @Test
    public void testInitialState() {

        assertEquals(TOTAL_FLOORS, controller.getTopFloor());
        
        // Check all elevators start at ground floor
        assertEquals(1, controller.getCurrentFloor());
    }

    @Test
    public void testSinglePassenger() {
        // Call elevator from floor 5 going up
        controller.addPassenger(5);
        assertEquals(1, controller.getElevatorPassengerCount());

        controller.move();
        // Elevator should have moved to floor 5
        assertEquals(5, controller.getCurrentFloor());
        // Elevator should be going up
        assertEquals(Direction.UP, controller.getCurrentDirection());
        // Elevator should not be full
        assertEquals(false, controller.elevatorIsFull());
    }

    @Test
    public void testMultiPassenger() {
        // The elevator is currently on floor 5 going up
        controller.addPassenger(7);
        controller.addPassenger(9);
        controller.addPassenger(2);

        
        assertEquals(Direction.UP, controller.getCurrentDirection());
        assertEquals(3, controller.getElevatorPassengerCount());

        // The elevator should have moved up to floor 2
        controller.move();
        assertEquals(2, controller.getCurrentFloor());
        assertEquals(2, controller.getElevatorPassengerCount());

        // The elevator should have moved up to floor 7
        controller.move();
        assertEquals(7, controller.getCurrentFloor());
        assertEquals(Direction.UP, controller.getCurrentDirection());
        assertEquals(false, controller.elevatorIsFull());
        assertEquals(false, controller.elevatorIsEmpty());
        assertEquals(1, controller.getElevatorPassengerCount());

        // The elevator should have moved up to floor 9 and be empty
        controller.move();
        assertEquals(9, controller.getCurrentFloor());
        assertEquals(Direction.UP, controller.getCurrentDirection());
        assertEquals(false, controller.elevatorIsFull());
        assertEquals(true, controller.elevatorIsEmpty());
        assertEquals(0, controller.getElevatorPassengerCount());
    }

    @Test
    public void testElevatorCapacity() {
        // Fill elevator to capacity
        for (int i = 0; i < CAPACITY; i++) {
            controller.addPassenger(5);
        }
        
        assertTrue(controller.elevatorIsFull());
        
        // Try to add one more passenger
        controller.addPassenger(5);
        // Should still only have CAPACITY passengers
        assertTrue(controller.elevatorIsFull());
    }

    @Test
    public void testDirectionChange() {
        // Going up first
        controller.addPassenger(5);
        controller.move();
        assertEquals(Direction.UP, controller.getCurrentDirection());
        assertEquals(5, controller.getCurrentFloor());

        // Now going down
        controller.addPassenger(2);
        controller.move();
        assertEquals(Direction.DOWN, controller.getCurrentDirection());
        assertEquals(2, controller.getCurrentFloor());
    }

    @Test
    public void testInvalidFloorRequests() {
        // Test floor below minimum
        controller.addPassenger(0);
        assertEquals(1, controller.getCurrentFloor()); // Should stay at current floor

        // Test floor above maximum
        controller.addPassenger(TOTAL_FLOORS + 1);
        assertEquals(1, controller.getCurrentFloor()); // Should stay at current floor
    }

    @Test
    public void testSequentialMovement() {
        // Add passengers going to different floors
        controller.addPassenger(3);
        controller.addPassenger(7);
        controller.addPassenger(4);

        // Should visit floors in optimal order
        controller.move();
        assertEquals(3, controller.getCurrentFloor());

        controller.move();
        assertEquals(4, controller.getCurrentFloor());

        controller.move();
        assertEquals(7, controller.getCurrentFloor());
    }

    @Test
    public void testEmptyElevatorBehavior() {
        // Empty elevator should stay at current floor
        controller.move();
        assertEquals(1, controller.getCurrentFloor());
        assertEquals(Direction.UP, controller.getCurrentDirection());
    }

    @Test
    public void testElevatorSpeed() {
        // Create controllers with different speeds
        ElevatorController slowElevator = new ElevatorController(TOTAL_FLOORS, CAPACITY, ElevatorSpeed.SLOW);
        ElevatorController mediumElevator = new ElevatorController(TOTAL_FLOORS, CAPACITY, ElevatorSpeed.MEDIUM);
        ElevatorController fastElevator = new ElevatorController(TOTAL_FLOORS, CAPACITY, ElevatorSpeed.FAST);

        // Add same destination to all elevators
        slowElevator.addPassenger(5);
        mediumElevator.addPassenger(5);
        fastElevator.addPassenger(5);

        // Move all elevators
        slowElevator.move();
        mediumElevator.move();
        fastElevator.move();

        // Verify they all reached the destination
        assertEquals(5, slowElevator.getCurrentFloor());
        assertEquals(5, mediumElevator.getCurrentFloor());
        assertEquals(5, fastElevator.getCurrentFloor());
    }

    @Test
    public void testMultipleFloorRequests() {
        // Test handling of multiple requests to the same floor
        controller.addPassenger(5);
        controller.addPassenger(5);
        controller.addPassenger(5);

        controller.move();
        assertEquals(5, controller.getCurrentFloor());
        assertEquals(Direction.UP, controller.getCurrentDirection());
        assertEquals(false, controller.elevatorIsFull());
        assertEquals(true, controller.elevatorIsEmpty());
        assertEquals(0, controller.getElevatorPassengerCount());
    }

    @Test
    public void testElevatorIdling() {
        // Test that elevator stays idle when no requests
        assertEquals(1, controller.getCurrentFloor());
        controller.move();
        assertEquals(1, controller.getCurrentFloor());
        
        // Add passenger and complete their journey
        controller.addPassenger(5);
        controller.move();
        assertEquals(5, controller.getCurrentFloor());
        
        // Should remain at last served floor when idle
        controller.move();
        assertEquals(5, controller.getCurrentFloor());
    }

    @Test
    public void testPickupAndDropoffOrder() {
        // Test pickup and dropoff sequence
        controller.addPassenger(3); // First passenger going to 3
        controller.addPassenger(6); // Second passenger going to 6
        controller.addPassenger(2); // Third passenger going to 2
        
        controller.move();
        assertEquals(2, controller.getCurrentFloor());
        
        controller.move();
        assertEquals(3, controller.getCurrentFloor());
        
        controller.move();
        assertEquals(6, controller.getCurrentFloor());
        assertEquals(0, controller.getElevatorPassengerCount());
    }

    @Test
    public void testCapacityEdgeCases() {
        // Fill to capacity minus 1
        for (int i = 0; i < CAPACITY - 1; i++) {
            controller.addPassenger(5);
        }
        
        assertFalse(controller.elevatorIsFull());
        assertEquals(CAPACITY - 1, controller.getElevatorPassengerCount());
        
        // Add one more to reach capacity
        controller.addPassenger(5);
        assertTrue(controller.elevatorIsFull());
        assertEquals(CAPACITY, controller.getElevatorPassengerCount());
    }

    @Test
    public void testDirectionChangeWithMultiplePassengers() {
        // Test direction changes with multiple passengers
        controller.addPassenger(8); // Going up to 8
        controller.move();
        assertEquals(8, controller.getCurrentFloor());
        assertEquals(Direction.UP, controller.getCurrentDirection());
        
        controller.addPassenger(3); // Going down to 3
        controller.addPassenger(2); // Going down to 2
        controller.move();
        assertEquals(3, controller.getCurrentFloor());
        assertEquals(Direction.DOWN, controller.getCurrentDirection());
        
        controller.move();
        assertEquals(2, controller.getCurrentFloor());
    }

    @Test
    public void testBoundaryFloors() {
        // Test behavior at boundary floors
        controller.addPassenger(TOTAL_FLOORS); // Go to top floor
        controller.move();
        assertEquals(TOTAL_FLOORS, controller.getCurrentFloor());
        
        controller.addPassenger(1); // Go to bottom floor
        controller.move();
        assertEquals(1, controller.getCurrentFloor());
        assertEquals(Direction.DOWN, controller.getCurrentDirection());
    }
}
