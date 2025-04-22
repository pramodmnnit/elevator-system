package com.elevator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ElevatorController {
    private static final Logger logger = Logger.getLogger(ElevatorController.class.getName());
    private List<Elevator> elevators;
    private ExecutorService executorService;
    private final Object lock = new Object();

    public ElevatorController(int numElevators, int minFloor, int maxFloor) {
        logger.log(Level.INFO, "Creating elevator controller with {0} elevators", numElevators);
        this.elevators = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(numElevators);
        
        for (int i = 0; i < numElevators; i++) {
            Elevator elevator = new Elevator(minFloor, maxFloor);
            elevators.add(elevator);
            executorService.submit(elevator);
            logger.log(Level.INFO, "Elevator {0} started", i + 1);
        }
    }

    public void requestElevator(Request request) {
        synchronized (lock) {
            logger.log(Level.INFO, "Processing new request: Floor {0} -> Floor {1}", 
                new Object[]{request.getSourceFloor(), request.getDestinationFloor()});
            Elevator bestElevator = findBestElevator(request);
            if (bestElevator != null) {
                logger.log(Level.INFO, "Assigned request to elevator at floor {0}", 
                    bestElevator.getCurrentFloor());
                bestElevator.addDestination(request.getSourceFloor());
                bestElevator.addDestination(request.getDestinationFloor());
            } else {
                logger.warning("No available elevator found for the request");
            }
        }
    }

    private Elevator findBestElevator(Request request) {
        Elevator bestElevator = null;
        int minCost = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            if (elevator.getState() == ElevatorState.MAINTENANCE) {
                continue;
            }

            int cost = calculateCost(elevator, request);
            if (cost < minCost) {
                bestElevator = elevator;
                minCost = cost;
            }
        }

        return bestElevator;
    }

    private int calculateCost(Elevator elevator, Request request) {
        int currentFloor = elevator.getCurrentFloor();
        Direction currentDirection = elevator.getDirection();
        int sourceFloor = request.getSourceFloor();

        // Base cost is the distance to source floor
        int cost = Math.abs(currentFloor - sourceFloor);

        // If elevator is idle, that's all we need
        if (currentDirection == Direction.IDLE) {
            return cost;
        }

        // If elevator is moving and has destinations, add a large penalty
        if (!elevator.getDestinationFloors().isEmpty()) {
            cost += 10000;
        }

        return cost;
    }

    public void shutdown() {
        synchronized (lock) {
            logger.info("Shutting down elevator controller");
            for (Elevator elevator : elevators) {
                elevator.shutdown();
            }
            executorService.shutdown();
            logger.info("All elevators and executor service shut down");
        }
    }

    public List<Elevator> getElevators() {
        synchronized (lock) {
            return new ArrayList<>(elevators);
        }
    }
} 