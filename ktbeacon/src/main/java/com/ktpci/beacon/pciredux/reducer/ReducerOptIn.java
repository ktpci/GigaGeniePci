package com.ktpci.beacon.pciredux.reducer;

import androidx.annotation.NonNull;

import com.ktpci.beacon.pciredux.action.ActionOptIn;
import com.ktpci.beacon.pciredux.core.Action;
import com.ktpci.beacon.pciredux.core.PCIReducer;
import com.ktpci.beacon.pciredux.state.PCIState;
import com.ktpci.beacon.pciredux.state.PCIState3Active;

public class ReducerOptIn implements PCIReducer {

    @NonNull
    @Override
    public PCIState reduce(@NonNull PCIState currentState, @NonNull Action action) {

        ActionOptIn actionOptIn = (ActionOptIn) action;

        switch (currentState.getType()) {
            case DEFAULT:
                break;
            case INACTIVE:
                break;
            case ACTIVE:
                return currentState;
        }
        /** Mic Agree 필수 부분 제거 by dalkommjk | 2020-03-16
         if (!currentState.isMicUseAgreed()) {
         PCILog.d("  optIn failure due to isMicUseAgreed=False: Microphone use is not agreed");
         return currentState;
         }
         */

        PCIState3Active newState = new PCIState3Active(currentState);
        newState.setOptIn(true);
        return newState;
    }

}