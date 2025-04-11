import pureplus.*;
import java.util.Base64;
import java.io.*;

JSONParser  parser = JSONParser.getInstance();

JSONObject obj = parser.readJSON(new FileReader(new File("out.json")))

JSONArray ary = (JSONArray)obj.get("images")

String data = (String)ary.get(0)

Base64.Decoder  dec = Base64.getDecoder();

byte[] bdata = dec.decode(data);

OutputStream  os = new FileOutputStream(new File("out.png"))
os.write(bdata);
os.close()
