package com.kt.gigagenie.pci.net.json;

public class JsonValue extends Json {

	private static final long serialVersionUID = 1L;

	private String value;

	/**
	 * Constructor from String value.
	 * 
	 * @param value
	 *            String object.
	 */
	public JsonValue(String value) {
		set(value);
	}

	/**
	 * Constructor from integer value.
	 * 
	 * @param value
	 *            integer value.
	 */
	public JsonValue(int value) {
		set(value);
	}

	/**
	 * Constructor from long integer value.
	 * 
	 * @param value
	 *            long integer value.
	 */
	public JsonValue(long value) {
		set(value);
	}

	/**
	 * Constructor from floating point value.
	 * 
	 * @param value
	 *            floating point value.
	 */
	public JsonValue(float value) {
		set(value);
	}

	/**
	 * Constructor from double precision floating point value.
	 * 
	 * @param value
	 *            double precision floating point value.
	 */
	public JsonValue(double value) {
		set(value);
	}

	/**
	 * Constructor from boolean value.
	 * 
	 * @param value
	 *            boolean value.
	 */
	public JsonValue(boolean value) {
		set(value);
	}

	public int getInt() {
		return Integer.parseInt(getString());
	}

	public long getLong() {
		return Long.parseLong(getString());
	}

	public long getI64() {
		return getLong();
	}

	public float getFloat() {
		return Float.parseFloat(getString());
	}

	public double getDouble() {
		return Double.parseDouble(getString());
	}

	public boolean getBoolean() {
		String s = getString();
		char c = s.charAt(0);
		return "TtYy1".indexOf(c) > -1;
	}

	public void set(int value) {
		set("" + value);
	}

	public void set(long value) {
		set("" + value);
	}

	public void set(float value) {
		set("" + value);
	}

	public void set(double value) {
		set("" + value);
	}

	public void set(boolean value) {
		set(value ? "true" : "false");
	}

	/**
	 * get string representation of this value.
	 * 
	 * @return string representation of this value.
	 */
	public String getString() {
		return value;
	}

	/**
	 * set new string value.
	 */
	public void set(String value) {
		this.value = value;
	}

	public StringBuffer toJsonString(StringBuffer sb) {
		sb.append('\"');
		escape(value, sb);
		sb.append('\"');
		return sb;
	}

	public int size() {
		return value == null ? 0 : value.length();
	}

	public Json duplicate() {
		return new JsonValue(value);
	}

	public void clear() {
		// do nothing.
	}

	protected StringBuffer toJsonStringPretty(StringBuffer sb, String prefix) {
		return toJsonString(sb);
	}

	public void cleanUp() {
		clear();
	}

}
