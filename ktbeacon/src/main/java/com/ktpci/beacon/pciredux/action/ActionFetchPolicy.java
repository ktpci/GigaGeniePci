package com.ktpci.beacon.pciredux.action;

import com.ktpci.beacon.pciredux.core.Action;

import java.util.HashMap;

public class ActionFetchPolicy extends Action {

    public ActionFetchPolicy() {
        super(ActionType.PCI_FETCH_POLICY, new HashMap<String, Object>() {{
        }});
    }

}
