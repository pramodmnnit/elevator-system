package com.elevator;

import java.util.logging.Logger;
import java.util.logging.Level;

public class StandardElevatorFactory implements ElevatorFactory {
    private static final Logger logger = Logger.getLogger(StandardElevatorFactory.class.getName());

    @Override
    public Elevator createElevator(int minFloor, int maxFloor) {
        logger.log(Level.INFO, "Creating standard elevator with floor range {0} to {1}", 
            new Object[]{minFloor, maxFloor});
        return new Elevator(minFloor, maxFloor);
    }
} 