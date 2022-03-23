package com.ktpci.beacon.pciredux.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.ktpci.beacon.pciredux.action.ActionType;
import com.ktpci.beacon.pciredux.state.PCIState;
import com.ktpci.beacon.pciutil.PCILog;

public class AsyncStore {

    @NonNull
    private HandlerThread workerThread;
    @NonNull private Handler workerHandler;
    @NonNull private Handler mainHandler;

    @NonNull private State state;
    @NonNull private Reducer reducer;
    @NonNull private Notifier notifier;

    public AsyncStore(@NonNull State initialState, @NonNull Reducer reducer, @NonNull Notifier notifier) {
        this.state = initialState;
        this.reducer = reducer;
        this.notifier = notifier;
        workerThread = new HandlerThread("store-worker");
        workerThread.start();
        workerHandler = new Handler(workerThread.getLooper());
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void dispatch(@NonNull final Action action) {
        final AsyncStore store = this;
        workerHandler.post(new Runnable() {
            @Override
            public void run() {
                final State state = store.getState();
                PCILog.d("[DEBUG][State %8s] Dispatch %s(%s)", state.getType(), action.getClass().getSimpleName(), action.getType());

                final State processedState = reducer.reduce(state, action);
                if(doesActionModifyState(action.getType())) {
                    store.setState(processedState);
                    store.stateChangeNotify(state, processedState, action);
                    store.notify(action);
                }
                store.verboseNotify(action);
            }
        });
    }

    public void dispatchTriggeredByInternalRedux(@NonNull final Action action) {
        final AsyncStore store = this;
        workerHandler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                final State state = store.getState();
                PCILog.d("[DEBUG][State %8s] Dispatch(TriggeredByInternalRedux) %s(%s)", state.getType(), action.getClass().getSimpleName(), action.getType());

                final State processedState = reducer.reduce(state, action);
                if(doesActionModifyState(action.getType())) {
                    store.setState(processedState);
                    store.stateChangeNotify(state, processedState, action);
                    store.notify(action);
                }
                store.verboseNotify(action);
            }
        });
    }

    @WorkerThread
    public void setState(@NonNull final State newState) {
        PCILog.d("[DEBUG][State %8s] => %s", this.state.getType(), newState.getType());
        final PCIState.Type prevStateLevel = this.state.getType();
        final PCIState.Type nextStateLevel = newState.getType();
        if (prevStateLevel == nextStateLevel) {
            this.state = newState;
            this.state.onKeep();
        } else {
            this.state.onLeave(nextStateLevel);
            this.state = newState;
            this.state.onEnter(prevStateLevel);
        }

        this.state.writePersistent();
    }

    @NonNull
    public State getState() {
        return this.state;
    }

    @WorkerThread
    private void notify(@NonNull final Action action) {
        final State state = this.state;
        mainHandler.post(() -> notifier.notify(state, action));
    }

    @WorkerThread
    private void stateChangeNotify(@NonNull final State prevState, @NonNull final State newState, @NonNull final Action action) {
        mainHandler.post(() -> notifier.stateChangeNotify(prevState, newState, action));
    }

    @WorkerThread
    public void verboseNotify(@NonNull final Action action) {
        final State state = this.state;
        mainHandler.post(() -> notifier.verboseNotify(state, action));
    }

    private boolean doesActionModifyState(final ActionType actionType) {   // actionYpe -> PCI_DOWNGRADE_STATE
        if (actionType == ActionType.PCI_PUSH_RECEIVED ||
                actionType == ActionType.PCI_SCAN_SOUND ||
                actionType == ActionType.PCI_SET_AVOID_APP_PERMISSION_MONITOR ||
                actionType == ActionType.PCI_UPLOAD_ALL_PACKAGES ||
                actionType == ActionType.PCI_UPLOAD_PCI_PACKAGES ||
                actionType == ActionType.PCI_UPLOAD_PUSH_RESULT ||
                actionType == ActionType.PCI_UPLOAD_PUSH_TRIGGERED_APP_INSTALL) {
            return false;
        }
        else return true;
    }
}

