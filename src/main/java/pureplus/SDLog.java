package pureplus;

public class SDLog extends SDParam
{
	String  sdModelName;
	String  sdModelHash;

	String	filename;

	public SDLog() {
	}

	public void setSDModelName(String modelname) {
		this.sdModelName = modelname;
	}

	public String getSDModelName() {
		return this.sdModelName;
	}

	public void setSDModelHash(String modelhash) {
		this.sdModelHash = modelhash;
	}

	public String getSDModelHash() {
		return this.sdModelHash;
	}

	public void setFilename(String fname) {
		this.filename = fname;
	}

	public String getFilename() {
		return this.filename;
	}
}

