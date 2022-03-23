package com.ktpci.beacon.pciredux.action;

import com.ktpci.beacon.pciredux.core.Action;

import java.util.HashMap;

public class ActionDisagreeTerms extends Action {

    public ActionDisagreeTerms() {
        super(ActionType.PCI_DISAGREE_TERMS, new HashMap<String, Object>() {{
        }});
    }
}
