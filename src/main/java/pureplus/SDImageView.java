package pureplus;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;

class SDImageView extends JComponent
{
	Image   image;
	File	file;

	public void paint(Graphics g) {
		Graphics2D  g2d;

		g2d = (Graphics2D)g;

		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		Rectangle  bounds = g2d.getClipBounds();

		g2d.setColor(Color.lightGray);
		g2d.fillRect(0, 0, bounds.width, bounds.height);

		if (image!=null) {

			int  img_w = image.getWidth(this);
			int  img_h = image.getHeight(this);

			if (img_w != 0 && img_h != 0) {
				double sc_w = (double)bounds.width / img_w;
				double sc_h = (double)bounds.height / img_h;

				double scale = (sc_w < sc_h)?sc_w:sc_h;

				int   draw_w = (int)(img_w * scale);
				int   draw_h = (int)(img_h * scale);
				int   draw_x = (bounds.width - draw_w)/2;
				int   draw_y = (bounds.height - draw_h)/2;
	
				g2d.drawImage(image, draw_x, draw_y, draw_w, draw_h, this);
			}
		}
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void setImage(Image img) {
		this.image = img;
		repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(512,512);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(128,128);
	}

	public void setImage(File file) {
		try {
			if (file.exists()) {
				Image  img = ImageIO.read(file);
				this.file = file;

				setImage(img);
			} else {
				System.out.println("File not found: "+file.getName());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public void setImage(String path) {
		setImage(new File(path));
	}
}

