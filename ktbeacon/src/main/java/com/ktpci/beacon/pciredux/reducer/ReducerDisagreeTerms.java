package com.ktpci.beacon.pciredux.reducer;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ktpci.beacon.pciredux.action.ActionDisagreeTerms;
import com.ktpci.beacon.pciredux.action.ActionDowngradeState;
import com.ktpci.beacon.pciredux.core.Action;
import com.ktpci.beacon.pciredux.core.PCIReducer;
import com.ktpci.beacon.pciredux.core.PCIStore;
import com.ktpci.beacon.pciredux.state.PCIState;

public class ReducerDisagreeTerms implements PCIReducer {

    @NonNull
    @Override
    public PCIState reduce(@NonNull PCIState currentState, @NonNull Action action) {

        ActionDisagreeTerms actionDisagreeTerms = (ActionDisagreeTerms) action;
        final Context context = currentState.getContext();
        PCIStore.getInstance(context).dispatchTriggeredByInternalRedux(new ActionDowngradeState(PCIState.Type.DEFAULT));

        return currentState;
    }

}