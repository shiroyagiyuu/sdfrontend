package pureplus;

import java.util.ArrayList;

public class JSONArray
{
	ArrayList<Object> ary;

	public JSONArray() {
		ary = new ArrayList<Object>();
	}

	public void add(Object o) {
		ary.add(o);
	}

	public Object get(int i) {
		return this.ary.get(i);
	}
}

