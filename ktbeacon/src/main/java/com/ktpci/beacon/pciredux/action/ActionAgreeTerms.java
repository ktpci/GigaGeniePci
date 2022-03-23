package com.ktpci.beacon.pciredux.action;

import com.ktpci.beacon.pciredux.core.Action;

import java.util.HashMap;

public class ActionAgreeTerms extends Action {

    private static final String KEY_ADID = "KEY_ADID";
    private static final String KEY_PARTNERCODE = "KEY_PARTNERCODE";
    private static final String KEY_PCIMODE = "KEY_PCIMODE";

    public ActionAgreeTerms(final String adid, final String PartnerCodeNumber, final int PCIMode) {
        super(ActionType.PCI_AGREE_TERMS, new HashMap<String, Object>() {{
            this.put(KEY_ADID, adid);
            this.put(KEY_PARTNERCODE, PartnerCodeNumber);
            this.put(KEY_PCIMODE, PCIMode);
        }});

    }
    public String getAdid() {
        return (String) getPayload().get(KEY_ADID);
    }
    public String getPartnerCode() {
        return (String) getPayload().get(KEY_PARTNERCODE);
    }
    public int getPciMode() {
        return (int) getPayload().get(KEY_PCIMODE);
    }
}
