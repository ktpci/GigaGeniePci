package com.ktpci.beacon.pciredux.reducer;

import androidx.annotation.NonNull;

import com.ktpci.beacon.pciredux.action.ActionOptOut;
import com.ktpci.beacon.pciredux.core.Action;
import com.ktpci.beacon.pciredux.core.PCIReducer;
import com.ktpci.beacon.pciredux.state.PCIState;
import com.ktpci.beacon.pciredux.state.PCIState2Inactive;

public class ReducerOptOut implements PCIReducer {

    @NonNull
    @Override
    public PCIState reduce(@NonNull PCIState currentState, @NonNull Action action) {

        ActionOptOut actionOptOut = (ActionOptOut) action;

        switch (currentState.getType()) {
            case DEFAULT : return currentState;
            case INACTIVE: return currentState;
            case ACTIVE  : break;
        }

        PCIState2Inactive newState = new PCIState2Inactive(currentState);
        newState.setOptIn(false);
        return newState;

    }
}
