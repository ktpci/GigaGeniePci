package com.ktpci.beacon.pciredux.reducer;

import com.ktpci.beacon.pciredux.action.ActionType;
import com.ktpci.beacon.pciredux.core.Action;
import com.ktpci.beacon.pciredux.core.PCIReducer;
import com.ktpci.beacon.pciredux.core.Reducer;
import com.ktpci.beacon.pciredux.core.State;
import com.ktpci.beacon.pciredux.state.PCIState;
import com.ktpci.beacon.pciutil.PCILog;

import java.util.HashMap;
import java.util.Map;

import static com.ktpci.beacon.pciredux.action.ActionType.PCI_AGREE_TERMS;
import static com.ktpci.beacon.pciredux.action.ActionType.PCI_DISAGREE_TERMS;
import static com.ktpci.beacon.pciredux.action.ActionType.PCI_DOWNGRADE_STATE;
import static com.ktpci.beacon.pciredux.action.ActionType.PCI_OPT_IN;
import static com.ktpci.beacon.pciredux.action.ActionType.PCI_OPT_OUT;
import static com.ktpci.beacon.pciredux.state.PCIState.Type.ACTIVE;
import static com.ktpci.beacon.pciredux.state.PCIState.Type.DEFAULT;
import static com.ktpci.beacon.pciredux.state.PCIState.Type.INACTIVE;

public class ReducerRoot  implements Reducer {

    // TODO : refactor state - action - reducer mapping table to better code
    private final Map<PCIState.Type, Map<ActionType, PCIReducer>> subReducers = new HashMap<PCIState.Type, Map<ActionType, PCIReducer>>() {{
        put(DEFAULT, new HashMap<ActionType, PCIReducer>() {{
            put(PCI_AGREE_TERMS, new ReducerAgreeTerms());

        }});
        put(INACTIVE, new HashMap<ActionType, PCIReducer>() {{

            put(PCI_OPT_IN, new ReducerOptIn());
            put(PCI_DISAGREE_TERMS, new ReducerDisagreeTerms());
            put(PCI_DOWNGRADE_STATE, new ReducerDowngradeState());

        }});
        put(ACTIVE, new HashMap<ActionType, PCIReducer>() {{
            //put(PCI_DISAGREE_TERMS, new ReducerDisagreeTerms());
            put(PCI_OPT_OUT, new ReducerOptOut());
            put(PCI_DOWNGRADE_STATE, new ReducerDowngradeState());
        }});

    }};

    @Override
    public State reduce(State currentState, Action action) {
        PCIReducer reducer = getReducer(currentState, action);
        if (reducer != null) {
            PCILog.d("[DEBUG][State %8s] Reduce %s to %s", currentState.getType(), action.getClass().getSimpleName(), reducer.getClass().getSimpleName());
            return reducer.reduce((PCIState) currentState, action);
        } else {
            PCILog.d("[DEBUG][State %8s] Reduce %s to nothing (no matching reducer)", currentState.getType(), action.getClass().getSimpleName());
            return currentState;
        }
    }

    private PCIReducer getReducer(State state, Action action) {
        if (subReducers.containsKey(state.getType()) && subReducers.get(state.getType()).containsKey(action.getType())) {
            return subReducers.get(state.getType()).get(action.getType());
        } else {
            return null;
        }
    }

}
