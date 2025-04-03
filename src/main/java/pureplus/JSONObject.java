package pureplus;

import java.io.*;
import java.util.HashMap;

public class JSONMap
{
	private boolean isWhiteSpace(int c) {
		switch (c) {
		case ' ':
		case '\t':
			return true;
		}
		return false;
	}

	/**
	 * Stringをリードする。最初の'"'は読み込み済みであること
	 */
	private String parseString(PushbackReader rd) throws IOException {
		int  c;
		StringBuilder  sb = new StringBuilder();

		c = rd.read();
		while(c!='\"' && c>=0) {
			sb.append(c);
			c = rd.read();
		}
		return sb.toString();
	}

	private boolean isNumber(int c) {
		if ('0'<=c && c<='9') return true;
		if (c=='.') return true;

		return false;
	}

	private Object parseNumber(PushbackReader rd) throws IOException {
		int c;
		StringBuilder  sb = new StringBuilder();

		c = rd.read();
		while(c>=0 && isNumber(c)) {
			sb.append(c);
			c = rd.read();
		}
		rd.unread(c);

		if (sb.indexOf(".")>0) {
			return Double.valueOf(sb.toString());
		} else {
			return Integer.valueOf(sb.toString());
		}
	}

	private void parseMember(PushbackReader rd, HashMap<String,Object> map) throws IOException {
		int  c;
		String  key;
		Object  val;

		c = rd.read();

		//if (c!='\"') parse error?

		key = parseString(rd);

		while(isWhiteSpace(c)) { c = rd.read(); }
		//if (c!=':') error
		
		c = rd.read();
		
		while(isWhiteSpace(c)) { c = rd.read(); }

		if(c == '\"') {
			val = parseString(rd);
		} else {
			rd.unread(c);
			val = parseNumber(rd);
		}

		map.put(key,val);
	}

	HashMap<String,Object> parseObject(PushbackReader rd) throws IOException {
		int  c;
		HashMap<String,Object>   map = new HashMap<String,Object>();

		c = rd.read();
		while (isWhiteSpace(c)) { c = rd.read(); }

		if (c!='{') {
			System.out.println("parse error?");
		}

		c = rd.read();
		
		while (c>=0) {
			if (isWhiteSpace(c)) {
				//skip
			}
			else if(c==',') {
				//do nothing.
			}
			else if(c=='}') {
				return map;
			}
			else {
				rd.unread(c);
				parseMember(rd, map);
			}
			c = rd.read();
		}

		return map;
	}
}
