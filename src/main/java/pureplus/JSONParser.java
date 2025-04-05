package pureplus;

import java.io.*;

public class JSONParser
{
	static JSONParser  instance = null;

	boolean debug = false;

	/**
	 * skip Whitespace
	 * @param rd
	 * @throws IOException
	 */
	private void readWhiteSpace(PushbackReader rd) throws IOException {
		int  c;

		c = rd.read();
		while (c>0) {
			switch (c) {
				case '\r':
				case '\n':
				case ' ':
				case '\t':
					c = rd.read();
					break;
				default:
					rd.unread(c);
					return;
			}
		}
	}

	/**
	 * Stringをリードする。
	 */
	private String readString(PushbackReader rd) throws IOException {
		int  c;
		boolean  escape;
		StringBuilder  sb = new StringBuilder();
		debugprint("String: ");

		c = rd.read();
		if (c!='\"') throw new JSONParseErrorException(String.valueOf(c), "\"");

		escape = false;
		while(c>=0) {
			c = rd.read();

			if (escape) {
				escape = false;
				sb.append((char)c);
			} else {
				if (c=='"') {
					break;
				} else if (c=='\\') {
					sb.append((char)c);
					escape = true;
				} else {
					sb.append((char)c);
				}
			}
		}
		debugprint(sb.toString());
		return sb.toString();
	}

	/**
	 * check, is char Number
	 * @param c
	 * @return
	 */
	private boolean isNumber(int c) {
		if ('0'<=c && c<='9') return true;
		if (c=='.') return true;
		if (c=='-') return true;

		return false;
	}

	/**
	 * read Number
	 * @param rd
	 * @return
	 * @throws IOException
	 */
	private Object readNumber(PushbackReader rd) throws IOException {
		int c;
		StringBuilder  sb = new StringBuilder();
		debugprint("Number");

		c = rd.read();
		while(c>=0 && isNumber(c)) {
			sb.append((char)c);
			c = rd.read();
		}
		rd.unread(c);

		if (sb.indexOf(".")>0) {
			return Double.valueOf(sb.toString());
		} else {
			return Integer.valueOf(sb.toString());
		}
	}

	/**
	 * Read Keyword. true,false,null
	 * @param rd
	 * @return JSON Object
	 * @throws IOException
	 */
	private static Object readKeyword(PushbackReader rd) throws IOException {
		StringBuilder sb = new StringBuilder();

		sb.append((char)rd.read());
		sb.append((char)rd.read());
		sb.append((char)rd.read());
		sb.append((char)rd.read());

		if ("null".equals(sb.toString())) {
			return null;
		}
		else if ("true".equals(sb.toString())) {
			return Boolean.TRUE;
		}

		sb.append((char)rd.read());

		if ("false".equals(sb.toString())) {
			return Boolean.FALSE;
		}

		throw new JSONParseErrorException(sb.toString(), "true,false,null");
	}

	/**
	 * Read member
	 * @param rd
	 * @return member (key:value)
	 * @throws IOException
	 */
	private JSONMember readMember(PushbackReader rd) throws IOException {
		int  c;
		String  key;
		Object  value;
		debugprint("Member <");

		key = readString(rd);

		readWhiteSpace(rd);
		c = rd.read();
		if (c!=':') throw new JSONParseErrorException(String.valueOf(c), ":");
		
		readWhiteSpace(rd);

		value = readElement(rd);

		debugprint("> Member");
		return new JSONMember(key, value);
	}

	/**
	 * readElement (value)
	 * @param rd
	 * @return
	 * @throws IOException
	 */
	Object readElement(PushbackReader rd) throws IOException {
		int     c;
		Object  obj;

		debugprint("Element <");
		readWhiteSpace(rd);

		c = rd.read();
		rd.unread(c);

		if (c=='{') {
			obj = readObject(rd);
		}
		else if (c=='[') {
			obj = readArray(rd);
		}
		else if(c == '\"') {
			obj = readString(rd);
		}
		else if (isNumber(c)) {
			obj = readNumber(rd);
		}
		else {
			obj = readKeyword(rd);
		}

		readWhiteSpace(rd);

		debugprint("> Element");
		return obj;
	}

	/**
	 * @param rd
	 * @return
	 * @throws IOException
	 */
	JSONArray readArray(PushbackReader rd) throws IOException {
		int  c;
		JSONArray   jsonary = new JSONArray();

		debugprint("Array <");
		c = rd.read();

		if (c!='[') throw new JSONParseErrorException(String.valueOf(c), "[");

		readWhiteSpace(rd);
		c = rd.read();

		if (c==']') {
			return jsonary;
		}

		rd.unread(c);

		while (c>0) {
			jsonary.add(readElement(rd));

			c = rd.read();

			if (c==',') {
				; //do nothing;
			}
			else if(c==']') {
				debugprint("> Array");
				return jsonary;	//end of Array
			}
			else {
				throw new JSONParseErrorException(String.valueOf(c), ", or ]");
			}
		}

		//unexpected end
		return jsonary;
	}

	/**
	 * read JSONObject
	 * @param rd
	 * @return
	 * @throws IOException
	 */
	JSONObject readObject(PushbackReader rd) throws IOException {
		int  c;
		JSONObject   jsonobj = new JSONObject();

		debugprint("Object <");
		c = rd.read();

		if (c!='{') throw new JSONParseErrorException(String.valueOf(c), "{");

		
		readWhiteSpace(rd);
		c = rd.read();

		if (c=='}') {
			return jsonobj;
		}

		rd.unread(c);

		while (c>0) {
			jsonobj.add(readMember(rd));
			c = rd.read();

			if (c==',') {
				; //do nothing;
			}
			else if(c=='}') {
				debugprint("> Object");
				return jsonobj;	//end of Object
			}
			else {
				throw new JSONParseErrorException(String.valueOf(c), ", or }");
			}
		}

		//unexpected end
		return jsonobj;
	}

	public JSONObject readJSON(Reader  rd) throws IOException {
		PushbackReader prd = new PushbackReader(rd);

		readWhiteSpace(prd);
		return readObject(prd);
	}

	/**
	 * get Instance(singleton)
	 * @return
	 */
	public static JSONParser getInstance() {
		if (JSONParser.instance==null) {
			JSONParser.instance = new JSONParser(false);
		}

		return JSONParser.instance;
	}

	/**
	 * debug print
	 * @param msg
	 */
	private void debugprint(String msg) {
		if (debug) {
			System.out.println(msg);
		}
	}

	/**
	 * constructor
	 * @param debug
	 */
	JSONParser(boolean debug) {
		this.debug = debug;
	}

	public static void main(String[] args) {
		try {
			File  file = new File(args[0]);
			JSONParser  parser = JSONParser.getInstance();
			parser.readJSON(new FileReader(file));
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
}
