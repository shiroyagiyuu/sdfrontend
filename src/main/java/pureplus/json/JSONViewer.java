package pureplus.json;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JTree;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;

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
    JTree       tree;
    JSONObject  jsonobj;

    JTextArea   ta;

    JFileChooser  fchooser;

    public void openFile() {
        if (fchooser == null) {
            fchooser = new JFileChooser();
        }

        int  res = fchooser.showOpenDialog(frm);

        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                load(fchooser.getSelectedFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void quit() {
        frm.dispose();
        System.exit(0);
    }

    public JMenuBar initMenuBar() {
        JMenuBar  menubar = new JMenuBar();
        
        JMenu   filemenu = new JMenu("File");
        JMenuItem    mopen = new JMenuItem("Open");
        mopen.addActionListener(e -> {
            openFile();
        });
        filemenu.add(mopen);

        JMenuItem    mquit = new JMenuItem("Quit");
        mquit.addActionListener(e -> {
            quit();
        });
        filemenu.add(mquit);

        menubar.add(filemenu);

        return menubar;
    }

    public void init() {
        frm = new JFrame("JSON Viewer");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        tree = new JTree();
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
        frm.setJMenuBar(initMenuBar());
        frm.setSize(new Dimension(600,400));

        frm.setVisible(true);
    }

    Reader getSafetensorReader(File file) throws IOException {
        ByteBuffer  sizebuf = ByteBuffer.allocate(8);
        FileInputStream   fis = new FileInputStream(file);
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
        return new InputStreamReader(new ByteArrayInputStream(cont));
    }

    public void load(Reader rd) throws IOException {
        jsonobj = JSONParser.readJSONObject(rd);
        tree.setModel(new JSONTreeModel(jsonobj));
    }

    public void load(InputStream in) throws IOException {
        load(new InputStreamReader(in));
    }

    public void load(File file) throws IOException {
        if (file.getName().endsWith(".safetensors")) {
            System.out.println("read SafeTensors");
            Reader rd = getSafetensorReader(file);
            load(rd);
        } else {
            System.out.println("read JSON");
            load(new FileReader(file));
        }
    }

    public void load(String path) throws IOException {
        load(new File(path));
    }

    public static void main(String[] args) {
        JSONViewer  viewer = new JSONViewer();

        try {
            viewer.init();
            if (args.length>1) {
                viewer.load(args[0]);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
