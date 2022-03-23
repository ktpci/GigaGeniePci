package com.kt.gigagenie.pci.net.json;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * JSON array type processing class.
 * 
 * @author comart
 */
public class JsonArray extends Json {

	private static final long serialVersionUID = 1L;

	private ArrayList data;

	/**
	 * Constructor.
	 */
	public JsonArray() {
		setType(JsonType.ARRAY);
		data = new ArrayList();
	}

	/**
	 * Add a Json object to this array.
	 * <p>
	 * 
	 * @param value
	 *            Json object to be added.
	 */
	public void add(Json value) {
		data.add(value);
	}

	/**
	 * Add a Json object to this array.
	 * <p>
	 * 
	 * @param value
	 *            Json object to be added.
	 */
	public void add(int index, Json value) {
		data.add(index, value);
	}

	/**
	 * Get a Json object at given index.
	 * <p>
	 * 
	 * @param index
	 *            index of wanted Json object.
	 * @return a Json object at <code>index</code>
	 */
	public Json get(int index) {
		return (Json) data.get(index);
	}

	/**
	 * Get string value at <code>index</code>
	 * <p>
	 * 
	 * @param index
	 *            index of wanted value.
	 * @return a string value at <code>index</code>
	 */
	public String getString(int index) {
		JsonValue jval = (JsonValue) get(index);
		if (jval != null)
			return jval.getString();
		return null;
	}

	public int getInt(int index) {
		JsonValue jval = (JsonValue) get(index);
		if (jval != null)
			return jval.getInt();
		return 0;
	}

	public long getLong(int index) {
		JsonValue jval = (JsonValue) get(index);
		if (jval != null)
			return jval.getLong();
		return 0;
	}

	public long getI64(int index) {
		return getLong(index);
	}

	public float getFloat(int index) {
		JsonValue jval = (JsonValue) get(index);
		if (jval != null)
			return jval.getFloat();
		return 0;
	}

	public double getDouble(int index) {
		JsonValue jval = (JsonValue) get(index);
		if (jval != null)
			return jval.getDouble();
		return 0;
	}

	public boolean getBoolean(int index) {
		JsonValue jval = (JsonValue) get(index);
		if (jval != null)
			return jval.getBoolean();
		return false;
	}

	public void add(String value) {
		add(new JsonValue(value));
	}

	public void add(int value) {
		add("" + value);
	}

	public void add(long value) {
		add("" + value);
	}

	public void add(float value) {
		add("" + value);
	}

	public void add(double value) {
		add("" + value);
	}

	public void add(boolean value) {
		add(value ? "true" : "false");
	}

	public StringBuffer toJsonString(StringBuffer sb) {
		sb.append('[');
		for (int i = 0; i < data.size(); i++) {
			Json json = (Json) data.get(i);
			if (i > 0)
				sb.append(',');
			json.toJsonString(sb);
		}
		sb.append(']');
		return sb;
	}

	public Json remove(int index) {
		return (Json) data.remove(index);
	}

	public int size() {
		return data.size();
	}

	public Json duplicate() {
		JsonArray res = new JsonArray();
		for (int i = 0; i < size(); i++)
			res.add(get(i).duplicate());
		return res;
	}

	public void clear() {
		data.clear();
	}

	protected StringBuffer toJsonStringPretty(StringBuffer sb, String prefix) {
		String newprefix = "  " + prefix;
		Iterator iter = data.iterator();
		sb.append('[');
		// if (iter.hasNext()) {
		// sb.append(LINESEP).append(newprefix);
		while (iter.hasNext()) {
			Json json = (Json) iter.next();
			json.toJsonStringPretty(sb, newprefix);
			if (iter.hasNext())
				sb.append(',');
		}
		// sb.append(LINESEP).append(prefix);
		// }
		sb.append(']');
		return sb;
	}

	public void cleanUp() {
		while (data.size() > 0) {
			Json item = (Json) data.remove(0);
			if (item != null)
				item.cleanUp();
		}
	}

}
