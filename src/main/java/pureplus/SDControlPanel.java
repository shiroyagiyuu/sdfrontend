package pureplus;

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

	JTextField  filename;

	public JComponent createPane() {
		JPanel  pane = new JPanel();

		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));


		JLabel label = new JLabel("Prompt");
		prompt = new JTextArea(5,30);
		prompt.setLineWrap(true);
		prompt.setWrapStyleWord(true);
		label.setLabelFor(prompt);

		pane.add(label);
		pane.add(prompt);

		label = new JLabel("Negative Prompt");
		//label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		pane.add(label);
		negative_prompt = new JTextArea(5,30);
		negative_prompt.setLineWrap(true);
		negative_prompt.setWrapStyleWord(true);
		pane.add(negative_prompt);

		JPanel  ctrl_a = new JPanel();
		
		ctrl_a.add(new JLabel("Seed:"));
		seed = new JTextField(20);
		ctrl_a.add(seed);

		ctrl_a.add(new JLabel("Width:"));
		width = new JTextField(5);
		ctrl_a.add(width);

		ctrl_a.add(new JLabel("Height:"));
		height = new JTextField(5);
		ctrl_a.add(height);

		pane.add(ctrl_a);

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

		pane.add(ctrl_b);

		JPanel  model_pane = new JPanel();

		model_pane.add(new JLabel("Model:"));
		sd_model_name = new JTextField(20);
		model_pane.add(sd_model_name);

		model_pane.add(new JLabel("Hash:"));
		sd_model_hash = new JTextField(20);
		model_pane.add(sd_model_hash);

		pane.add(model_pane);

		JPanel  img_pane = new JPanel();

		img_pane.add(new JLabel("Filename:"));
		filename = new JTextField(32);
		img_pane.add(filename);

		pane.add(img_pane);

		return pane;
	}

	public void setSDLogData(SDLog param) {
		prompt.setText(param.getPrompt());
		negative_prompt.setText(param.getNegativePrompt());
		seed.setText(String.valueOf(param.getSeed()));
		width.setText(String.valueOf(param.getWidth()));
		height.setText(String.valueOf(param.getWidth()));
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
