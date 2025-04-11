package pureplus;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import java.util.ArrayList;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

class JSONTreeModel implements TreeModel
{
    ArrayList<TreeModelListener>    listeners;
    JSONMember   root;

    @Override
    public int getChildCount(Object o) {
        if (o instanceof JSONMember) {
            JSONMember    pmem = (JSONMember)o;
            Object    pobj = pmem.getValue();
            if (pobj instanceof JSONObject) {
                JSONObject  jsonobj = (JSONObject)pobj;
                return jsonobj.size();
            } else if (pobj instanceof JSONArray) {
                JSONArray ary = (JSONArray)pobj;
                return ary.size();
            }
            else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public int getIndexOfChild(Object parent, Object ch) {
        JSONMember    pmem = (JSONMember)parent;
        Object    pobj = pmem.getValue();

        if (pobj instanceof JSONObject) {
            JSONObject  jsonobj = (JSONObject)pobj;

            int   n;
            n = jsonobj.size();

            for (int i=0; i<n; i++) {
                JSONMember    mem = jsonobj.members.get(i);
                if (mem == ch) {
                    return i;
                }
            }

            return -1;
        } else if (pobj instanceof JSONArray) {
            JSONArray  ary = (JSONArray)pobj;
            int  n = ary.size();

            for (int i=0; i<n; i++) {
                if (ary.get(i)==ch) {
                    return i;
                }
            }

            return -1;
        } else {
            return -1;
        }
    }

    @Override
    public Object getChild(Object parent, int n) {
        if (parent instanceof JSONMember) {
            JSONMember pmem = (JSONMember)parent;
            Object pobj = pmem.getValue();

            if (pobj instanceof JSONObject) {
                JSONObject    jsonobj = (JSONObject)pobj;
                JSONMember    chmem = jsonobj.members.get(n);

                return chmem;
            } else if (pobj instanceof JSONArray) {
                JSONArray  ary = (JSONArray)pobj;
                return new JSONMember(Integer.toString(n), ary.get(n));
            }
        }
        return null;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    @Override
    public boolean isLeaf(Object o) {
        if (o instanceof JSONMember) {
            JSONMember  pmem = (JSONMember)o;
            Object  cobj = pmem.getValue();

            if (cobj instanceof JSONObject) {
                return false;
            } else if (cobj instanceof JSONArray) {
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object o) {
        ;
    }

    @Override
    public Object getRoot() {
        return this.root;
    }

    public JSONTreeModel(JSONObject jsonobj) {
        this.listeners = new ArrayList<TreeModelListener>();

        this.root = new JSONMember("root", jsonobj);
    }
}

class JSONTreeRenderer extends DefaultTreeCellRenderer
{
    @Override
    public Component getTreeCellRendererComponent(JTree tree,
                                       Object value,
                                       boolean selected,
                                       boolean expanded,
                                       boolean leaf,
                                       int row,
                                       boolean hasFocus) {
            String name;
            if (value instanceof JSONMember) {
                JSONMember  m = (JSONMember)value;
                Object      v = m.getValue();
                if (v instanceof JSONObject) {
                    name = m.getKey() + " : {}";
                } else if (v instanceof JSONArray) {
                    name = m.getKey() + " : []";
                } else {
                    name = m.getKey();// + " : " + v.toString();
                }
            } else if (value != null) {
                name = value.toString();
            } else {
                name = "null";
            }
            return super.getTreeCellRendererComponent(tree, name, selected, expanded, leaf, row, hasFocus);
    }
}

public class JSONViewer {
    JFrame      frm;
    JSONObject  jsonobj;

    JTextArea   ta;

    public void init() {
        frm = new JFrame("JSON Viewer");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JSONTreeModel  model = new JSONTreeModel(jsonobj);
        JTree  tree = new JTree(model);
        tree.setCellRenderer(new JSONTreeRenderer());
        tree.setEditable(false);
        JScrollPane   tree_scpane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        ta = new JTextArea();
        ta.setLineWrap(true);
        JScrollPane   ta_scpane = new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Object obj = e.getPath().getLastPathComponent();
                if (obj instanceof JSONMember) {
                    JSONMember  mem = (JSONMember)obj;
                    Object  value = mem.getValue();
                    if (value != null) {
                        ta.setText(value.toString());
                    } else {
                        ta.setText("null");
                    }
                }
            }
        });

        JSplitPane  splpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tree_scpane, ta_scpane);

        frm.setContentPane(splpane);
        frm.pack();

        frm.setVisible(true);
    }

    public void load(File file) throws IOException {
        jsonobj = JSONParser.readJSONObject(new FileReader(file));
    }

    public void load(InputStream in) throws IOException {
        jsonobj = JSONParser.readJSONObject(new InputStreamReader(in));
    }

    public static void main(String[] args) {
        JSONViewer  viewer = new JSONViewer();

        if (args[0].endsWith(".safetensors")) {
            System.out.println("read SafeTensors");
            try {
                ByteBuffer  sizebuf = ByteBuffer.allocate(8);
                FileInputStream   fis = new FileInputStream(new File(args[0]));
                FileChannel       fch = fis.getChannel();
                fch.read(sizebuf);

                sizebuf.order(ByteOrder.LITTLE_ENDIAN);
                int  csize = (int)(sizebuf.flip().getLong());
                System.out.println("csize="+csize);

                ByteBuffer   contbuf = ByteBuffer.allocate(csize);
                fch.read(contbuf);

                fch.close();
                fis.close();

                byte[]  cont = new byte[csize];
                contbuf.flip().get(cont);
                viewer.load(new ByteArrayInputStream(cont));
                viewer.init();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("read JSON");
            try {
                viewer.load(new File(args[0]));
                viewer.init();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
