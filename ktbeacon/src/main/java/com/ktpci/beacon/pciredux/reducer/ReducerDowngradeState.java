package com.ktpci.beacon.pciredux.reducer;

import androidx.annotation.NonNull;

import com.ktpci.beacon.pciredux.action.ActionDowngradeState;
import com.ktpci.beacon.pciredux.core.Action;
import com.ktpci.beacon.pciredux.core.PCIReducer;
import com.ktpci.beacon.pciredux.state.PCIState;
import com.ktpci.beacon.pciredux.state.PCIState1Default;
import com.ktpci.beacon.pciredux.state.PCIState2Inactive;

import static com.ktpci.beacon.pciredux.state.PCIState.Type.ACTIVE;
import static com.ktpci.beacon.pciredux.state.PCIState.Type.INACTIVE;


public class ReducerDowngradeState implements PCIReducer {

    @NonNull
    @Override
    public PCIState reduce(@NonNull PCIState currentState, @NonNull Action action) {

        ActionDowngradeState actionDowngradeState = (ActionDowngradeState) action;
        final PCIState.Type targetStateType = actionDowngradeState.getStateType();

        switch (targetStateType) {
            case DEFAULT:
                break;
            case INACTIVE:
                break;
            case ACTIVE:
                throw new IllegalStateException("Cannot downgrade to highest state type : " + targetStateType);
            default:
                throw new IllegalStateException("Invalid state type : " + targetStateType);
        }

        PCIState newState = null;
        // Opt out
        if (currentState.getType().getValue() >= ACTIVE.getValue() && targetStateType.getValue() < ACTIVE.getValue()) {
            newState = new PCIState2Inactive(currentState);
        }
        // Disagree terms
        if (currentState.getType().getValue() >= INACTIVE.getValue() && targetStateType.getValue() < INACTIVE.getValue()) {
            newState = new PCIState1Default(currentState);
            newState.setTermAgreed(false);
        }

        return newState;
    }

}
