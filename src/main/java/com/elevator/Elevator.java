package com.elevator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Elevator implements Runnable {
    private static final Logger logger = Logger.getLogger(Elevator.class.getName());
    private String id;
    private int currentFloor;
    private Direction direction;
    private ElevatorState state;
    private List<Integer> destinationFloors;
    private int maxFloor;
    private int minFloor;
    private final Object lock = new Object();
    private boolean running = true;

    public Elevator(int minFloor, int maxFloor) {
        this.id = UUID.randomUUID().toString();
        this.currentFloor = minFloor;
        this.direction = Direction.IDLE;
        this.state = ElevatorState.STOPPED;
        this.destinationFloors = new ArrayList<>();
        this.maxFloor = maxFloor;
        this.minFloor = minFloor;
        logger.log(Level.INFO, "Elevator {0} initialized at floor {1}", new Object[]{id, currentFloor});
    }

    public void addDestination(int floor) {
        synchronized (lock) {
            if (floor >= minFloor && floor <= maxFloor && !destinationFloors.contains(floor)) {
                destinationFloors.add(floor);
                logger.log(Level.INFO, "Elevator {0} added destination floor {1}", new Object[]{id, floor});
                updateDirection();
            } else {
                logger.log(Level.WARNING, "Invalid destination floor {0} for elevator {1}", 
                    new Object[]{floor, id});
            }
        }
    }

    private void updateDirection() {
        synchronized (lock) {
            if (destinationFloors.isEmpty()) {
                direction = Direction.IDLE;
                logger.log(Level.INFO, "Elevator {0} is now idle", id);
                return;
            }

            int nextFloor = destinationFloors.get(0);
            direction = nextFloor > currentFloor ? Direction.UP : Direction.DOWN;
            logger.log(Level.INFO, "Elevator {0} direction set to {1}", new Object[]{id, direction});
        }
    }

    public void move() {
        synchronized (lock) {
            if (destinationFloors.isEmpty()) {
                direction = Direction.IDLE;
                state = ElevatorState.STOPPED;
                return;
            }

            state = ElevatorState.MOVING;
            int nextFloor = destinationFloors.get(0);

            if (currentFloor == nextFloor) {
                stop();
                if (destinationFloors.isEmpty()) {
                    direction = Direction.IDLE;
                }
                return;
            }

            direction = nextFloor > currentFloor ? Direction.UP : Direction.DOWN;
            currentFloor = direction == Direction.UP ? currentFloor + 1 : currentFloor - 1;
            logger.log(Level.INFO, "Elevator {0} moved to floor {1}", new Object[]{id, currentFloor});

            if (destinationFloors.contains(currentFloor)) {
                stop();
                if (destinationFloors.isEmpty()) {
                    direction = Direction.IDLE;
                }
            }
        }
    }

    private void stop() {
        synchronized (lock) {
            state = ElevatorState.STOPPED;
            destinationFloors.remove(Integer.valueOf(currentFloor));
            logger.log(Level.INFO, "Elevator {0} stopped at floor {1}", new Object[]{id, currentFloor});
            openDoors();
            closeDoors();
            state = ElevatorState.STOPPED;
            updateDirection();
        }
    }

    private void openDoors() {
        state = ElevatorState.DOORS_OPEN;
        logger.log(Level.INFO, "Elevator {0} doors opening at floor {1}", new Object[]{id, currentFloor});
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void closeDoors() {
        state = ElevatorState.DOORS_CLOSED;
        logger.log(Level.INFO, "Elevator {0} doors closing at floor {1}", new Object[]{id, currentFloor});
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void clearDestinations() {
        synchronized (lock) {
            destinationFloors.clear();
            direction = Direction.IDLE;
            state = ElevatorState.STOPPED;
        }
    }

    public void shutdown() {
        synchronized (lock) {
            running = false;
            clearDestinations();
            logger.log(Level.INFO, "Elevator {0} shutting down", id);
        }
    }

    @Override
    public void run() {
        logger.log(Level.INFO, "Elevator {0} thread started", id);
        while (running) {
            synchronized (lock) {
                if (destinationFloors.isEmpty()) {
                    direction = Direction.IDLE;
                    state = ElevatorState.STOPPED;
                }
            }
            move();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Elevator {0} thread interrupted", id);
                Thread.currentThread().interrupt();
                break;
            }
        }
        logger.log(Level.INFO, "Elevator {0} thread stopped", id);
    }

    public int getCurrentFloor() {
        synchronized (lock) {
            return currentFloor;
        }
    }

    public Direction getDirection() {
        synchronized (lock) {
            return direction;
        }
    }

    public ElevatorState getState() {
        synchronized (lock) {
            return state;
        }
    }

    public List<Integer> getDestinationFloors() {
        synchronized (lock) {
            return new ArrayList<>(destinationFloors);
        }
    }
} 