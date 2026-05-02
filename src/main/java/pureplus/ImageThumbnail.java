package pureplus;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ImageThumbnail
{
    final static int THUMBNAIL_WIDTH = 300;
    final static int THUMBNAIL_HEIGHT = 200;

	public static BufferedImage createThumbnail(BufferedImage  img) {
		Dimension       base = new Dimension(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
		BufferedImage   dstimg;
		double  scale_w, scale_h;

		
		scale_w = (double)base.width  / (double)img.getWidth();
		scale_h = (double)base.height / (double)img.getHeight();

		System.out.println("img= "  +img.getWidth()+","+img.getHeight());
		System.out.println("scale= "+scale_w+","+scale_h);

		if (scale_w < scale_h) {
			scale_h = scale_w;
		} else {
			scale_w = scale_h;
		}
		int		imgw = (int)(img.getWidth()  * scale_w);
		int     imgh = (int)(img.getHeight() * scale_h);

		System.out.println("dstimg= "  +imgw+","+imgh);

		dstimg = new BufferedImage(imgw, imgh, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = dstimg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		//g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.drawImage(img, 0, 0, imgw, imgh, null);
		g.dispose();

		return  dstimg;
	}

	public static void createThumbnail(File src, File dst) throws IOException {
        String  srcfname = src.getName();
        String  ext = srcfname.substring(srcfname.lastIndexOf(".")+1).toLowerCase();
        createThumbnail(src, dst, ext);
    }

    public static void createThumbnail(File src, File dst, String ext) throws IOException {
		BufferedImage   srcimg = ImageIO.read(src);
		BufferedImage   dstimg = createThumbnail(srcimg);

		boolean  ret;
		ret = ImageIO.write(dstimg, ext, dst);
		if (!ret) {
			System.out.println("write error!!");
		}
	}

	public static void testencode(String testfile) {
		File   srcf = new File(testfile);
		File   dstf = new File("output.jpg");
		System.out.println("srcfile="+testfile);
		System.out.println("dstfile="+dstf.getAbsolutePath());
		try {
			ImageThumbnail.createThumbnail(srcf, dstf, "jpg");
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void createThumbnailDir(File srcfile, File destdir, int depth) {
		if (srcfile.isDirectory()) {
			if (depth>0) {
				File[]   lists = srcfile.listFiles();
				Arrays.sort(lists);
				for (File file : lists) {
					createThumbnailDir(file, destdir, depth - 1);
				}
			}
		} else {
			File destfile = new File(destdir, srcfile.getName());
			try {
                if (destfile.exists()) {
                    //System.out.println("skip "+destfile.getAbsolutePath() + " already exists");
                } else if (srcfile.getName().toLowerCase().endsWith(".jpg") ||
						   srcfile.getName().toLowerCase().endsWith(".jpeg") ||
						   srcfile.getName().toLowerCase().endsWith(".png") ||
						   srcfile.getName().toLowerCase().endsWith(".bmp") ||
						   srcfile.getName().toLowerCase().endsWith(".gif")) {
                    System.out.println("create "+destfile.getAbsolutePath());
				    createThumbnail(srcfile, destfile);
                } else {
					System.out.println("skip "+srcfile.getAbsolutePath() + " (unsupported format)");
				}
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		File srcfile = new File(args[0]);
		File destdir;

		if (srcfile.isDirectory()) {
			destdir = new File(srcfile, "thumbnail");
		} else {
			destdir = new File(srcfile.getParentFile(), "thumbnail");
		}
		if (!destdir.exists()) {
			destdir.mkdir();
		}
		
		System.out.println("Creating thumbnails at " + destdir.getAbsolutePath());
		createThumbnailDir(srcfile, destdir, 1);
	}
}
