package com.ktpci.beacon.pciredux.core;

import androidx.annotation.NonNull;
import com.ktpci.beacon.pciredux.state.PCIState;

public interface PCIStateChangeNotifier {
    void stateChangeNotify(@NonNull PCIState prevState, @NonNull PCIState newState);

}

