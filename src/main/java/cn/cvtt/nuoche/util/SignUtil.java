package cn.cvtt.nuoche.util;

import cn.cvtt.nuoche.common.Constant;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * @decription SignUtil
 * <p>通过signature对请求进行校验</p>
 * @author Yampery
 * @date 2018/3/6 14:48
 */
public class SignUtil {

    /**
     *
     * 验证消息的确来自微信服务器
     *
     * @param signature 微信加密签名
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @return
     */
    public static boolean validateSigniture(String signature, String timestamp, String nonce) {


        String[] array = new String[] {Constant.TOKEN, timestamp, nonce };

        // 1. 将三者进行字典排序
        Arrays.sort(array);

        // 2. 将三个参数拼接成一个字串
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            content.append(array[i]);
        }

        MessageDigest md = null;
        String sha = null;

        try {

            md = MessageDigest.getInstance("SHA-1");
            // 3. 将拼接后的字串进行SHA1加密
            byte[] digest = md.digest(content.toString().getBytes());
            sha = byteToStr(digest);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        //4. 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
        return null != sha ? sha.equals(signature.toUpperCase()) : false;
    }


    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray 字节数组
     * @return
     */
    public static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte 字节
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }
}
