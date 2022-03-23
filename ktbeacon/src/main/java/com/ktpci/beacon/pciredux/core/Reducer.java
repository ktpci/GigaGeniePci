package com.ktpci.beacon.pciredux.core;

public interface Reducer {
    State reduce(State currentState, Action action);
}
