package pureplus;

public class JSONParseErrorException extends java.io.IOException
{
	String   getStr,needStr;

	public JSONParseErrorException(String getstr, String needstr) {
		super("we need str: " + needstr + "  getstr: " + getstr);
		this.getStr = getstr;
		this.needStr = needstr;
	}
}
