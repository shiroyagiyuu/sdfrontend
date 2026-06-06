package pureplus;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class ImageScaler {
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

		int srcw = src.getWidth();
		int srch = src.getHeight();
		int[] srcPixels = src.getRGB(0, 0, srcw, srch, null, 0, srcw);

        double xRatio = (double) srcw / newWidth;
        double yRatio = (double) srch / newHeight;

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {

                // 元画像での対応領域
                int xStart = (int) Math.floor(x * xRatio);
                int yStart = (int) Math.floor(y * yRatio);
                int xEnd = (int) Math.min(Math.ceil((x + 1) * xRatio), srcw);
                int yEnd = (int) Math.min(Math.ceil((y + 1) * yRatio), srch);

                long sumR = 0, sumG = 0, sumB = 0, sumA = 0;
                int count = 0;

                for (int yy = yStart; yy < yEnd; yy++) {
                    int rowStart = yy * srcw;
                    for (int xx = xStart; xx < xEnd; xx++) {
                        int rgb = srcPixels[rowStart + xx];
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

    /**
     * 画像を指定されたサイズに収まるように拡大・縮小する
     * @param img 元画像
     * @param width 目標幅
     * @param height 目標高さ
     * @return 拡大・縮小後の画像
     */
	public static BufferedImage scaleImage(BufferedImage  img, int width, int height) {
		BufferedImage   dstimg;
		double  scale_w, scale_h, scale;

		scale_w = (double)width  / (double)img.getWidth();
		scale_h = (double)height / (double)img.getHeight();
		scale = Math.min(scale_w, scale_h);

		System.out.println("img= "  +img.getWidth()+","+img.getHeight());
		System.out.println("scale= "+scale_w+","+scale_h);

		int		imgw = (int)(img.getWidth()  * scale);
		int     imgh = (int)(img.getHeight() * scale);

		System.out.println("dstimg= "  +imgw+","+imgh);

        if (scale < 1.0) {
            dstimg = resizeBoxFilter(img, imgw, imgh);
        } else {
            dstimg = new BufferedImage(imgw, imgh, BufferedImage.TYPE_INT_RGB);
			Graphics2D  g2d = dstimg.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.drawImage(img, 0, 0, imgw, imgh, null);
			g2d.dispose();
        }

		return  dstimg;
	}

}
