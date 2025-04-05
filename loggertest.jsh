import pureplus.*;

SDLogger  sdlogger = new SDLogger();
sdlogger.setLogdir("log");

SDLogger  sdlogger = new SDLogger();
sdlogger.setLogdir("log");

JSONParser  parser = JSONParser.getInstance();
JSONObject  obj = parser.readJSON(new java.io.FileReader("out.json"));

sdlogger.writeResponse(obj);