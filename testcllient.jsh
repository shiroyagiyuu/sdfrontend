import pureplus.*;

SDQueClient  client = new SDQueClient();
SDParam   param = new SDParam();

param.setPrompt("masterpiece, (best quality:1.1), 1 girl");
param.setNegativePrompt("");
param.setSeed(-1);
param.setSteps(20);
param.setWidth(512);
param.setHeight(512);
param.setCfgs(7);
param.setSampler("DPM++ 2M");

//String resp = client.request(param);

client.setLogdir("log");

client.addRequestQue(param);

SDParam   param2 = new SDParam();
param2.setPrompt("masterpiece, (best quality:1.1), 1 girl, <lora:ill_milfeulle:1>")
param2.setNegativePrompt("");
param2.setSeed(-1);
param2.setSteps(20);
param2.setWidth(512);
param2.setHeight(512);
param2.setCfgs(7);
param2.setSampler("DPM++ 2M");

client.addRequestQue(param2);

client.start();