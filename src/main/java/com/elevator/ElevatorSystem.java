package com.elevator;

import java.util.logging.Logger;
import java.util.logging.Level;

public class ElevatorSystem {
    private static final Logger logger = Logger.getLogger(ElevatorSystem.class.getName());
    private ElevatorController controller;

    public ElevatorSystem(int numElevators, int minFloor, int maxFloor) {
        logger.log(Level.INFO, "Initializing elevator system with {0} elevators, floors {1} to {2}", 
            new Object[]{numElevators, minFloor, maxFloor});
        this.controller = new ElevatorController(numElevators, minFloor, maxFloor);
    }

    public void requestElevator(int sourceFloor, int destinationFloor) {
        logger.log(Level.INFO, "New elevator request: Floor {0} -> Floor {1}", 
            new Object[]{sourceFloor, destinationFloor});
        Request request = new Request(sourceFloor, destinationFloor);
        controller.requestElevator(request);
    }

    public void shutdown() {
        logger.info("Shutting down elevator system");
        controller.shutdown();
    }

    public static void main(String[] args) {
        logger.info("Starting elevator system simulation");
        
        // Create an elevator controller with 2 elevators, floors 0-10
        ElevatorSystem system = new ElevatorSystem(2, 0, 10);

        // Example requests
        system.requestElevator(0, 5);
        system.requestElevator(2, 7);
        system.requestElevator(3, 1);

        logger.info("All requests submitted. System will run for 10 seconds...");

        // Let the system run for a while
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "System interrupted", e);
            Thread.currentThread().interrupt();
        }

        // Shutdown the system
        system.shutdown();
        logger.info("Elevator system simulation completed");
    }
} 