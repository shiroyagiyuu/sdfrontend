package pureplus;

import java.util.LinkedList;
import java.util.ArrayList;
import java.io.*;
import pureplus.json.JSONObject;
import pureplus.json.JSONParser;

public class SDQueClient {
    private LinkedList<JSONObject>     que;
    private Object  monitor;
    private SDClient  client;
    private SDLogger  logger;

    private RequestThread  th;
    boolean running;

    private ArrayList<SDQueListener>   listeners;

    /**
     * add param to que
     * @param param キューに挿入するパラメーター
     */
    public void addRequestQue(JSONObject param) {
        que.addLast(param);
        fireSDQueEvent(new SDQueEvent(SDQueEvent.ADD));
        synchronized(monitor) {
            monitor.notify();
        }
    }

    public int getQueSize() {
        return que.size();
    }

    public JSONObject getQueData(int idx) {
        return que.get(idx);
    }

    public void fireSDQueEvent(SDQueEvent ev) {
        for (SDQueListener  l : listeners) {
            l.changeSDQue(ev);
        }
    }

    public void addSDQueListener(SDQueListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void removeSDQueListener(SDQueListener l) {
        if (listeners.contains(l)) {
            listeners.remove(l);
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
                        JSONObject  param = que.peekFirst();

                        String      response = client.txt2img(param);

                        JSONObject  resobj = JSONParser.readJSON(new StringReader(response));
                        logger.writeResponse(resobj);

                        que.remove(param);
                        fireSDQueEvent(new SDQueEvent(SDQueEvent.REMOVE));
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
        que = new LinkedList<JSONObject>();
        logger = new SDLogger();
        monitor = new Object();
        client = new SDClient();

        listeners = new ArrayList<>();
    }
}
