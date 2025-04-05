package pureplus;

public class SDLoggerTest {
    public static void main(String[] args) throws Exception {
        SDLogger  sdlogger = new SDLogger();
        sdlogger.setLogdir("log");

        JSONParser  parser = JSONParser.getInstance();
        JSONObject  obj = parser.readJSON(new java.io.FileReader("out.json"));

        sdlogger.writeResponse(obj);

        /* 
        SDParam   param = new SDParam();

        param.setPrompt("masterpiece, (best quality:1.1), 1girl <lora:ill_apricot:1>");
        param.setNegativePrompt("");
        param.setSeed(-1);
        param.setSteps(20);
        param.setWidth(512);
        param.setHeight(512);
        param.setCfgs(7);
        param.setSampler("DPM++ 2M");

        sdlogger.setParam(param);

        sdlogger.request();
        */
    }
}
