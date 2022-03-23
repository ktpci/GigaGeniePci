package com.kt.gigagenie.pci.net.json;

import java.io.Serializable;

/**
 * Json implementation class.
 * <p>
 * 
 * @author comart
 *
 */
public abstract class Json implements Serializable {

	private static final long serialVersionUID = 1L;

	protected static String LINESEP;

	static {
		LINESEP = System.getProperty("line.separator");
		if (LINESEP == null)
			LINESEP = "\r\n";
	}

	private JsonType type;

	/**
	 * Set type of this JSON object.
	 * <p>
	 * 
	 * @param type
	 *            one of JsonType.OBJECT, JsonType.ARRAY and JsonType.VALUE
	 */
	protected void setType(JsonType type) {
		this.type = type;
	}

	/**
	 * Get the type of this JSON object.
	 * <p>
	 * 
	 * @return the type of this JSON object.
	 */
	public JsonType getType() {
		return type;
	}

	/**
	 * Apply JSON escape to given string and store it to StringBuffer object.
	 * <p>
	 * 
	 * @param value
	 *            String object to be escaped.
	 * @param sb
	 *            The escaped string will be stored in this parameter.
	 */
	protected void escape(String value, StringBuffer sb) {
		value = "" + value;
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			switch (c) {
			case '\"':
				sb.append("\\\"");
				break;
			case '\'':
				sb.append("\\\'");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '{':
				sb.append("\\{");
				break;
			case '}':
				sb.append("\\}");
				break;
			case '[':
				sb.append("\\[");
				break;
			case ']':
				sb.append("\\]");
				break;
			case '/':
				sb.append("\\/");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			default:
				sb.append(c);
				break;
			}
		}
	}

	/**
	 * Returns the JSON representation of this object.
	 * <p>
	 * 
	 * @return the JSON representation of this object.
	 */
	public String toJsonString() {
		StringBuffer sb = new StringBuffer();
		toJsonString(sb);
		return sb.toString();
	}

	public String toString() {
		return toJsonString();
	}

	/**
	 * Get pretty(well formed) JSON representation of this object.
	 * <p>
	 * 
	 * @return well formed JSON representation of this object.
	 */
	public String toJsonStringPretty() {
		StringBuffer sb = new StringBuffer();
		toJsonStringPretty(sb, "");
		return sb.toString();
	}

	/**
	 * Get JSON representation of this object.
	 * <p>
	 * Store the JSON representation of this object to given
	 * {@link StringBuffer} object.
	 * 
	 * @param sb
	 *            StringBuffer object where JSON representation of this object
	 *            will be stored in.
	 * @return <code>sb</code> argument where the JSON string of this object
	 *         added.
	 */
	public abstract StringBuffer toJsonString(StringBuffer sb);

	/**
	 * Get pretty(well formatted) JSON representation of this object.
	 * <p>
	 * Store the JSON representation of this object to given
	 * {@link StringBuffer} object.
	 * 
	 * @param sb
	 *            StringBuffer object where JSON representation of this object
	 *            will be stored in.
	 * @param prefix
	 *            prepended to each line for the depth represent.
	 * @return <code>sb</code> argument where the JSON string of this object
	 *         added.
	 */
	protected abstract StringBuffer toJsonStringPretty(StringBuffer sb, String prefix);

	/**
	 * Get size of this JSON object.
	 * <p>
	 * this returns,
	 * <ul>
	 * <li>String length if type is JsonType.VALUE,</li>
	 * <li>Array size if type is JsonType.ARRAY,</li>
	 * <li>Map size if type is JsonType.OBJECT.</li>
	 * </ul>
	 * 
	 * @return size of this JSON object.
	 */
	public abstract int size();

	/**
	 * Duplicates this object.
	 * <p>
	 * This method will creates brand new Json object which has no shared
	 * references with this object.
	 * 
	 * @return duplicated Json object.
	 */
	public abstract Json duplicate();

	/**
	 * Clear this object.
	 * <p>
	 * This method will applied to JsonType.OBJECT and JsonType.ARRAY. The
	 * object which is JsonType.VALUE type will have no change.
	 */
	public abstract void clear();

	/**
	 * Clear this object recursively.
	 * <p>
	 * This method have same behavior with clear() method except all of children
	 * of this object will be cleaned up recursively. Note!! Caller must ensure
	 * that have no references of the offspring of this object.
	 */
	public abstract void cleanUp();
}
