package com.ktpci.beacon.pciredux.reducer;


import androidx.annotation.NonNull;

import com.ktpci.beacon.pciredux.action.ActionAgreeTerms;
import com.ktpci.beacon.pciredux.core.Action;
import com.ktpci.beacon.pciredux.core.PCIReducer;
import com.ktpci.beacon.pciredux.state.PCIState;
import com.ktpci.beacon.pciredux.state.PCIState2Inactive;

public class ReducerAgreeTerms implements PCIReducer {

    @NonNull
    @Override
    public PCIState reduce(@NonNull PCIState currentState, @NonNull Action action) {

        ActionAgreeTerms actionAgreeTerms = (ActionAgreeTerms) action;

        PCIState2Inactive newState = new PCIState2Inactive(currentState);
        newState.setTermAgreed(true);
        newState.setAdid(actionAgreeTerms.getAdid());
        newState.setPartnerCode(actionAgreeTerms.getPartnerCode());
        newState.setPcimode(actionAgreeTerms.getPciMode());

        return newState;
    }
}
