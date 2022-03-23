package com.gnifrix.ui.key;

import com.gnifrix.debug.GLog;

public class KeyInfo {
	int code;
	int code_pc;
	String keyStr;
	String keyStrKor;
	
	public KeyInfo(int _code, int _code_pc, String _str, String _strKor){
		setEvent(_code, _code_pc, _str, _strKor);
	}

	public void setEvent(int _code, int _code_pc, String _str, String _strKor){
		code = _code;
		code_pc = _code_pc;
		keyStr = _str;
		keyStrKor = _strKor;
	}

	public void printKeyInfo(Object caller){
		if(caller == null) GLog.printDbg("KeyInfo", getKeyInfo());
		GLog.printDbg(caller, getKeyInfo());
	}

	public String getKeyInfo(){
		return "KEY : code=" + getCode() + ", codePc=" + getCodePc() + " | " + getKeyStr() + "(Kor:" + getKeyStrKor() + ")";
	}



	public int getCode() { return code; }
	public int getCodePc() { return code_pc; }
	public String getKeyStr() { return keyStr; }
	public String getKeyStrKor() { return keyStrKor; }

	public void dispose(){
		keyStr = null;
		keyStrKor = null;
	}

	public String toString(){
		return "KeyInfo[" + getKeyInfo() + "]";
	}
}
