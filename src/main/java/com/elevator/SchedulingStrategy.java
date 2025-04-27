package com.elevator;

import java.util.List;

public interface SchedulingStrategy {
    /**
     * Selects the best elevator for a given request
     * @param elevators List of available elevators
     * @param request The elevator request
     * @return The selected elevator, or null if no suitable elevator is found
     */
    Elevator selectElevator(List<Elevator> elevators, Request request);
} 