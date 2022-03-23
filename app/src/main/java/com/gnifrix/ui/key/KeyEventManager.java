package com.gnifrix.ui.key;

import com.kt.gigagenie.pci.Global;

/**
 * Pc, Emul, STB에서 각 키를 매칭하여 작동하게끔 관리
 * Created by LeeBaeng on 2018-09-13.
 */
public class KeyEventManager {
    private static final String LogHeader = "KeyEventManager";

    private static KeyEventManager INSTANCE = null;
    private KeyEventInfoGIGAGENIE keyEventInfo = null;

    public KeyEventManager(){
        keyEventInfo = new KeyEventInfoGIGAGENIE();
    }

    public static KeyEventManager getInstance(){
        if (INSTANCE == null)
            INSTANCE = new KeyEventManager();

    return INSTANCE;
    }

    public KeyInfo getMatchedKey(int keyCode){
        if(Global.getInstance().getPciProperty().getAppPlatform().equals("emul"))
            return keyEventInfo.searchKeyInfoByPcCode(keyCode);
        else
            return keyEventInfo.searchKeyInfoByCode(keyCode);
    }

    public int getMatchedKeyCode(int keyCode){
        return getMatchedKey(keyCode).getCode();
    }

    public void dispose(){
        keyEventInfo.dispose();
        keyEventInfo = null;

        INSTANCE = null;
    }

    public KeyEventInfoGIGAGENIE getKeyEventInfo() { return keyEventInfo; }
    public String toString(){
        return LogHeader;
    }
}
