package com.ktpci.beacon.pciredux.core;

import androidx.annotation.NonNull;

import com.ktpci.beacon.pciredux.state.PCIState;

public interface State {
    void onKeep();
    void onEnter(@NonNull PCIState.Type fromStateLevel);
    void onLeave(@NonNull PCIState.Type toStateLevel);
    void writePersistent();
    PCIState.Type getType();
}
