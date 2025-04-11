package pureplus;

import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Base64;

public class SDLogger {
    SDControlPanel  sp;
    SDClient        client;
    SDParam         param;

    File    logdir,logfile;
    int     image_num;

    void init() {
        client = new SDClient();
    }

    public void setParam(SDParam param) {
        this.param = param;
    }

    public void setLogdir(String path) {
        this.logdir = new File(path);

        /* check logdir */
        this.logfile = new File(logdir,"log.csv");

        try {
            if (!logfile.exists()) {
                FileWriter  wtr = new FileWriter(logfile);
                wtr.write("prompt,seed,width,height,sampler,cfgs,steps,filename,negative_prompt,sd_model_name,sd_model_hash\n");
                wtr.close();
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }

        String[]  chs = logdir.list();
        int  cnt = 0;
        for (int i=0; i<chs.length; i++) {
            if (chs[i].endsWith(".png")) { cnt++; }
        }
        image_num = cnt;
    }

    void writeLogLine(JSONObject param, String fname) {
        String  prompt = (String)param.get("prompt");
        long    seed = (long)param.get("seed");
        long    width = (long)param.get("width");
        long    height = (long)param.get("height");
        String  sampler = (String)param.get("sampler_name");
        long    cfgs = (long)(double)param.get("cfg_scale");
        long    steps = (long)param.get("steps");
        String  negative_prompt = (String)param.get("negative_prompt");
        String  sd_model_name = (String)param.get("sd_model_name");
        String  sd_model_hash = (String)param.get("sd_model_hash");

        StringBuilder  sb = new StringBuilder();
        sb.append('"').append(prompt).append("\",");
        sb.append(seed).append(",");
        sb.append(width).append(",");
        sb.append(height).append(",");
        sb.append('"').append(sampler).append("\",");
        sb.append(cfgs).append(",");
        sb.append(steps).append(",");
        sb.append(fname).append(",");
        sb.append('"').append(negative_prompt).append("\",");
        sb.append('"').append(sd_model_name).append("\",");
        sb.append('"').append(sd_model_hash).append("\"\n");
        
        try {
            FileWriter  wtr = new FileWriter(logfile, true);
            wtr.write(sb.toString());
            wtr.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    String writeImageData(String data, long seed) throws IOException {
        String  fname;
        Base64.Decoder  dec = Base64.getDecoder();

        fname = String.format("%05d-%d.png", image_num, seed);

        byte[] bdata = dec.decode(data);

        FileOutputStream  fos = new FileOutputStream(new File(logdir, fname));
        fos.write(bdata);
        fos.close();

        image_num++;

        return fname;
    }

    public void writeResponse(JSONObject obj) throws IOException {
        JSONArray   images = (JSONArray)obj.get("images");
        String      infostr = (String)obj.get("info");
        JSONObject  infoobj = JSONParser.readJSONObject(new StringReader(infostr));
        long        seed = (long)(infoobj.get("seed"));

        int  num = images.size();

        String fdata = (String)images.get(0);
        String img_name = writeImageData(fdata, seed);
        writeLogLine(infoobj, img_name);

        for (int i=1; i<num; i++) {
            try {
                String data = (String)images.get(i);

                writeImageData(data, seed);
            } catch (Exception ex) {
                System.out.println("skip: "+i);
                ex.printStackTrace();
            }
        }
    }

    public void request() {
        try {
            String resp = client.request(this.param);
            JSONObject  obj = JSONParser.readJSON(new StringReader(resp));

            writeResponse(obj);
        } catch(IOException ex) {
            ex.printStackTrace();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public SDLogger() {
    }
}
