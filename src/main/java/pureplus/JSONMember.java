package pureplus;

public class JSONMember
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