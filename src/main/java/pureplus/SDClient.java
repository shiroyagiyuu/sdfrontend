package pureplus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.time.Duration;

import pureplus.json.JSONObject;
import pureplus.json.JSONWriter;

public class SDClient {
    HttpClient  client;
    String      host = "http://127.0.0.1:7860"; 

    public SDClient() {
        client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofMinutes(120))
        .build();
    }

    public String request(JSONObject param) throws IOException, InterruptedException {
        try {
            String reqjson = JSONWriter.toJSON(param);
            String uri = host + "/" + "sdapi/v1/txt2img";

            HttpRequest  request = HttpRequest.newBuilder(new URI(uri))
            .POST(HttpRequest.BodyPublishers.ofString(reqjson))
            .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.statusCode());
            return response.body();
        } catch(java.net.URISyntaxException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) throws Exception {
        SDClient  client = new SDClient();
        JSONObject  param = new JSONObject();

        param.add("prompt","masterpiece, (best quality:1.1), 1 girl, <lora:ga_aplicot:1>");
        param.add("negative_prompt","");
        param.add("seed",1);
        param.add("steps",20);
        param.add("width",512);
        param.add("height",512);
        param.add("cfg_scale",7);
        param.add("sampler_name","DPM++ 2M");
        param.add("n_iter",1);
        param.add("batch_size",1);
        param.add("save_images", false);
        
        String resp = client.request(param);

        try {
            File outfile = new File("out.json");
            FileOutputStream  fos = new FileOutputStream(outfile);
            fos.write(resp.getBytes());
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
