package com.kt.gigagenie.pci.net.json;

import com.gnifrix.debug.GLog;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class JsonParser {

	private final static String JSON_SPACES = " \t\r\n";
	private final static String JSON_DELIMS = ":,[]{}";
	private static final String logHeader = "JsonParser";

	private String _input;
	private int _index, _inlen;

	private JsonParser(String input) {
		this(input, 0);
	}

	private JsonParser(String input, int offset) {
		_input = input;
		_index = offset;
		_inlen = input.length();
	}

	private void incIndex(int size) throws JsonException {
		_index += size;
		if (_index > _inlen)
			throw new JsonException("premature end of string.");
	}

	private void incIndex() throws JsonException {
		incIndex(1);
	}

	private void skipSpaces() throws JsonException {
		while (JSON_SPACES.indexOf(_input.charAt(_index)) > -1)
			incIndex();
	}

	private static int hexToInt(String s, int offset, int len) {
		return Integer.parseInt(s.substring(offset, offset + len), 16);
	}

	private void parseEscape(StringBuffer sb) throws JsonException {
		incIndex();
		switch (_input.charAt(_index)) {
		case 't':
			sb.append('\t');
			break;
		case 'r':
			sb.append('\r');
			break;
		case 'n':
			sb.append('\n');
			break;
		case 'u':
			sb.append((char) hexToInt(_input, _index + 1, 2));
			incIndex(2);
			sb.append((char) hexToInt(_input, _index + 1, 2));
			incIndex(2);
			break;
		default:
			sb.append(_input.charAt(_index));
		}
	}

	private StringBuffer parseString() throws JsonException {
		boolean isend = false;
		boolean isstquote = true;
		skipSpaces();
		if (_input.charAt(_index) != '\"')
			isstquote = false;
		else
			incIndex();
		StringBuffer sb = new StringBuffer();
		while (!isend) {
			char c = _input.charAt(_index);
			if (c == '\\') {
				parseEscape(sb);
			} else if (isstquote && c == '\"') {
				isend = true;
			} else if (!isstquote && JSON_DELIMS.indexOf(c) > -1) {
				isend = true;
				break;
			} else {
				sb.append(c);
			}
			incIndex();
		}
		return sb;
	}

	private Json parseValue() throws JsonException {
		skipSpaces();
		if (_input.charAt(_index) == '\"') {
			StringBuffer sb = parseString();
			return new JsonValue(sb.toString());
		}
		StringBuffer sb = new StringBuffer();
		char c;
		while (JSON_DELIMS.indexOf((c = _input.charAt(_index))) < 0) {
			if (c == '\\') {
				parseEscape(sb);
			} else {
				sb.append(c);
			}
			incIndex();
		}
		if (_input.length() > _index) {
			// incIndex();
			return new JsonValue(sb.toString());
		} else {
			throw new JsonException("parse error at " + _index);
		}
	}

	private Json parseObject() throws JsonException {
		boolean isend = false;
		skipSpaces();
		if (_input.charAt(_index) != '{')
			throw new JsonException("parse error at " + _index + ": object not starts with '{'.");
		JsonObject obj = new JsonObject();
		incIndex();
		skipSpaces();
		if (_input.charAt(_index) == '}') {
			incIndex();
			isend = true;
		}
		while (!isend) {
			String name = parseString().toString();
			skipSpaces();
			if (_input.charAt(_index) != ':')
				throw new JsonException("parse error at " + _index + ": name ended but next character is not ':' in JsonObject.");
			incIndex();
			skipSpaces();
			obj.put(name, parse());
			skipSpaces();
			char c = _input.charAt(_index);
			if (c == ',') {
				incIndex();
			} else if (c == '}') {
				incIndex();
				isend = true;
			} else {
				throw new JsonException("parse error at " + _index + ": next character must be ',' or '}' in JsonObject.");
			}
		}
		return obj;
	}

	private Json parseArray() throws JsonException {
		boolean isend = false;
		skipSpaces();
		if (_input.charAt(_index) != '[')
			throw new JsonException("parse error at " + _index + ": array not starts with '['.");
		JsonArray arr = new JsonArray();
		incIndex();
		skipSpaces();
		char c = _input.charAt(_index);
		if (c == ']') {
			incIndex();
			isend = true;
		}
		while (!isend) {
			arr.add(parse());
			skipSpaces();
			c = _input.charAt(_index);
			if (c == ',') {
				incIndex();
			} else if (c == ']') {
				incIndex();
				isend = true;
			} else {
				throw new JsonException("parse error at " + _index + ": next character must be ',' or ']' in JsonArray.");
			}
		}
		return arr;
	}

	private Json parse() throws JsonException {
		try {
			skipSpaces();
			switch (_input.charAt(_index)) {
			case '{':
				return parseObject();
			case '[':
				return parseArray();
			default:
				return parseValue();
			}
		} catch (JsonException e) {
			throw e;
		} catch (Exception e) {
			throw new JsonException("parse error at " + _index, e);
		}
	}

	public static Json parse(String input) throws JsonException {
		JsonParser parser = new JsonParser(input);
		return parser.parse();
	}

	public static void main(String[] args) throws IOException, JsonException {

		FileInputStream fis = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			fis = new FileInputStream(args[0]);  /** KISA 보완조치 by dalkommjk | 2019-10-30 */
			byte buffer[] = new byte[1024];
			int rsize = 0; /** KISA 보완조치 by dalkommjk | 2019-10-30 */
			while ((rsize = fis.read(buffer)) > -1) {
				if (rsize > 0) {
					baos.write(buffer, 0, rsize);
				}
			}
			String src = new String(baos.toByteArray());
			GLog.printInfo(logHeader, "Original:" + src);
			GLog.printInfo(logHeader, "Parsed  :" + parse(src).toJsonStringPretty());
		}

		finally {
			try {
				fis.close();
			} catch (Exception ignored) {
				ignored.printStackTrace();
			}
		}
	}
}