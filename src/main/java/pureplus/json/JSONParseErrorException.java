package pureplus.json;

public class JSONParseErrorException extends java.io.IOException
{
	public JSONParseErrorException(String getstr, String needstr) {
		super(String.format("need: [%s]  get: [%s]", needstr, getstr));
	}

	public JSONParseErrorException(char getc, char needc) {
		this(String.valueOf(getc), String.valueOf(needc));
	}
}
