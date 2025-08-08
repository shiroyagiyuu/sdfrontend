package pureplus;

import java.util.ArrayList;

public class SDLogFilter {
    ArrayList<SDLog>    srclog;
    String         filter;

    public SDLogFilter(ArrayList<SDLog> log) {
        this.srclog = log;
    }

    public ArrayList<SDLog> getFilteredList() {
        ArrayList<SDLog>  dstlog = new ArrayList<SDLog>();

        if (filter==null || filter.length()==0) return srclog;

        for (SDLog log : srclog) {
            String  p = log.getPrompt();
            if (p.indexOf(filter)>0) {
                dstlog.add(log);
            }
        }

        if (dstlog.size()==0) return srclog;

        return dstlog;
    }

    public void setFilter(String filter_str) {
        this.filter = filter_str;
    }

    /**
     * @return
     */
    public String getFilter() {
        return this.filter;
    }
}
