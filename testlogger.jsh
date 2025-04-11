import pureplus.*;
import java.util.Map;

SDLogger  sdlogger = new SDLogger();
sdlogger.setLogdir("logtest");

Map<String,Object>  obj = JSONParser.readJSONObject(new java.io.FileReader("out.json"));

sdlogger.writeResponse(obj);