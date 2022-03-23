/**
 * class name	:JsonObject
 * 
 * Simple JSON implement.
 * Every values are treated as String.
 * 
 * @author comart
 */

package com.kt.gigagenie.pci.net.json;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

public class JsonObject extends Json implements Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap data;

	public JsonObject() {
		setType(JsonType.OBJECT);
		data = new HashMap();
	}

	public Json get(Object key) {
		return (Json) data.get(key);
	}

	public void put(Object key, Json json) {
		data.put(key, json);
	}

	public String getString(Object key) {
		JsonValue jval = (JsonValue) get(key);
		if (jval != null)
			return jval.getString();
		return null;
	}

	public int getInt(Object key) {
		JsonValue jval = (JsonValue) get(key);
		if (jval != null)
			return jval.getInt();
		return 0;
	}

	public long getLong(Object key) {
		JsonValue jval = (JsonValue) get(key);
		if (jval != null)
			return jval.getLong();
		return 0;
	}

	public long getI64(Object key) {
		return getLong(key);
	}

	public float getFloat(Object key) {
		JsonValue jval = (JsonValue) get(key);
		if (jval != null)
			return jval.getFloat();
		return 0;
	}

	public double getDouble(Object key) {
		JsonValue jval = (JsonValue) get(key);
		if (jval != null)
			return jval.getDouble();
		return 0;
	}

	public boolean getBoolean(Object key) {
		JsonValue jval = (JsonValue) get(key);
		if (jval != null)
			return jval.getBoolean();
		return false;
	}

	public void put(Object key, String value) {
		put(key, new JsonValue(value));
	}

	public void put(Object key, int value) {
		put(key, "" + value);
	}

	public void put(Object key, long value) {
		put(key, "" + value);
	}

	public void put(Object key, float value) {
		put(key, "" + value);
	}

	public void put(Object key, double value) {
		put(key, "" + value);
	}

	public void put(Object key, boolean value) {
		put(key, value ? "true" : "false");
	}

	public StringBuffer toJsonString(StringBuffer sb) {
		Iterator iter = data.keySet().iterator();
		sb.append('{');
		while (iter.hasNext()) {
			String key = (String) iter.next();
			sb.append('\"');
			escape(key, sb);
			sb.append('\"');
			sb.append(':');
			((Json) data.get(key)).toJsonString(sb);
			if (iter.hasNext())
				sb.append(',');
		}
		sb.append('}');
		return sb;
	}

	public Json remove(Object key) {
		return (Json) data.remove(key);
	}

	public int size() {
		return data.size();
	}

	public Json duplicate() {
		JsonObject res = new JsonObject();
		Iterator iter = data.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			res.put(key, get(key).duplicate());
		}
		return res;
	}

	public void clear() {
		data.clear();
	}

	protected StringBuffer toJsonStringPretty(StringBuffer sb, String prefix) {
		Iterator iter = data.keySet().iterator();
		String newprefix = "  " + prefix;
		sb.append('{');
		if (iter.hasNext()) {
			sb.append(LINESEP);
			while (iter.hasNext()) {
				String key = (String) iter.next();
				sb.append(newprefix);
				sb.append('\"');
				escape(key, sb);
				sb.append('\"');
				sb.append(':');
				((Json) data.get(key)).toJsonStringPretty(sb, newprefix);
				if (iter.hasNext())
					sb.append(',');
				sb.append(LINESEP);
			}
			sb.append(prefix);
		}
		sb.append('}');
		return sb;
	}

	public void cleanUp() {
		Iterator iter = data.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			Json item = (Json) data.get(key);
			if (item != null)
				item.cleanUp();
		}
		data.clear();
	}
}
