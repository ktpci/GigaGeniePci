package com.ktpci.beacon.pciredux.core;


import android.content.Context;

import androidx.annotation.NonNull;

import com.ktpci.beacon.pciredux.notifier.NotifierRoot;
import com.ktpci.beacon.pciredux.reducer.ReducerRoot;
import com.ktpci.beacon.pciredux.state.PCIState;

public class PCIStore extends AsyncStore {

    private static PCIStore singleton;

    private final NotifierRoot mNotifierRoot;

    public static PCIStore getInstance(@NonNull final Context context) {
        if (singleton == null) {
            synchronized (PCIStore.class) {
                if (singleton == null) {
                    singleton = new PCIStore(
                            PCIState.from(context),
                            new ReducerRoot(),
                            new NotifierRoot());
                }

            }
        }
        return singleton;
    }

    private PCIStore(@NonNull PCIState state, @NonNull ReducerRoot reducerRoot, @NonNull NotifierRoot notifierRoot) {
        super(state, reducerRoot, notifierRoot);
        mNotifierRoot = notifierRoot;
    }

    public void addNotifier(String name, PCINotifier notifier) {
        mNotifierRoot.addSubNotifier(name, notifier);
    }

    public boolean removeNotifier(String name) {
        return mNotifierRoot.removeSubNotifier(name);
    }

    public void addStateChangeNotifier(String name, PCIStateChangeNotifier notifier) {
        mNotifierRoot.addSubStateChangeNotifier(name, notifier);
    }

    public boolean removeStateChangeNotifier(String name) {
        return mNotifierRoot.removeSubStateChangeNotifier(name);
    }

    public void addVerboseNotifier(String name, PCINotifier notifier) {
        mNotifierRoot.addSubVerboseNotifier(name, notifier);
    }

    public boolean removeVerboseNotifier(String name) {
        return mNotifierRoot.removeSubVerboseNotifier(name);
    }

}
