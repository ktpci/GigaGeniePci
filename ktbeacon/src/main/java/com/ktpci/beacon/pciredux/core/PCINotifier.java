package com.ktpci.beacon.pciredux.core;

import androidx.annotation.NonNull;

import com.ktpci.beacon.pciredux.state.PCIState;


public interface PCINotifier {

    void notify(@NonNull PCIState state);
}
