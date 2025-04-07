package pureplus;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class SDLogView
{
	SDControlPanel		ctrl;
	JTextField			fld_curr;
	JLabel				max_label;
	SDImageView			imageview;
	ArrayList<SDLog>	log;
	
	File				basedir;
	File				logfile;
	int  curr_row;

	public String[] parseCSV(String l) {
		ArrayList<String>  ary = new ArrayList<String>();
		int  len = l.length();
		StringBuilder  sb;

		System.out.println(l);
		sb = new StringBuilder();
		for (int n=0;n<len;n++) {
			char c = l.charAt(n);
			switch (c) {
			case '"':
				n++;
				sb = new StringBuilder();
				c = l.charAt(n++);
				while (c!='"' && n<len-1) {
					sb.append(c);
					c = l.charAt(n++);
				//	System.out.println(sb);
				}
				ary.add(sb.toString());
				sb = new StringBuilder();
				c = l.charAt(n++);
				if (c!=',') {
					System.out.println("parse error?");
					return null;
				}
				break;
			case ',':
				ary.add(sb.toString());
				sb = new StringBuilder();
				break;
			default:
				sb.append(c);
			}
		}
		ary.add(sb.toString());

		return ary.toArray(new String[ary.size()]);
	}

	public SDLog parseLine(CSVReader csv) throws IOException {
		String[]  tk = csv.readRow();
		SDLog p = new SDLog();

		//debug
		/*
		for (int i=0;i<tk.length;i++) {
			System.out.println(""+i+":"+tk[i]);
		}
		System.out.println();
		*/

		if (tk.length>10) {
			p.setPrompt(tk[0]);
			p.setSeed(Long.parseLong(tk[1]));
			p.setWidth(Integer.parseInt(tk[2]));
			p.setHeight(Integer.parseInt(tk[3]));
			p.setSampler(tk[4]);
			p.setCfgs(Integer.parseInt(tk[5]));
			p.setSteps(Integer.parseInt(tk[6]));
			p.setFilename(tk[7]);
			p.setNegativePrompt(tk[8]);
			p.setSDModelName(tk[9]);
			p.setSDModelHash(tk[10]);

			return p;
		}

		return null;
	}

	public ArrayList<SDLog> readLog(Reader rd) {
		CSVReader  csv = new CSVReader(rd);
		int     num=0;	

		ArrayList<SDLog> nlog = new ArrayList<SDLog>();
		try {
			csv.readRow();//load hdr;

			for (num=0; csv.isAvailable(); num++) {
				SDLog  param = parseLine(csv);
				if (param!=null) {
					nlog.add(param);
				}
			}
			max_label.setText("/"+(nlog.size()-1));
		} catch(IOException ex) {
			ex.printStackTrace();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("read " + num + " lines");

		return nlog; 
	}

	void syncRow() {
		if (curr_row < 0 || curr_row >= log.size()) {
			curr_row = 0;
		}

		ctrl.setSDLogData(log.get(curr_row));
		fld_curr.setText(String.valueOf(curr_row));

		SDLog  p = log.get(curr_row);
		imageview.setImage(new File(basedir,p.getFilename()));
	}

	public void setCurrRow(int row) {
		curr_row = row;
		syncRow();
	}

	public void nextRow() {
		if (curr_row >= log.size()-1) {
			curr_row = 0;
		} else {
			curr_row++;
		}

		syncRow();
	}

	public void previousRow() {
		if (curr_row==0) {
			curr_row = log.size()-1;
		} else {
			curr_row--;
		}

		syncRow();
	}

	public void setBaseDir(File file) {
		this.basedir = file;
	}

	public void init() {
		JFrame frm = new JFrame("SD LogViewer");
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ctrl = new SDControlPanel();

		JPanel  pane = new JPanel();
		pane.setLayout(new BorderLayout());

		pane.add(ctrl.createPane(), BorderLayout.CENTER);

		imageview = new SDImageView();
		imageview.setSize(new Dimension(512,512));
		//pane.add(imageview, BorderLayout.EAST);

		imageview.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_LEFT:
						previousRow();
						break;
					case KeyEvent.VK_RIGHT:
						nextRow();
						break;
				}
			}
		});

		imageview.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				imageview.requestFocus();
			}
		});

		JPanel  ctrlpane = new JPanel();
		ctrlpane.add(new JLabel("curr:"));
		fld_curr = new JTextField(4);
		ctrlpane.add(fld_curr);
		max_label = new JLabel("/0");
		ctrlpane.add(max_label);

		JButton  set_btn = new JButton("Set");
		set_btn.addActionListener(e -> { 
			String row = fld_curr.getText(); 
			int    rint = Integer.parseInt(row);
			setCurrRow(rint);
		});
		ctrlpane.add(set_btn);

		JButton  prev_btn = new JButton("Previous");
		prev_btn.addActionListener(e -> { previousRow(); });
		ctrlpane.add(prev_btn);

		JButton  next_btn = new JButton("Next");
		next_btn.addActionListener(e -> { nextRow(); });
		ctrlpane.add(next_btn);

		//ctrlpane.add(Box.createRigidArea(new Dimension(0,5)));

		JButton  reload_btn = new JButton("Reload");
		reload_btn.addActionListener(e -> { reloadLog(); });
		ctrlpane.add(reload_btn);

		pane.add(ctrlpane,BorderLayout.NORTH);

		JSplitPane sppane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
										pane,imageview);

		frm.setContentPane(sppane);

		frm.pack();
		frm.setVisible(true);
	}

	public void setLog(String fname) {
		this.logfile = new File(fname);
		setBaseDir(logfile.getParentFile());

		curr_row = 0;
		reloadLog();
	}

	/**
	 * ログデータを読み込む
	 */
	public void reloadLog() {
		try {
			Reader  rd = new FileReader(logfile);
			log = readLog(rd);

			syncRow();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SDLogView  logview = new SDLogView();

		logview.init();
		System.out.println("log="+args[0]);
		logview.setLog(args[0]);
	}
}
