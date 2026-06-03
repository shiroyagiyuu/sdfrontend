package pureplus;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;

class SDImageView extends JComponent
{
	Image   image;
	File	file;

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

	public void paint(Graphics g) {
		Graphics2D  g2d;

		g2d = (Graphics2D)g;

		Rectangle  bounds = this.getBounds();

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
	
				if (scale < 1.0) {
					BufferedImage  resized = resizeBoxFilter((BufferedImage)image, draw_w, draw_h);
					//Image resized = image.getScaledInstance(draw_w, draw_h, Image.SCALE_AREA_AVERAGING);
					g2d.drawImage(resized, draw_x, draw_y, draw_w, draw_h, this);
				} else {
					g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
										RenderingHints.VALUE_RENDER_QUALITY);
					// BICUBIC 補間を指定
        			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            			RenderingHints.VALUE_INTERPOLATION_BICUBIC);
					g2d.drawImage(image, draw_x, draw_y, draw_w, draw_h, this);
				}
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

