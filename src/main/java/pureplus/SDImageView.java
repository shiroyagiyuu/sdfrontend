package pureplus;

import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

class SDImageView extends JComponent
{
	BufferedImage   image;
	BufferedImage   cachedImage;
	Rectangle       cachedBounds;

	/**
     * BOXフィルタで画像を縮小
     * @param src 元画像
     * @param newWidth 縮小後の幅
     * @param newHeight 縮小後の高さ
     * @return 縮小後の画像
     */
    public static BufferedImage resizeBoxFilter(BufferedImage src, int newWidth, int newHeight) {
        if (src == null || newWidth <= 0 || newHeight <= 0) {
            throw new IllegalArgumentException("Invalid image or dimensions.");
        }

        BufferedImage dst = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        double xRatio = (double) src.getWidth() / newWidth;
        double yRatio = (double) src.getHeight() / newHeight;

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {

                // 元画像での対応領域
                int xStart = (int) Math.floor(x * xRatio);
                int yStart = (int) Math.floor(y * yRatio);
                int xEnd = (int) Math.min(Math.ceil((x + 1) * xRatio), src.getWidth());
                int yEnd = (int) Math.min(Math.ceil((y + 1) * yRatio), src.getHeight());

                long sumR = 0, sumG = 0, sumB = 0, sumA = 0;
                int count = 0;

                for (int yy = yStart; yy < yEnd; yy++) {
                    for (int xx = xStart; xx < xEnd; xx++) {
                        int rgb = src.getRGB(xx, yy);
                        int a = (rgb >> 24) & 0xFF;
                        int r = (rgb >> 16) & 0xFF;
                        int g = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;

                        sumA += a;
                        sumR += r;
                        sumG += g;
                        sumB += b;
                        count++;
                    }
                }

                // 平均色を計算
                int avgA = (int) (sumA / count);
                int avgR = (int) (sumR / count);
                int avgG = (int) (sumG / count);
                int avgB = (int) (sumB / count);

                int avgRGB = (avgA << 24) | (avgR << 16) | (avgG << 8) | avgB;
                dst.setRGB(x, y, avgRGB);
            }
        }

        return dst;
	}

	private BufferedImage createScaledImage(BufferedImage img, int dstw, int dsth) {
		BufferedImage resized;
		int  img_w = img.getWidth();
		int  img_h = img.getHeight();

		double sc_w = (double)dstw / img_w;
		double sc_h = (double)dsth / img_h;

		double scale = (sc_w < sc_h)?sc_w:sc_h;

		int   draw_w = (int)(img_w * scale);
		int   draw_h = (int)(img_h * scale);

		if (scale < 1.0) {
			resized = resizeBoxFilter(img, draw_w, draw_h);
		} else {
			resized = new BufferedImage(draw_w, draw_h, BufferedImage.TYPE_INT_RGB);
			Graphics2D  g2d = resized.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.drawImage(img, 0, 0, draw_w, draw_h, this);
			g2d.dispose();
		}
		return resized;
	}

	public void paint(Graphics g) {		
		Rectangle  bounds = this.getBounds();

		g.setColor(Color.lightGray);
		g.fillRect(0, 0, bounds.width, bounds.height);

		if (image!=null) {
			if (cachedImage != null && cachedBounds != null) {
				if (bounds.width != cachedBounds.width || bounds.height != cachedBounds.height) {
					cachedImage = createScaledImage(image, bounds.width, bounds.height);
					cachedBounds = bounds;
				}
			} else {
				cachedImage = createScaledImage(image, bounds.width, bounds.height);
				cachedBounds = bounds;
			}

			int  draw_x = (bounds.width - cachedImage.getWidth()) / 2;
			int  draw_y = (bounds.height - cachedImage.getHeight()) / 2;

			g.drawImage(cachedImage, draw_x, draw_y, this);
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

