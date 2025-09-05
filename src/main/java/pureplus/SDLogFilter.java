package pureplus;

import java.util.ArrayList;

public class SDLogFilter {
    ArrayList<SDLog>    srclog;
    String[]            filter;

    public SDLogFilter(ArrayList<SDLog> log) {
        this.srclog = log;
    }

    private boolean andFilter(String[] keyword, String prompt) {
        boolean  hit = true;

        for (int i=0; i<keyword.length; i++) {
            if (keyword[i].length()==0) continue;

            if (prompt.indexOf(keyword[i])<0) {
                hit = false;
                break;
            }
        }
        return hit;
    }

    public ArrayList<SDLog> getFilteredList() {
        ArrayList<SDLog>  dstlog = new ArrayList<SDLog>();

        if (filter==null || filter.length==0) return srclog;

        for (SDLog log : srclog) {
            if (andFilter(filter, log.getPrompt())) {
                dstlog.add(log);
            }
        }

        if (dstlog.size()==0) return srclog;

        return dstlog;
    }

    public void setFilter(String filter_str) {
        if (filter_str != null) {
            this.filter = filter_str.split(" ");
        } else {
            this.filter = null;
        }
    }

    /**
     * @return
     */
    public String getFilter() {
        return String.join(" ", this.filter);
    }
}
