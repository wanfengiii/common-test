package com.common.util;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.filters.Canvas;
import net.coobird.thumbnailator.geometry.Coordinate;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.*;
import java.util.Objects;

public class ImageUtils {
    private static Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    public static String[] IMAGE_TYPES = new String[]{"jpg", "jpeg", "bmp", "png", "gif", "psd"};

    /**
     * <p>1.根据文件流{@source}，组装图片ImageVO对象，并计算原图和设定的宽高大小之间的比例，以此判断是否需要压缩</p>
     * <p>2.如果不需要压缩，直接将原图的文件字节数组{@bytes}转换为文件流即可</p>
     * <p>3.如果需要压缩，则读取原图的文件字节数组{@bytes}，并根据计算出的比例，再输出压缩图的文件流</p>
     *
     * @param source 文件流
     * @param bytes  文件字节数组
     * @param width  设定的图片宽度
     * @param height 设定的图片高度
     */
    public static InputStream getThumbnailInputStream(InputStream source, byte[] bytes, int width, int height) throws IOException {
        //获取文件对象，并计算原图和设定的宽高大小之间的比例，以此判断是否需要压缩
        ImageVO imageVO = getImageWidthAndHeight(source);
        float ratio = Math.min(((float) width) / imageVO.getWidth(), ((float) height) / imageVO.getHeight());

        if (ratio >= 1) {
            //如果图片本身就小于设定的宽高，则直接将原图返回即可
            return new ByteArrayInputStream(bytes);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Thumbnails.of(new ByteArrayInputStream(bytes)).scale(ratio).toOutputStream(bos);
        return new ByteArrayInputStream(bos.toByteArray());
    }

    public static boolean hasAlpha(Image image) {
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;
            return bimage.getColorModel().hasAlpha();
        }
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }
        ColorModel cm = pg.getColorModel();
        if (Objects.isNull(cm)) {
            return false;
        }
        return cm.hasAlpha();
    }

    /**
     * 校验图片合法图片类型
     *
     * @param fileName
     * @return
     */
    public final static boolean validImgType(String fileName) {
        String ext = FileUtils.extractExt(fileName);
        for (int i = 0; i < IMAGE_TYPES.length; i++) {
            if (IMAGE_TYPES[i].equalsIgnoreCase(ext))
                return true;
        }
        return false;
    }

    private static ImageVO getImageWidthAndHeight(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        try {
            return getImageWidthAndHeight(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private static ImageVO getImageWidthAndHeight(InputStream is) throws IOException {
        BufferedImage image = null;
        try {
            image = ImageIO.read(is);
        } catch (javax.imageio.IIOException e) {
        }

        if (image == null) {
            throw new IOException("Error occurs when read image(is null)");
        }

        ImageVO imageVO = new ImageVO();
        imageVO.setWidth(image.getWidth());
        imageVO.setHeight(image.getHeight());

        return imageVO;
    }

    private static class ImageVO {

        private int width;
        private int height;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public static void crop(String sourcePathname, String targetPathname, int x, int y, int w, int h) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(sourcePathname)));
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(targetPathname)));
        String ext = FileUtils.extractExt(targetPathname);
        crop(in, out, ext, x, y, w, h);
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(out);
    }

    public static void crop(InputStream in, OutputStream out, String ext, int x, int y, int w, int h) throws IOException {
        BufferedImage image = ImageIO.read(in);
        Thumbnails
                .of(image)
                .size(image.getWidth(), image.getHeight())
                .addFilter(new Canvas(w, h, new Coordinate(x, y), true, Color.WHITE))
                .outputFormat(ext)
                .toOutputStream(out);
    }

    public static void main(String[] args) throws Exception {
        File file = new File("C:\\home\\trace-api\\temp\\2021-03-15\\e-enterprise\\1615794966193.jpg");
        ImageVO imageVO = getImageWidthAndHeight(file);
        float ratio = Math.min(((float) 300) / imageVO.getWidth(), ((float) 300) / imageVO.getHeight());
        Image image = Toolkit.getDefaultToolkit().getImage(file.getCanonicalPath());
        image = new ImageIcon(image).getImage();
        BufferedImage bi_scale = null;
        bi_scale = new BufferedImage(image.getWidth(null), image.getHeight(null), hasAlpha(image) ? BufferedImage.TYPE_INT_ARGB
                : BufferedImage.TYPE_INT_RGB);
        Graphics g = bi_scale.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
    }

}