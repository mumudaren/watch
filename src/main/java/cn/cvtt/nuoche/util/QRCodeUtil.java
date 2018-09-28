package cn.cvtt.nuoche.util;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.lang.StringUtils;
import sun.font.FontDesignMetrics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

public class QRCodeUtil {

    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 300;
    // LOGO宽度
    private static final int WIDTH = 60;
    // LOGO高度
    private static final int HEIGHT = 60;

    /**
     * 创建二维码图片
     * @param content 内容
     * @param imgPath logo地址
     * @param needCompress 是否压缩
     * @return
     * @throws Exception
     */
    private static BufferedImage createImage(String content, String imgPath,
                                             boolean needCompress, String pressText) throws Exception {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
                        : 0xFFFFFFFF);
            }
        }
        if (imgPath == null || "".equals(imgPath)) {
            return image;
        }
        // 如果文字参数不为空，绘制文字
        if (StringUtils.isNotBlank(pressText)) {
            pressText(pressText, image, 1, Color.DARK_GRAY, 12, width, height);
        }
        // 插入图片
        QRCodeUtil.insertImage(image, imgPath, needCompress);
        return image;
    }

    /**
     * 嵌入logo
     * @param source 二维码图片
     * @param imgPath logo地址
     * @param needCompress 是否压缩
     * @throws Exception
     */
    private static void insertImage(BufferedImage source, String imgPath,
                                    boolean needCompress) throws Exception {
        File file = new File(imgPath);
        if (!file.exists()) {
            System.err.println(""+imgPath+" 该文件不存在！");
            return;
        }
        Image src = ImageIO.read(new File(imgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > WIDTH) {
                width = WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    /**
     * 在图片下方插入文字
     * @param pressText
     * @param image
     * @param fontStyle
     * @param color
     * @param fontSize
     * @param width
     * @param height
     */
    public static void pressText(String pressText, BufferedImage image,
                                 int fontStyle, Color color, int fontSize, int width, int height) {

        Font f = new Font("微软雅黑", Font.BOLD, fontSize);
        FontMetrics fm = FontDesignMetrics.getMetrics(f);
        //计算文字开始的位置
        //x开始的位置：（图片宽度-字符串宽度）/2
        int startX = (width - (fm.stringWidth(pressText))) / 2;
        //y开始的位置：图片高度-（图片高度-图片宽度）/2
        int startY = height - (height - width) / 2 - 10;

        // 调试图片，文字高度
        /*System.out.println("startX: " + startX);
        System.out.println("startY: " + startY);
        System.out.println("height: " + height);
        System.out.println("width: " + width);
        System.out.println("fontSize: " + fontSize);
        System.out.println("pressText.length(): " + pressText.length());*/

        try {
            int imageW = image.getWidth();
            int imageH = image.getHeight();
            Graphics g = image.createGraphics();
            g.drawImage(image, 0, 0, imageW, imageH, null);
            g.setColor(color);
            g.setFont(f);
            g.drawString(pressText, startX, startY);
            g.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 生成二维码（内嵌logo）
     * @param content 内容
     * @param imgPath logo地址
     * @param destPath 存放地址
     * @param needCompress 是否压缩
     * @throws Exception
     */
    public static void encode(String content, String imgPath, String destPath,
                              boolean needCompress, String pressText) throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content, imgPath,
                needCompress, pressText);
        mkdirs(destPath);
        String file = new Random().nextInt(99999999)+".jpg";
        ImageIO.write(image, FORMAT_NAME, new File(destPath+"/"+file));
    }

    /**
     *
     * @param destPath 存放路径
     */
    public static void mkdirs(String destPath) {
        File file =new File(destPath);
        //当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    /**
     * 生成二维码（logo），默认不压缩
     * @param content 内容
     * @param imgPath logo地址
     * @param destPath 存放地址
     * @throws Exception
     */
    public static void encode(String content, String imgPath, String destPath, String pressText)
            throws Exception {
        QRCodeUtil.encode(content, imgPath, destPath, false, pressText);
    }

    /**
     * 生成二维码
     * @param content 内容
     * @param destPath 存放地址
     * @param needCompress 是否压缩
     * @throws Exception
     */
    public static void encode(String content, String destPath,
                              boolean needCompress, String pressText) throws Exception {
        QRCodeUtil.encode(content, null, destPath, needCompress, pressText);
    }

    /**
     * 生成二维码，默认不压缩
     * @param content 内容
     * @param destPath 存放地址
     * @throws Exception
     */
    public static void encode(String content, String destPath, String pressText) throws Exception {
        QRCodeUtil.encode(content, null, destPath, false, pressText);
    }

    /**
     * 生成二维码（logo）
     * @param content 内容
     * @param imgPath logo地址
     * @param output 输出流
     * @param needCompress 是否压缩
     * @throws Exception
     */
    public static void encode(String content, String imgPath,
                              OutputStream output, boolean needCompress, String pressText) throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content, imgPath,
                needCompress, pressText);
        ImageIO.write(image, FORMAT_NAME, output);
    }

    /**
     * 生成二维码
     * @param content 内容
     * @param output 输出流
     * @throws Exception
     */
    public static void encode(String content, OutputStream output, String pressText)
            throws Exception {
        QRCodeUtil.encode(content, null, output, false, pressText);
    }

    /**
     * 生成二维码（带id参数）
     * @param plaintext 明文
     * @param secret 秘钥
     * @param destPath 存储路径
     * @param url 服务主地址
     * @return
     * @throws Exception
     */
    public static String encode(String plaintext, String secret,
                              String destPath, String url, String logoPath, String pressText) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("plaintext", plaintext);
        // 使用明文+秘钥生成唯一uuid
        String id = ApiSignUtils.signTopRequest(map, secret, "MD5");
        BufferedImage image = QRCodeUtil.createImage(url + "?id=" + id, logoPath,
                true, pressText);
        mkdirs(destPath);
        String file = id + ".jpg";
        ImageIO.write(image, FORMAT_NAME, new File(destPath + "/" + file));
        return id;
    }

    /**
     * 解析二维码
     * @param file
     * @return
     * @throws Exception
     */
    public static String decode(File file) throws Exception {
        BufferedImage image;
        image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(
                image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        result = new MultiFormatReader().decode(bitmap, hints);
        String resultStr = result.getText();
        return resultStr;
    }



    public static String decode(String path) throws Exception {
        return QRCodeUtil.decode(new File(path));
    }
}