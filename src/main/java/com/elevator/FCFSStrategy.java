package com.elevator;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FCFSStrategy implements SchedulingStrategy {
    private static final Logger logger = Logger.getLogger(FCFSStrategy.class.getName());

    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        logger.log(Level.INFO, "Using FCFS strategy to select elevator for request: Floor {0} -> Floor {1}",
            new Object[]{request.getSourceFloor(), request.getDestinationFloor()});

        // Find the first available elevator that's not in maintenance
        for (Elevator elevator : elevators) {
            if (elevator.getState() != ElevatorState.MAINTENANCE) {
                logger.log(Level.INFO, "Selected elevator at floor {0}", elevator.getCurrentFloor());
                return elevator;
            }
        }
        
        logger.warning("No available elevator found for the request");
        return null;
    }
} 