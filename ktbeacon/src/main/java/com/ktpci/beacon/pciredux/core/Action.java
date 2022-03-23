package com.ktpci.beacon.pciredux.core;

import com.ktpci.beacon.pciredux.action.ActionType;

import java.util.HashMap;

public abstract class Action {
    private final ActionType type;
    private final HashMap<String, Object> payload;

    public Action(ActionType type, HashMap<String, Object> payload) {
        this.type = type;
        this.payload = payload;
    }

    public final ActionType getType() {
        return this.type;
    }

    public final HashMap<String, Object> getPayload() {
        return this.payload;
    }
}
