package com.ktpci.beacon.pciredux.action;

import com.ktpci.beacon.pciredux.core.Action;

import java.util.HashMap;

public class ActionOptOut extends Action {

    public ActionOptOut() {
        super(ActionType.PCI_OPT_OUT, new HashMap<String, Object>() {{

        }});
    }

}
