package pureplus;

import java.util.LinkedList;
import java.io.*;

public class SDQueClient {
    private LinkedList<SDParam>     que;
    private Object  monitor;
    private SDClient  client;
    private SDLogger  logger;

    private RequestThread  th;
    boolean running;


    /**
     * add param to que
     * @param param キューに挿入するパラメーター
     */
    public void addRequestQue(SDParam param) {
        que.addLast(param);
        synchronized(monitor) {
            monitor.notify();
        }
    }

    /**
     * 実行結果を保存するディレクトリを指定する
     * @param path - ログを保存するディレクトリ
     */
    public void setLogdir(String path) {
        logger.setLogdir(path);
    }

    public void start() {
        if (th==null) {
            th = new RequestThread();
            th.start();
        }
    }

    public void stop() {
        running = false;
        synchronized(monitor) {
            monitor.notifyAll();
        }
    }

    class RequestThread extends Thread {
        @Override
        public void run() {
            while (running) {
                try {
                    if (que.size()>0) {
                        SDParam     param = que.pollFirst();

                        String      response = client.request(param);

                        JSONObject  resobj = JSONParser.readJSON(new StringReader(response));
                        logger.writeResponse(resobj);
                    }
                    else {
                            synchronized(monitor) {
                                monitor.wait();
                            }
                    }
                } catch(InterruptedException ex) {
                    ex.printStackTrace();
                } catch(IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public SDQueClient() {
        que = new LinkedList<SDParam>();
        logger = new SDLogger();
        monitor = new Object();
        client = new SDClient();
    }
}
