/**
 * JsonType
 * 
 * enumeration type for JSON object.
 * @author comart
 */

package com.kt.gigagenie.pci.net.json;

import com.kt.gigagenie.pci.system.PciRuntimeException;

import java.io.Serializable;

public class JsonType implements Serializable {

	private static final long serialVersionUID = 1L;

	private final static int TYPE_VALUE = 1;
	private final static int TYPE_OBJECT = 2;
	private final static int TYPE_ARRAY = 3;
	/**
	 * JSON type of Value.
	 */
	public static final JsonType VALUE = new JsonType(TYPE_VALUE);

	/**
	 * JSON type of Object(name value pair).
	 */
	public static final JsonType OBJECT = new JsonType(TYPE_OBJECT);

	/**
	 * JSON type of Array.
	 */
	public static final JsonType ARRAY = new JsonType(TYPE_ARRAY);

	/**
	 * @serial
	 */
	private int type;

	/**
	 * Set scope to private to avoid instantiation from other class.
	 * 
	 * @param type
	 *            type of json.
	 */
	private JsonType(int type) {
		this.type = type;
	}

	public boolean equals(Object o) {
		if (o instanceof JsonType)
			return ((JsonType) o).type == type;
		else
			return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public String toString() {
		switch (type) {
		case TYPE_VALUE:
			return "JsonType.VALUE";
		case TYPE_OBJECT:
			return "JsonType.OBJECT";
		case TYPE_ARRAY:
			return "JsonType.ARRAY";
		default:
			throw new PciRuntimeException("unknown Json Type : " + type);
		}
	}
}
