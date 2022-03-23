package com.ktpci.beacon.pciredux.action;

import com.ktpci.beacon.pciredux.core.Action;
import com.ktpci.beacon.pciredux.state.PCIState;

import java.util.HashMap;

public class ActionDowngradeState extends Action {

    private static final String KEY_STATE_TYPE = "state_type";

    public ActionDowngradeState(final PCIState.Type stateType) {
        super(ActionType.PCI_DOWNGRADE_STATE, new HashMap<String, Object>() {{
            put(KEY_STATE_TYPE, stateType);
        }});
    }

    public PCIState.Type getStateType() {
        return (PCIState.Type) this.getPayload().get(KEY_STATE_TYPE);
    }

}

