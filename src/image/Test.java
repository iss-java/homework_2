package image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Wen Ke on 2016/12/28.
 */
public class Test {
    public static void main(String[] args) {
        cut("data/huaji.jpg", "data/huaji_cut.jpg");
        zoom("data/huaji.jpg", "data/huaji_enlarge.jpg", 1.732);
        zoom("data/huaji.jpg", "data/huaji_shrink.jpg", 0.4);
    }
    
    public static void cut(String inpath, String outpath) {
        try {
            BufferedImage image = ImageIO.read(new File(inpath));
            int width = image.getWidth();
            int height = image.getHeight();
            BufferedImage new_image = new BufferedImage(width, height/2, BufferedImage.TYPE_INT_RGB);
            
            for (int i=0; i < width; ++i) {
                for (int j=0; j < height/2; ++j) {
                   new_image.setRGB(i, j, image.getRGB(i, j));
                }
            }
            
            ImageIO.write(new_image, "jpg", new File(outpath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void zoom(String inpath, String outpath, double ratio) {
        try {
            BufferedImage image = ImageIO.read(new File(inpath));
            int width = image.getWidth();
            int height = image.getHeight();
            int[] imageArray = new int[width * height];
            image.getRGB(0, 0, width, height, imageArray, 0, width);
            
            int new_width = (int) (width * ratio);
            int new_height = (int) (height * ratio);
            
            BufferedImage imageNew = new BufferedImage(new_width, new_height, BufferedImage.TYPE_INT_RGB);
            //imageNew.setRGB(0,0,width/2,height,imageArray,width,width);
            
            for (int i = 0; i < new_width; i++) {
                for (int k = 0; k < new_height; k++) {
                    int xvalue_1 = (int) (i / ratio);
                    int xvalue_2 = xvalue_1 + 1;
                    int yvalue_1 = (int) (k / ratio);
                    int yvalue_2 = yvalue_1 + 1;
                    double xratio = i / ratio - xvalue_1;
                    double yratio = k / ratio - yvalue_1;
                    if (yvalue_2 >= height || xvalue_2 >= width)
                        continue;
                    int[] rgb_1 = getARGB(image.getRGB(xvalue_1, yvalue_1));
                    int[] rgb_2 = getARGB(image.getRGB(xvalue_1, yvalue_2));
                    int[] rgb_3 = getARGB(image.getRGB(xvalue_2, yvalue_1));
                    int[] rgb_4 = getARGB(image.getRGB(xvalue_2, yvalue_2));
                    int[] xrgb_1 = new int[3];
                    int[] xrgb_2 = new int[3];
                    int[] rgb = new int[3];
                    
                    // 计算上方投影点的颜色值
                    for (int j = 0; j < 3; j++) {
                        xrgb_1[j] = (int) (rgb_1[j] * (1 - xratio) + rgb_3[j] * xratio);
                    }
                    // 计算下方投影点的颜色值
                    for (int j = 0; j < 3; j++) {
                        xrgb_2[j] = (int) (rgb_2[j] * (1 - xratio) + rgb_4[j] * xratio);
                    }
                    // 将上下两个投影点连起来，计算中间点的值
                    for (int j = 0; j < 3; j++) {
                        rgb[j] = (int) (xrgb_1[j] * (1 - yratio) + xrgb_2[j] * yratio);
                    }
                    imageNew.setRGB(i, k, RGB(rgb));
                }
            }
            File outFile = new File(outpath);
            ImageIO.write(imageNew, "jpg", outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static int[] getARGB(int pixel) {
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        int rgb[] = new int[3];
        rgb[0] = red;
        rgb[1] = green;
        rgb[2] = blue;
        return rgb;
    }
    
    public static int RGB(int[] data) {
        return (255 << 24)
                | ((data[0] & 0xFF) << 16)
                | ((data[1] & 0xFF) << 8)
                | (data[2] & 0xFF);
    }
    
}
