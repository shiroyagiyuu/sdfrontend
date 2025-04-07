package pureplus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.time.Duration;

public class SDClient {
    HttpClient  client;
    String      host = "http://127.0.0.1:7860"; 

    public SDClient() {
        client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofMinutes(120))
        .build();
    }

    void appendKeyPair(StringBuilder sb, String key, String value, boolean last) {
        sb.append("\"");
        sb.append(key);
        sb.append("\" : \"");
        sb.append(value);
        sb.append("\"");

        if (last) {
            sb.append("\n");
        } else {
            sb.append(",\n");
        }
    }

    void appendKeyPair(StringBuilder sb, String key, String value) {
        appendKeyPair(sb, key, value, false);
    }

    void appendKeyPair(StringBuilder sb, String key, int value, boolean last) {
        sb.append("\"");
        sb.append(key);
        sb.append("\" : ");
        sb.append(String.valueOf(value));

        if (last) {
            sb.append("\n");
        } else {
            sb.append(",\n");
        }
    }

    void appendKeyPair(StringBuilder sb, String key, int value) {
        appendKeyPair(sb, key, value, false);
    }

    public String paramToJson(SDParam param) {
        StringBuilder  sb = new StringBuilder();

        sb.append("{\n");
        appendKeyPair(sb, "prompt",param.getPrompt());
        appendKeyPair(sb, "negative_prompt",param.getNegativePrompt());
        appendKeyPair(sb, "seed", (int)param.getSeed());
        appendKeyPair(sb, "steps", param.getSteps());
        appendKeyPair(sb, "width", param.getWidth());
        appendKeyPair(sb, "height", param.getHeight());
        appendKeyPair(sb, "cfg_scale", param.getCfgs());
        appendKeyPair(sb, "sampler_name", param.getSampler());
        appendKeyPair(sb, "n_iter", 1);
        appendKeyPair(sb, "batch_size", 1);
        sb.append("\"save_images\":true");

        sb.append("}");

        return sb.toString();
    }

    public String request(SDParam param) throws IOException, InterruptedException {
        try {
            String reqjson = paramToJson(param);
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
        SDParam   param = new SDParam();

        param.setPrompt("masterpiece, (best quality:1.1), 1girl <lora:lora_model:1>");
        param.setNegativePrompt("");
        param.setSeed(1);
        param.setSteps(20);
        param.setWidth(512);
        param.setHeight(512);
        param.setCfgs(7);
        param.setSampler("DPM++ 2M");
        
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
