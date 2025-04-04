package pureplus;

import java.util.ArrayList;

class JSONMember
{
	String	key;
	Object  value;

	public JSONMember(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object o) {
		this.value = o;
	}
}

public class JSONObject
{
	ArrayList<JSONMember>	members;

	public JSONObject() {
		members = new ArrayList<JSONMember>();
	}

	public void add(JSONMember mbr) {
		members.add(mbr);
	}

	public void add(String key, Object value) {
		members.add(new JSONMember(key, value));
	}

	public Object get(String key) {
		for (JSONMember m : members) {
			if (key.equals(m.getKey())) {
				return m.getValue();
			}
		}
		return null;
	}

	public int getInt(String key) {
		Object o;
		o = get(key);
		if (o instanceof Integer) {
			return (int)o;
		}
		return 0;
	}

	public double getDouble(String key) {
		Object o;
		o = get(key);
		if (o instanceof Double) {
			return (double)o;
		}
		return 0.0;
	}
}

