package com.gnifrix.ui.key;

import android.view.KeyEvent;

public interface KeyCode_KT_GIGAGENIE_PC {
	// 안드로이드 에뮬레이터에서 F1~F12 및 CTRL, ALT, INSERT, NUMLOCK, ESC, PrintScr, PAGE UP/DN, HOME, END, DELETE등의 주요 시스템 키는 전부 이벤트가 들어오지 않음
	// (이벤트가 들어오지 않을 경우 키코드 변경 필요)
	// -1 = 정의하지 않음

	int KEY_MOVIE = -1;
	int KEY_REPLAY_TV = -1;
	int KEY_APP_STORE = -1;
	int KEY_EXIT = KeyEvent.KEYCODE_GRAVE;  // 1옆에 ' 키
	int KEY_HOME = KeyEvent.KEYCODE_APOSTROPHE;  // 엔터옆 ' 키
	int KEY_EPG = -1;  // 편성표 키
	int KEY_VOL_UP = KeyEvent.KEYCODE_EQUALS; // +키
	int KEY_VOL_DOWN = KeyEvent.KEYCODE_MINUS; // -키
	int KEY_CH_UP = -1;
	int KEY_CH_DOWN = -1;
	
	int KEY_LEFT = KeyEvent.KEYCODE_DPAD_LEFT;
	int KEY_UP = KeyEvent.KEYCODE_DPAD_UP;
	int KEY_RIGHT = KeyEvent.KEYCODE_DPAD_RIGHT;
	int KEY_DOWN = KeyEvent.KEYCODE_DPAD_DOWN;
	
	int KEY_RED = KeyEvent.KEYCODE_LEFT_BRACKET; // [ 키
	int KEY_GREEN = -1;
	int KEY_YELLOW = -1;
	int KEY_BLUE = KeyEvent.KEYCODE_RIGHT_BRACKET; // ] 키

	int KEY_OK = KeyEvent.KEYCODE_ENTER; // ENTER
	int KEY_PREV = KeyEvent.KEYCODE_DEL; // 백스페이스
	
	int KEY_RW = KeyEvent.KEYCODE_COMMA; // <키(쉬프트 없이)
	int KEY_PLAY_PAUSE = KeyEvent.KEYCODE_SLASH; // ?키(쉬프트 없이)
	int KEY_FFW = KeyEvent.KEYCODE_PERIOD; // >키(쉬프트 없이)
	
	int KEY_0 = KeyEvent.KEYCODE_0;
	int KEY_1 = KeyEvent.KEYCODE_1;
	int KEY_2 = KeyEvent.KEYCODE_2;
	int KEY_3 = KeyEvent.KEYCODE_3;
	int KEY_4 = KeyEvent.KEYCODE_4;
	int KEY_5 = KeyEvent.KEYCODE_5;
	int KEY_6 = KeyEvent.KEYCODE_6;
	int KEY_7 = KeyEvent.KEYCODE_7;
	int KEY_8 = KeyEvent.KEYCODE_8;
	int KEY_9 = KeyEvent.KEYCODE_9;
	
	int KEY_SEARCH_WINDOW = -1;
	int KEY_DELETE = -1;
	int KEY_EN_KOR = -1;
	int KEY_MY_MENU = -1;
	int KEY_SHOPPING = -1;
	int KEY_WIDGET = -1;
	int KEY_WEB_SEARCH = -1;
	int KEY_MUTE = -1;

	int KEY_MONTH_PAY = -1;
	int KEY_OPTION = -1;
}


