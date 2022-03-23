package com.ktpci.beacon.pciredux.core;

import androidx.annotation.NonNull;

import com.ktpci.beacon.pciredux.state.PCIState;

public interface PCIReducer {
    @NonNull
    PCIState reduce(@NonNull PCIState currentState, @NonNull Action action);
}
