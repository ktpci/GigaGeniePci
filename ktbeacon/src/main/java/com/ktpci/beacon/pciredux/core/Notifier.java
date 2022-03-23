package com.ktpci.beacon.pciredux.core;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

public interface Notifier {
    @MainThread
    void notify(@NonNull State state, @NonNull Action action);
    @MainThread void stateChangeNotify(@NonNull State prevState, @NonNull State newState, @NonNull Action action);
    @MainThread void verboseNotify(@NonNull State state, @NonNull Action action);
}
