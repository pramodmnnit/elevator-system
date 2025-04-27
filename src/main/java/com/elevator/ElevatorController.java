package com.elevator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ElevatorController {
    private static final Logger logger = Logger.getLogger(ElevatorController.class.getName());
    private static volatile ElevatorController instance; // Singleton instance

    private List<Elevator> elevators;
    private ExecutorService executorService;
    private final Object lock = new Object();
    private SchedulingStrategy schedulingStrategy;
    private ElevatorFactory elevatorFactory;

    // Make constructor private for Singleton
    private ElevatorController(int numElevators, int minFloor, int maxFloor) {
        logger.log(Level.INFO, "Creating elevator controller with {0} elevators", numElevators);
        this.elevators = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(numElevators);
        this.schedulingStrategy = new SCANStrategy(); // Default strategy
        this.elevatorFactory = new StandardElevatorFactory(); // Default factory
        
        for (int i = 0; i < numElevators; i++) {
            Elevator elevator = elevatorFactory.createElevator(minFloor, maxFloor);
            elevators.add(elevator);
            executorService.submit(elevator);
            logger.log(Level.INFO, "Elevator {0} started", i + 1);
        }
    }

    // Thread-safe Singleton accessor
    public static ElevatorController getInstance(int numElevators, int minFloor, int maxFloor) {
        if (instance == null) {
            synchronized (ElevatorController.class) {
                if (instance == null) {
                    instance = new ElevatorController(numElevators, minFloor, maxFloor);
                }
            }
        }
        return instance;
    }

    public void setSchedulingStrategy(SchedulingStrategy strategy) {
        synchronized (lock) {
            this.schedulingStrategy = strategy;
            logger.info("Scheduling strategy changed to: " + strategy.getClass().getSimpleName());
        }
    }

    public void setElevatorFactory(ElevatorFactory factory) {
        synchronized (lock) {
            this.elevatorFactory = factory;
            logger.info("Elevator factory changed to: " + factory.getClass().getSimpleName());
        }
    }

    public void requestElevator(Request request) {
        synchronized (lock) {
            logger.log(Level.INFO, "Processing new request: Floor {0} -> Floor {1}", 
                new Object[]{request.getSourceFloor(), request.getDestinationFloor()});
            
            Elevator bestElevator = schedulingStrategy.selectElevator(elevators, request);
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

    public void shutdown() {
        synchronized (lock) {
            logger.info("Shutting down elevator controller");
            for (Elevator elevator : elevators) {
                elevator.clearDestinations();
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