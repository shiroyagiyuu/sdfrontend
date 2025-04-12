package pureplus.json;

import java.util.ArrayList;

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

	public int size() {
		return members.size();
	}
}

