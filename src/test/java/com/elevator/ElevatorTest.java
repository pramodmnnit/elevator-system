package com.elevator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ElevatorTest {
    private Elevator elevator;

    @BeforeEach
    void setUp() {
        elevator = new Elevator(0, 10);
    }

    @Test
    void testInitialState() {
        assertEquals(0, elevator.getCurrentFloor());
        assertEquals(Direction.IDLE, elevator.getDirection());
        assertEquals(ElevatorState.STOPPED, elevator.getState());
        assertTrue(elevator.getDestinationFloors().isEmpty());
    }

    @Test
    void testAddDestination() {
        elevator.addDestination(5);
        assertEquals(1, elevator.getDestinationFloors().size());
        assertTrue(elevator.getDestinationFloors().contains(5));
        assertEquals(Direction.UP, elevator.getDirection());
    }

    @Test
    void testMoveUp() {
        elevator.addDestination(3);
        elevator.move();
        assertEquals(1, elevator.getCurrentFloor());
        assertEquals(ElevatorState.MOVING, elevator.getState());
    }

    @Test
    void testMoveDown() {
        // First move up to floor 3
        elevator.addDestination(3);
        for (int i = 0; i < 3; i++) {
            elevator.move();
        }
        // Ensure elevator has stopped at floor 3
        assertEquals(3, elevator.getCurrentFloor());
        assertEquals(ElevatorState.STOPPED, elevator.getState());
        
        // Now test moving down one floor
        elevator.addDestination(1);
        elevator.move();
        assertEquals(2, elevator.getCurrentFloor());
        assertEquals(Direction.DOWN, elevator.getDirection());
        assertEquals(ElevatorState.MOVING, elevator.getState());
    }

    @Test
    void testInvalidDestination() {
        elevator.addDestination(11); // Beyond max floor
        assertTrue(elevator.getDestinationFloors().isEmpty());
        
        elevator.addDestination(-1); // Below min floor
        assertTrue(elevator.getDestinationFloors().isEmpty());
    }
} 