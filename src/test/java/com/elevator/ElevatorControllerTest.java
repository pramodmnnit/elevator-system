package com.elevator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ElevatorControllerTest {
    private ElevatorController controller;
    private static final int NUM_ELEVATORS = 2;
    private static final int MIN_FLOOR = 0;
    private static final int MAX_FLOOR = 10;

    @BeforeEach
    void setUp() {
        controller = new ElevatorController(NUM_ELEVATORS, MIN_FLOOR, MAX_FLOOR);
    }

    @AfterEach
    void tearDown() {
        controller.shutdown();
    }

    @Test
    void testInitialization() {
        List<Elevator> elevators = controller.getElevators();
        assertEquals(NUM_ELEVATORS, elevators.size());
        
        for (Elevator elevator : elevators) {
            assertEquals(MIN_FLOOR, elevator.getCurrentFloor());
            assertEquals(Direction.IDLE, elevator.getDirection());
            assertEquals(ElevatorState.STOPPED, elevator.getState());
            assertTrue(elevator.getDestinationFloors().isEmpty());
        }
    }

    @Test
    void testRequestElevator() throws InterruptedException {
        // Request elevator from floor 0 to floor 5
        Request request = new Request(0, 5);
        controller.requestElevator(request);

        // Wait for elevator to process request
        Thread.sleep(2000);

        List<Elevator> elevators = controller.getElevators();
        boolean requestProcessed = false;
        
        for (Elevator elevator : elevators) {
            List<Integer> destinations = elevator.getDestinationFloors();
            if (!destinations.isEmpty()) {
                requestProcessed = true;
                assertTrue(destinations.contains(0) || destinations.contains(5));
                break;
            }
        }
        
        assertTrue(requestProcessed, "No elevator processed the request");
    }

    @Test
    void testMultipleRequests() throws InterruptedException {
        // Request 1: floor 0 to floor 5
        Request request1 = new Request(0, 5);
        controller.requestElevator(request1);

        // Request 2: floor 3 to floor 8
        Request request2 = new Request(3, 8);
        controller.requestElevator(request2);

        // Wait for elevators to process requests
        Thread.sleep(2000);

        List<Elevator> elevators = controller.getElevators();
        int requestsProcessed = 0;
        
        for (Elevator elevator : elevators) {
            List<Integer> destinations = elevator.getDestinationFloors();
            if (!destinations.isEmpty()) {
                requestsProcessed++;
                assertTrue(destinations.contains(0) || destinations.contains(3) || 
                         destinations.contains(5) || destinations.contains(8));
            }
        }
        
        assertTrue(requestsProcessed > 0, "No requests were processed");
    }

    @Test
    void testShutdown() {
        controller.shutdown();
        List<Elevator> elevators = controller.getElevators();
        
        for (Elevator elevator : elevators) {
            assertTrue(elevator.getDestinationFloors().isEmpty());
        }
    }
} 