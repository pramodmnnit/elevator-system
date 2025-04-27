package com.elevator;

public interface ElevatorFactory {
    /**
     * Creates a new elevator with the specified floor range
     * @param minFloor The minimum floor the elevator can reach
     * @param maxFloor The maximum floor the elevator can reach
     * @return A new elevator instance
     */
    Elevator createElevator(int minFloor, int maxFloor);
} 