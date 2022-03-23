package com.kt.gigagenie.pci.net;

public class NetException extends Throwable {

	private int _code;
	private String _url;

	public NetException(int code, String msg, String url, Exception e) {
		super(msg, e);
		_code = code;
		_url = url;
	}

	public int code() {
		return _code;
	}

	public String codeStr() {
		return Integer.toHexString(_code);
	}

	public String url() {
		return _url;
	}

	public String toString() {
		return "NetException[" + codeStr() + "|" + _url + "|" + super.toString() + "]";
	}
}