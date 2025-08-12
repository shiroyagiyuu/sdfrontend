package pureplus;

import javax.swing.*;
import javax.swing.table.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

class TagData implements Comparable<TagData> {
    String  tagName;

    public String getTagName() {
        return tagName;
    }

    int     count;

    public int getCount() {
        return count;
    }

    public TagData(String tag, int count) {
        this.tagName = tag;
        this.count  = count;
    }

    @Override
    public int compareTo(TagData t) {
        return (this.count - t.getCount());
    }
}

class TagListModel extends AbstractTableModel
{
    TagData[]  tagList;

    @Override
    public Object getValueAt(int row, int col) {
        if (row<tagList.length) {
            TagData   tag = tagList[row];
            switch (col) {
                case 0:
                    return row;
                case 1:
                    return tag.getTagName();
                case 2:
                    return tag.getCount();
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public int getRowCount() {
        return tagList.length;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int col) {
        switch(col) {
            case 0:
            return "#";
            case 1:
            return "Name";
            case 2:
            return "Count";
            default:
            return super.getColumnName(col);
        }
    }

    public TagListModel(TagData[] data) {
        this.tagList = data;
    }
}

public class SDPromptStatistics {
    TagListModel    model;
    JTable          table;
    JFrame          frame = null;

    public void createWindow() {
        if (model != null) {
            if (frame == null) {
                table = new JTable(model);
                JScrollPane  sptable = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

                frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setContentPane(sptable);
            }
        } else {
            System.out.println("Nothing to Do...(PromptStatistics)");
        }
    }

    public void setPrompts(String[] prompt_list) {
        HashMap<String,Integer>  hmap = new HashMap<>();
        ArrayList<TagData>   tlist;

        for (String pr : prompt_list) {
            String[] tags = pr.split(",");
            for (String t : tags) {
                t = t.trim();
                if (t.length()>0) {
                    if (hmap.containsKey(t)) {
                        int v = hmap.get(t);
                        v++;
                        hmap.put(t,v);
                    } else {
                        hmap.put(t,1);
                    }
                }
            }
        }

        tlist = new ArrayList<TagData>(hmap.size());

        for (String tag : hmap.keySet()) {
            tlist.add(new TagData(tag, hmap.get(tag)));
        }

        Collections.sort(tlist, Collections.reverseOrder());
        model = new TagListModel(tlist.toArray(new TagData[tlist.size()]));

        if (table!=null) {
            table.setModel(model);
        }
    }

    public void setVisible(boolean vis) {
        frame.setVisible(vis);
    }
}
