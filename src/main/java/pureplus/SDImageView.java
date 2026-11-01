package pureplus;

import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import java.awt.RenderingHints;

class SDImageView extends JComponent
{
	BufferedImage   image;
	BufferedImage   cachedImage;
	Rectangle       cachedBounds;

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;

        // 高品質補間を指定
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

		Rectangle  bounds = this.getBounds();

		g2d.setColor(Color.lightGray);
		g2d.fillRect(0, 0, bounds.width, bounds.height);

		if (image!=null) {
			if (cachedImage != null && cachedBounds != null) {
				if (bounds.width != cachedBounds.width || bounds.height != cachedBounds.height) {
					cachedImage = ImageScaler.scaleImage(image, bounds.width, bounds.height);
					cachedBounds = bounds;
				}
			} else {
				cachedImage = ImageScaler.scaleImage(image, bounds.width, bounds.height);
				cachedBounds = bounds;
			}

			int  draw_x = (bounds.width - cachedImage.getWidth()) / 2;
			int  draw_y = (bounds.height - cachedImage.getHeight()) / 2;

			g2d.drawImage(cachedImage, draw_x, draw_y, this);
		}
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void setImage(BufferedImage img) {
		this.image = img;
		this.cachedImage = null;
		this.cachedBounds = null;
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

	public void setImage(java.io.File file) {
		try {
			if (file.exists()) {
				BufferedImage  img = javax.imageio.ImageIO.read(file);
				setImage(img);
			} else {
				System.out.println("File not found: "+file.getName());
			}
		} catch (java.io.IOException ex) {
			ex.printStackTrace();
		}

	}

	public void setImage(String path) {
		setImage(new java.io.File(path));
	}
}

