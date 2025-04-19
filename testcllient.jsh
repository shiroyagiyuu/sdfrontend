import pureplus.*;

SDQueClient  client = new SDQueClient();
JSONObject   param = new JSONObject();

param.add("prompt", "masterpiece, (best quality:1.1), 1 girl");
param.add("negatime_prompt","");
param.add("seed", -1);
param.add("steps", 20);
param.add("width", 512);
param.add("height", 512);
param.add("cfg_scale", 7);
param.add("sampler_name", "DPM++ 2M");

//String resp = client.request(param);

client.setLogdir("log");

client.addRequestQue(param);

JSONObject   param2 = new JSONObject();
param2.add("prompt", "masterpiece, (best quality:1.1), 1 girl, <lora:ill_milfeulle:1>")
param2.add("negatime_prompt","");
param2.add("seed", -1);
param2.add("steps", 20);
param2.add("width", 512);
param2.add("height", 512);
param2.add("cfg_scale", 7);
param2.add("sampler_name", "DPM++ 2M");

client.addRequestQue(param2);

client.start();