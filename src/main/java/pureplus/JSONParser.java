package pureplus;

import java.io.*;

public class JSONParser
{
	private static void readWhiteSpace(PushbackReader rd) throws IOException {
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
	private static String readString(PushbackReader rd) throws IOException {
		int  c;
		boolean  escape;
		StringBuilder  sb = new StringBuilder();
		System.out.print("String: ");

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
		System.out.println(sb.toString());
		return sb.toString();
	}

	private static boolean isNumber(int c) {
		if ('0'<=c && c<='9') return true;
		if (c=='.') return true;
		if (c=='-') return true;

		return false;
	}

	private static Object readNumber(PushbackReader rd) throws IOException {
		int c;
		StringBuilder  sb = new StringBuilder();
		System.out.println("Number");

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

	private static Object readKeyword(PushbackReader rd) throws IOException {
		int  c;
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

	private static JSONMember readMember(PushbackReader rd) throws IOException {
		int  c;
		String  key;
		Object  value;
		System.out.println("Member <");

		key = readString(rd);

		readWhiteSpace(rd);
		c = rd.read();
		if (c!=':') throw new JSONParseErrorException(String.valueOf(c), ":");
		
		readWhiteSpace(rd);

		value = readElement(rd);

		System.out.println("> Member");
		return new JSONMember(key, value);
	}

	static Object readElement(PushbackReader rd) throws IOException {
		int     c;
		Object  obj;

		System.out.println("Element <");
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

		System.out.println("> Element");
		return obj;
	}

	static JSONArray readArray(PushbackReader rd) throws IOException {
		int  c;
		JSONArray   jsonary = new JSONArray();

		System.out.println("Array <");
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
				System.out.println("> Array");
				return jsonary;	//end of Array
			}
			else {
				throw new JSONParseErrorException(String.valueOf(c), ", or ]");
			}
		}

		//unexpected end
		return jsonary;
	}

	static JSONObject readObject(PushbackReader rd) throws IOException {
		int  c;
		JSONObject   jsonobj = new JSONObject();

		System.out.println("Object <");
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
				System.out.println("> Object");
				return jsonobj;	//end of Object
			}
			else {
				throw new JSONParseErrorException(String.valueOf(c), ", or }");
			}
		}

		//unexpected end
		return jsonobj;
	}

	public static JSONObject readJSON(Reader  rd) throws IOException {
		PushbackReader prd = new PushbackReader(rd);

		readWhiteSpace(prd);
		return readObject(prd);
	}

	public static void main(String[] args) {
		try {
			File  file = new File(args[0]);
			JSONParser.readJSON(new FileReader(file));
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
}
