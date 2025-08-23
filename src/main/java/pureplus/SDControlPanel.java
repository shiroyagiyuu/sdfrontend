package pureplus;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class SDControlPanel
{
	JTextArea  prompt;
	JTextArea  negative_prompt;

	JTextField  seed;
	JTextField  width;
	JTextField  height;

	JTextField  sampler;
	JTextField  cfgs;
	JTextField  steps;
	JTextField  sd_model_name;
	JTextField  sd_model_hash;

	JPopupMenu  prompt_popup;

	JTextField  filename;

	class PopupListener extends MouseAdapter
	{
			@Override
			public void mousePressed(MouseEvent e) {
				checkPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				checkPopup(e);
			}

			void checkPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					prompt_popup.show(e.getComponent(),e.getX(),e.getY());
				}
			}
		}

	public JComponent createPane() {
		JPanel  toppane = new JPanel();

		toppane.setLayout(new BorderLayout());

		/* Prompt Popup */
		prompt_popup = new JPopupMenu();
		JMenuItem  cut_menu = new JMenuItem("cut");
		cut_menu.addActionListener(e->{
			Object  inv = prompt_popup.getInvoker();
			if (inv instanceof JTextArea) {
				JTextArea  ta = (JTextArea)inv;
				ta.cut();
			}
		});
		prompt_popup.add(cut_menu);
		JMenuItem  copy_menu = new JMenuItem("copy");
		copy_menu.addActionListener(e->{
			Object  inv = prompt_popup.getInvoker();
			if (inv instanceof JTextArea) {
				JTextArea  ta = (JTextArea)inv;
				ta.copy();
			}
		});
		prompt_popup.add(copy_menu);
		JMenuItem  paste_menu = new JMenuItem("paste");
		paste_menu.addActionListener(e->{
			Object  inv = prompt_popup.getInvoker();
			if (inv instanceof JTextArea) {
				JTextArea  ta = (JTextArea)inv;
				ta.paste();
			}
		});
		prompt_popup.add(paste_menu);

		PopupListener  popupListener = new PopupListener();

		/* Prompt Pane */
		JPanel	prompt_pane = new JPanel();
		prompt_pane.setLayout(new BoxLayout(prompt_pane, BoxLayout.Y_AXIS));

		JLabel label = new JLabel("Prompt");
		prompt = new JTextArea(5,30);
		prompt.setLineWrap(true);
		prompt.setWrapStyleWord(true);
		label.setLabelFor(prompt);
		prompt.addMouseListener(popupListener);

		prompt_pane.add(label);

		JScrollPane scprompt = new JScrollPane(prompt, 
						ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		prompt_pane.add(scprompt);

		label = new JLabel("Negative Prompt");
		//label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		prompt_pane.add(label);
		negative_prompt = new JTextArea(5,30);
		negative_prompt.setLineWrap(true);
		negative_prompt.setWrapStyleWord(true);
		negative_prompt.addMouseListener(popupListener);

		JScrollPane scnegprompt = new JScrollPane(negative_prompt, 
						ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		prompt_pane.add(scnegprompt);

		toppane.add(prompt_pane, BorderLayout.CENTER);

		/* Control Pane */
		JPanel	ctrl_pane = new JPanel();
		ctrl_pane.setLayout(new BoxLayout(ctrl_pane, BoxLayout.Y_AXIS));

		JPanel  ctrl_a = new JPanel();
		
		ctrl_a.add(new JLabel("Seed:"));
		seed = new JTextField(10);
		ctrl_a.add(seed);

		ctrl_a.add(new JLabel("Width:"));
		width = new JTextField(5);
		ctrl_a.add(width);

		ctrl_a.add(new JLabel("Height:"));
		height = new JTextField(5);
		ctrl_a.add(height);

		ctrl_pane.add(ctrl_a);

		JPanel ctrl_b = new JPanel();

		ctrl_b.add(new JLabel("Sampler:"));
		sampler = new JTextField(10);
		ctrl_b.add(sampler);

		ctrl_b.add(new JLabel("Cfgs:"));
		cfgs = new JTextField(3);
		ctrl_b.add(cfgs);

		ctrl_b.add(new JLabel("Steps:"));
		steps = new JTextField(3);
		ctrl_b.add(steps);

		ctrl_pane.add(ctrl_b);

		JPanel  model_pane = new JPanel();

		model_pane.add(new JLabel("Model:"));
		sd_model_name = new JTextField(20);
		model_pane.add(sd_model_name);

		model_pane.add(new JLabel("Hash:"));
		sd_model_hash = new JTextField(20);
		model_pane.add(sd_model_hash);

		ctrl_pane.add(model_pane);

		JPanel  img_pane = new JPanel();

		img_pane.add(new JLabel("Filename:"));
		filename = new JTextField(32);
		img_pane.add(filename);

		ctrl_pane.add(img_pane);

		toppane.add(ctrl_pane, BorderLayout.SOUTH);

		return toppane;
	}

	public void setSDLogData(SDLog param) {
		prompt.setText(param.getPrompt());
		negative_prompt.setText(param.getNegativePrompt());
		seed.setText(String.valueOf(param.getSeed()));
		width.setText(String.valueOf(param.getWidth()));
		height.setText(String.valueOf(param.getHeight()));
		sampler.setText(param.getSampler());
		cfgs.setText(Integer.toString(param.getCfgs()));
		steps.setText(Integer.toString(param.getSteps()));

		sd_model_name.setText(param.getSDModelName());
		sd_model_hash.setText(param.getSDModelHash());


		filename.setText(param.getFilename());
	}

	public static void main(String[] args) {
		SDControlPanel   ctrl = new SDControlPanel();

		JFrame  frm = new JFrame("Automatic1111");
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setContentPane(ctrl.createPane());
		frm.pack();

		frm.setVisible(true);
	}
}
