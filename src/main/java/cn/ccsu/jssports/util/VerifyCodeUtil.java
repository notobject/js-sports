package cn.ccsu.jssports.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class VerifyCodeUtil {

    private static final String IMG_URL = "http://jwcxxcx.ccsu.cn/jwxt/verifycode.servlet";

    private static int[][] stdImg = new int[][]{
            {0x0c0, 0x0e0, 0x0f0, 0x0d8, 0x0c8, 0x0c0, 0x0c0, 0x0c0, 0x0c0, 0x0c0, 0x0c0, 0x0c0},
            {0x0f0, 0x1f8, 0x31c, 0x30c, 0x300, 0x180, 0x1c0, 0x0e0, 0x070, 0x018, 0x3fc, 0x3fc},
            {0x1f0, 0x3f8, 0x30c, 0x300, 0x1e0, 0x1e0, 0x380, 0x300, 0x30c, 0x39c, 0x1f8, 0x0f0},
            {0x018, 0x018, 0x018, 0x1d8, 0x3f8, 0x738, 0x618, 0x618, 0x618, 0x738, 0x3f8, 0x1d8},
            {0x000, 0x000, 0x000, 0x1e0, 0x3f0, 0x338, 0x018, 0x018, 0x018, 0x338, 0x1f0, 0x1e0},
            {0x000, 0x000, 0x000, 0x9d8, 0xff8, 0x738, 0x318, 0x318, 0x318, 0x318, 0x318, 0x318},
            {0x000, 0x000, 0x000, 0x3d8, 0x7f8, 0x638, 0x618, 0x618, 0x618, 0x618, 0x618, 0x618},
            {0x000, 0x000, 0x000, 0x318, 0x318, 0x318, 0x1b0, 0x1b0, 0x1b0, 0x0e0, 0x0e0, 0x0e0},
            {0x000, 0x000, 0x000, 0x318, 0x3b8, 0x1b0, 0x0e0, 0x0e0, 0x0e0, 0x1b0, 0x3b8, 0x318},
            {0x000, 0x000, 0x000, 0x3f8, 0x3f8, 0x180, 0x1c0, 0x0e0, 0x070, 0x030, 0x3f8, 0x3f8}};

    private static char[] charList = {'1', '2', '3', 'b', 'c', 'm', 'n', 'v', 'x', 'z'};

    private static final int CHAR_WIDTH = 10;

    private static final int CHAR_HEIGHT = 12;

    private static int[][] subImgPos = new int[][]{{3, 4}, {13, 4}, {23, 4}, {33, 4}};

    public static String ocr(BufferedImage img) {
        String result = "";
        BufferedImage[] subImgs = getSubImage(img);
        for (BufferedImage subImg : subImgs) {
            result += recognize(subImg);
        }
        return result;
    }

    protected static char recognize(BufferedImage charImg) {
        int[] weight = new int[charList.length];
        int maxIndex = 0;
        float maxValue = 0;
        int offset = (4 - charImg.getWidth() % 4) % 4;
        for (int k = 0; k < charList.length; k++) {
            for (int y = 0; y < charImg.getHeight(); y++) {
                for (int x = 0; x < charImg.getWidth(); x++) {
                    boolean stdImgIsBlack = ((stdImg[k][y] >> (x + offset)) & 1) == 1;
                    if (isBlack(charImg.getRGB(x, y)) == stdImgIsBlack) {
                        weight[k]++;
                    }
                }
            }
            float w = (float) weight[k]
                    / (charImg.getWidth() * charImg.getHeight());
            if (maxValue < w) {
                maxValue = w;
                maxIndex = k;
            }
        }
        return charList[maxIndex];
    }

    protected static boolean isBlack(int colorInt) {
        Color color = new Color(colorInt);
        int threshold = color.getRed() + color.getGreen() + color.getBlue();
        return threshold < 127 * 3;
    }

    protected static BufferedImage[] getSubImage(BufferedImage img) {
        BufferedImage[] subImgs = new BufferedImage[subImgPos.length];
        for (int i = 0; i < subImgPos.length; i++) {
            subImgs[i] = img.getSubimage(subImgPos[i][0], subImgPos[i][1],
                    CHAR_WIDTH, CHAR_HEIGHT);
        }
        return subImgs;
    }

    protected static void splitImg(BufferedImage img) {
        clearNoise(img);
        BufferedImage[] subImgs = getSubImage(img);
        for (BufferedImage subImg : subImgs) {
            try {
                ImageIO.write(subImg, "png",
                        new File(System.currentTimeMillis() + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected static void clearNoise(BufferedImage img) {
        int[][] nearPoints = new int[][]{{0, 1}, {1, 1}, {1, 0},
                {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};
        for (int y = 1; y < img.getHeight() - 1; y++) {
            for (int x = 1; x < img.getWidth() - 1; x++) {
                int nearPointCount = 0;
                boolean isBlack = isBlack(img.getRGB(x, y));
                if (isBlack) {
                    for (int[] nearPoint : nearPoints) {
                        if (isBlack(img.getRGB(x + nearPoint[0], y
                                + nearPoint[1]))) {
                            nearPointCount++;
                        }
                    }
                }
                img.setRGB(x, y, isBlack && nearPointCount > 1 ? 0x000000
                        : 0xffffff);
            }
        }
    }

    protected static void encodeImg() {
        System.out.println("{");
        for (char c : charList) {
            try {
                BufferedImage img = ImageIO.read(new File(c + ".png"));
                int[] hexCode = new int[img.getHeight()];
                int width = img.getWidth() / 4
                        + (img.getWidth() % 4 == 0 ? 0 : 1);
                for (int y = 0; y < img.getHeight(); y++) {
                    int value = 0;
                    for (int x = img.getWidth() - 1; x >= 0; x--) {
                        boolean isBlack = isBlack(img.getRGB(x, y));
                        value = value << 1;
                        value = value + (isBlack ? 1 : 0);
                    }
                    hexCode[y] = value << (4 - img.getWidth() % 4) % 4;
                }
                printHexCode(hexCode, width);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("}");
    }

    protected static void printHexCode(int[] hexs, int width) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (int hex : hexs) {
            sb.append(String.format("%#0" + (width + 2) + "x", hex) + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        System.out.print(sb);
        System.out.println(",");
    }

    public static String ocr(byte[] imgBytes) throws IOException {
        return ocr(ImageIO.read(new ByteArrayInputStream(imgBytes)));
    }

    protected static String getCode() throws IOException {
        return ocr(getCodeImg());
    }

    protected static BufferedImage getCodeImg() throws IOException {
        URL url = new URL(IMG_URL);
        return ImageIO.read(url);
    }

    public static void test() {
        try {
            for (int i = 0; i < 10; i++) {
                BufferedImage img = getCodeImg();
                String result = ocr(img);
                ImageIO.write(img, "png", new File(result + ".png"));
                System.out.println(result);
                // splitImg(img);
                // encodeImg();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        test();
        // encodeImg();
    }
}
