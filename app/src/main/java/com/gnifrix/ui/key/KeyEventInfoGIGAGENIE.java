package com.gnifrix.ui.key;

import android.util.SparseArray;
import android.view.KeyEvent;

import com.gnifrix.debug.GLog;

import java.util.ArrayList;

public class KeyEventInfoGIGAGENIE implements KeyCode_KT_GIGAGENIE {
    private static final String LOG_HEADER = "KeyEventInfoGIGAGENIE";

    SparseArray<KeyInfo> keyInfoList_pc;
    SparseArray<KeyInfo> keyInfoList_stb;
	ArrayList<KeyInfo> keyInfoList;
	KeyInfo unknownKey;
	
	public KeyEventInfoGIGAGENIE(){
        keyInfoList_pc = new SparseArray<>();
        keyInfoList_stb = new SparseArray<>();

		keyInfoList = new ArrayList();
		keyInfoList.add(new KeyInfo(KEY_MOVIE, KeyCode_KT_GIGAGENIE_PC.KEY_MOVIE, "Movie", "영화"));
		keyInfoList.add(new KeyInfo(KEY_REPLAY_TV, KeyCode_KT_GIGAGENIE_PC.KEY_REPLAY_TV,"RePlay TV", "TV다시보기"));
		keyInfoList.add(new KeyInfo(KEY_APP_STORE, KeyCode_KT_GIGAGENIE_PC.KEY_APP_STORE,"App Store", "APP스토어"));
		keyInfoList.add(new KeyInfo(KEY_EXIT, KeyCode_KT_GIGAGENIE_PC.KEY_EXIT,"Exit", "나가기"));
		keyInfoList.add(new KeyInfo(KEY_HOME, KeyCode_KT_GIGAGENIE_PC.KEY_HOME,"Home", "홈키(메뉴)"));
		keyInfoList.add(new KeyInfo(KEY_EPG, KeyCode_KT_GIGAGENIE_PC.KEY_EPG,"EPG(TV schedules)", "편성표"));
		keyInfoList.add(new KeyInfo(KEY_VOL_UP, KeyCode_KT_GIGAGENIE_PC.KEY_VOL_UP,"Vol+", "음량+"));
		keyInfoList.add(new KeyInfo(KEY_VOL_DOWN, KeyCode_KT_GIGAGENIE_PC.KEY_VOL_DOWN,"Vol-", "음량-"));
		keyInfoList.add(new KeyInfo(KEY_CH_UP, KeyCode_KT_GIGAGENIE_PC.KEY_CH_UP,"CH UP", "채널업"));
		keyInfoList.add(new KeyInfo(KEY_CH_DOWN, KeyCode_KT_GIGAGENIE_PC.KEY_CH_DOWN,"CH DOWN", "채널 다운"));
		
		keyInfoList.add(new KeyInfo(KEY_LEFT, KeyCode_KT_GIGAGENIE_PC.KEY_LEFT,"LEFT", "좌"));
		keyInfoList.add(new KeyInfo(KEY_UP, KeyCode_KT_GIGAGENIE_PC.KEY_UP,"UP", "상"));
		keyInfoList.add(new KeyInfo(KEY_RIGHT, KeyCode_KT_GIGAGENIE_PC.KEY_RIGHT,"RIGHT", "우"));
		keyInfoList.add(new KeyInfo(KEY_DOWN, KeyCode_KT_GIGAGENIE_PC.KEY_DOWN, "DOWN", "하"));
		
		keyInfoList.add(new KeyInfo(KEY_RED, KeyCode_KT_GIGAGENIE_PC.KEY_RED, "RED", "레드"));
		keyInfoList.add(new KeyInfo(KEY_GREEN, KeyCode_KT_GIGAGENIE_PC.KEY_GREEN,"GREEN", "그린"));
		keyInfoList.add(new KeyInfo(KEY_YELLOW, KeyCode_KT_GIGAGENIE_PC.KEY_YELLOW,"YELLOW", "옐로우"));
		keyInfoList.add(new KeyInfo(KEY_BLUE, KeyCode_KT_GIGAGENIE_PC.KEY_BLUE,"BLUE", "블루"));

		keyInfoList.add(new KeyInfo(KEY_OK, KeyCode_KT_GIGAGENIE_PC.KEY_OK,"OK", "확인"));
		keyInfoList.add(new KeyInfo(KEY_PREV, KeyCode_KT_GIGAGENIE_PC.KEY_PREV,"PREV", "이전"));
		
		keyInfoList.add(new KeyInfo(KEY_RW, KeyCode_KT_GIGAGENIE_PC.KEY_RW, "<<(Rewind)", "<<(되감기)"));
		keyInfoList.add(new KeyInfo(KEY_PLAY_PAUSE, KeyCode_KT_GIGAGENIE_PC.KEY_PLAY_PAUSE,"Play/Pause", "재생/일시정지"));
		keyInfoList.add(new KeyInfo(KEY_FFW, KeyCode_KT_GIGAGENIE_PC.KEY_FFW,">>(FastFoward)", ">>(빨리감기)"));
		
		keyInfoList.add(new KeyInfo(KEY_0, KeyCode_KT_GIGAGENIE_PC.KEY_0,"0", "0"));
		keyInfoList.add(new KeyInfo(KEY_1, KeyCode_KT_GIGAGENIE_PC.KEY_1,"1", "1"));
		keyInfoList.add(new KeyInfo(KEY_2, KeyCode_KT_GIGAGENIE_PC.KEY_2,"2", "2"));
		keyInfoList.add(new KeyInfo(KEY_3, KeyCode_KT_GIGAGENIE_PC.KEY_3,"3", "3"));
		keyInfoList.add(new KeyInfo(KEY_4, KeyCode_KT_GIGAGENIE_PC.KEY_4,"4", "4"));
		keyInfoList.add(new KeyInfo(KEY_5, KeyCode_KT_GIGAGENIE_PC.KEY_5,"5", "5"));
		keyInfoList.add(new KeyInfo(KEY_6, KeyCode_KT_GIGAGENIE_PC.KEY_6,"6", "6"));
		keyInfoList.add(new KeyInfo(KEY_7, KeyCode_KT_GIGAGENIE_PC.KEY_7,"7", "7"));
		keyInfoList.add(new KeyInfo(KEY_8, KeyCode_KT_GIGAGENIE_PC.KEY_8,"8", "8"));
		keyInfoList.add(new KeyInfo(KEY_9, KeyCode_KT_GIGAGENIE_PC.KEY_9,"9", "9"));
		
		
		keyInfoList.add(new KeyInfo(KEY_SEARCH_WINDOW, KeyCode_KT_GIGAGENIE_PC.KEY_SEARCH_WINDOW,"Search Window", "검색창"));
		keyInfoList.add(new KeyInfo(KEY_DELETE, KeyCode_KT_GIGAGENIE_PC.KEY_DELETE,"Delete", "지우기"));
		keyInfoList.add(new KeyInfo(KEY_EN_KOR, KeyCode_KT_GIGAGENIE_PC.KEY_EN_KOR,"En/Kor", "한/영"));
		keyInfoList.add(new KeyInfo(KEY_MY_MENU, KeyCode_KT_GIGAGENIE_PC.KEY_MY_MENU,"My Menu", "마이메뉴"));
		keyInfoList.add(new KeyInfo(KEY_SHOPPING, KeyCode_KT_GIGAGENIE_PC.KEY_SHOPPING,"Shopping", "쇼핑"));
		keyInfoList.add(new KeyInfo(KEY_WIDGET, KeyCode_KT_GIGAGENIE_PC.KEY_WIDGET,"Widget", "위젯"));
		keyInfoList.add(new KeyInfo(KEY_WEB_SEARCH, KeyCode_KT_GIGAGENIE_PC.KEY_WEB_SEARCH,"Web Search", "웹검색"));
		keyInfoList.add(new KeyInfo(KEY_MUTE, KeyCode_KT_GIGAGENIE_PC.KEY_MUTE,"Mute", "음소거"));

		keyInfoList.add(new KeyInfo(KEY_MONTH_PAY, KeyCode_KT_GIGAGENIE_PC.KEY_MONTH_PAY,"monthly pay", "월정액"));
		keyInfoList.add(new KeyInfo(KEY_OPTION, KeyCode_KT_GIGAGENIE_PC.KEY_OPTION,"Option", "옵션키(Ξ)"));

		unknownKey = new KeyInfo(-1, -1, "UNKNOWN KEY", "알수없는 키");

        for (KeyInfo ki: keyInfoList) {
            keyInfoList_pc.append(ki.getCodePc(), ki);
            keyInfoList_stb.append(ki.getCode(), ki);
        }
    }
	
	public String getKeyInfo(int actionType, int keyCode){
		KeyInfo ki = searchKeyInfoByCode(keyCode);
		return "Event : " + getActionTypeStr(actionType) + "(" + actionType + ") / KeyCode : " + ki.getKeyStr() + "(" + ki.getKeyStrKor() + "," + keyCode + ")";
	}

	public KeyInfo searchKeyInfoByCode(int keyCode){
//		GLog.printInfo(">>>>><<<<", "found Key code(STB) :" + keyCode);
//		for(int i=0; i<keyInfoList_pc.size(); i++){
//			GLog.printInfo(">>>>>>>>>>>>>>>>>>", "Key List's Key[" + i + "] : " + keyInfoList_pc.keyAt(i));
//		}

	    return keyInfoList_stb.get(keyCode, unknownKey);
	}

    public KeyInfo searchKeyInfoByPcCode(int pcKeyCode){
//		GLog.printInfo(">>>>><<<<", "found Key code(PC) :" + pcKeyCode);
//		for(int i=0; i<keyInfoList_pc.size(); i++){
//			GLog.printInfo(">>>>>>>>>>>>>>>>>>", "Key List's Key[" + i + "] : " + keyInfoList_pc.keyAt(i));
//		}

        return keyInfoList_pc.get(pcKeyCode, unknownKey);
    }

	public String getActionTypeStr(int actionType){
		if(actionType == KeyEvent.ACTION_DOWN) return "PRESSED";
		else if(actionType == KeyEvent.ACTION_UP) return "RELEASED";
		else return "UNKNOWN";
	}
	
	public void dispose(){
	    if(keyInfoList_stb != null){
            keyInfoList_stb.clear();
            keyInfoList_stb = null;
        }
        if(keyInfoList_pc != null){
            keyInfoList_pc.clear();
            keyInfoList_pc = null;
        }
		if(keyInfoList != null){
            for (KeyInfo ki: keyInfoList) ki.dispose();
			keyInfoList.clear();
			keyInfoList = null;
		}
		
		unknownKey.dispose();
		unknownKey = null;
	}

	public String toString(){
	    return LOG_HEADER;
    }
}
