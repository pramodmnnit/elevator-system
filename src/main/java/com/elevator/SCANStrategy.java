package com.elevator;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class SCANStrategy implements SchedulingStrategy {
    private static final Logger logger = Logger.getLogger(SCANStrategy.class.getName());

    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        logger.log(Level.INFO, "Using SCAN strategy to select elevator for request: Floor {0} -> Floor {1}",
            new Object[]{request.getSourceFloor(), request.getDestinationFloor()});

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

        if (bestElevator != null) {
            logger.log(Level.INFO, "Selected elevator at floor {0} with cost {1}",
                new Object[]{bestElevator.getCurrentFloor(), minCost});
        } else {
            logger.warning("No available elevator found for the request");
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

        // If elevator is moving in the same direction as the request
        if ((currentDirection == Direction.UP && sourceFloor > currentFloor) ||
            (currentDirection == Direction.DOWN && sourceFloor < currentFloor)) {
            return cost;
        }

        // If elevator is moving in opposite direction, add a large penalty
        return cost + 10000;
    }
} 