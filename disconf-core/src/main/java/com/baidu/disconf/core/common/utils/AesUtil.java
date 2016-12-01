/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

/*
 *
 *  (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *  All rights reserved.
 *
 */

package com.baidu.disconf.core.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.MessageDigest;
import java.security.Security;

/**
 * AES加密工具
 * @author luoshiqian 2016/11/14
 */
public class AesUtil {

    public static final Logger logger = LoggerFactory.getLogger(AesUtil.class);

    private static Cipher encryptCipher = null;

    private static Cipher decryptCipher = null;

    private static final String IV_128 = "iv_disconf_ymt_q";
    private static final String KEY_128 = "^disconf@ymt.cn$";

    static{

        try {
            Security.addProvider(new com.sun.crypto.provider.SunJCE());

            SecretKeySpec secretKey = new SecretKeySpec(IV_128.getBytes(),"AES");
            IvParameterSpec param = new IvParameterSpec(KEY_128.getBytes());
            // Cipher对象实际完成加密操作
            encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, param);

            decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            decryptCipher.init(Cipher.DECRYPT_MODE,secretKey,param);

        } catch (Exception e) {
            logger.error("init encrypt error",e);
        }
    }


    /**
     * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]
     * hexStr2ByteArr(String strIn) 互为可逆的转换过程
     * param arrB 需要转换的byte数组 return 转换后的字符串 throws Exception
     * 本方法不处理任何异常，所有异常全部抛出
     */
    public static String byteArr2HexStr(byte[] arrB) throws Exception {

        int iLen = arrB.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = arrB[i];
            // 把负数转换为正数
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }

            // 小于0F的数需要在前面补0
            if (intTmp < 16) {
                sb.append("0");
            }

            sb.append(Integer.toString(intTmp, 16));
        }

        return sb.toString();
    }


    /**
     * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
     * <p/>
     * 互为可逆的转换过程
     * <p/>
     * <p/>
     * <p/>
     * param strIn 需要转换的字符串 return 转换后的byte数组 throws Exception
     * <p/>
     * 本方法不处理任何异常，所有异常全部抛出
     */
    public static byte[] hexStr2ByteArr(String strIn) throws Exception {

        byte[] arrB = strIn.getBytes();

        int iLen = arrB.length;

        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen / 2];

        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }

        return arrOut;
    }


    /**
     * 加密字节数组
     * <p/>
     * <p/>
     * <p/>
     * param arrB 需加密的字节数组 return 加密后的字节数组 throws Exception
     */
    public static byte[] encrypt(byte[] arrB) throws Exception {
        return encryptCipher.doFinal(arrB);
    }


    /**
     * 加密字符串
     * <p/>
     * <p/>
     * <p/>
     * param strIn 需加密的字符串 return 加密后的字符串 throws Exception
     */
    public static String encrypt(String strIn) throws Exception {
        return byteArr2HexStr(encrypt(strIn.getBytes())).toUpperCase();
    }


    /**
     * 解密字节数组
     * <p/>
     * <p/>
     * <p/>
     * param arrB 需解密的字节数组 return 解密后的字节数组 throws Exception
     */
    public static byte[] decrypt(byte[] arrB) throws Exception {
        return decryptCipher.doFinal(arrB);
    }


    /**
     * 解密字符串
     * <p/>
     * <p/>
     * <p/>
     * param strIn 需解密的字符串 return 解密后的字符串 throws Exception
     */
    public static String decrypt(String strIn) throws Exception {

        return new String(decrypt(hexStr2ByteArr(strIn)));

    }


    /**
     * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
     * <p/>
     * <p/>
     * <p/>
     * param arrBTmp 构成该字符串的字节数组 return 生成的密钥 throws java.lang.Exception
     */
    private Key getKey(byte[] arrBTmp) throws Exception {

        // 创建一个空的8位字节数组（默认值为0）
        byte[] arrB = new byte[8];

        // 将原始字节数组转换为8位
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }

        // 生成密钥
        Key key = new SecretKeySpec(arrB, "DES");

        return key;
    }

    public static void main(String[] args) {
        try {
            System.out.println(encrypt("123456"));
            System.out.println(decrypt(encrypt("123456")));
            System.out.println(decrypt("F8CF4E9AE8690417A25E3E16B107A8B6524170E8AD939154AFED08BD72D3456DDDD8B81077271D86ABCBF7C21836F7F3C3C648EEE5D1F4BD773227944B57D402AD6A650003A387F708BA572BDCDF490736E35768E6764651D16ADC31758AA43FA135DF4B79646E4C7BD41FC02BA286442AB7EAF19C9FFAB07FE92D7011686773C4DDACBAB9E5BD8236BD6B31C98AEF25B21B6A706CBF14C2958A22B6C2F6FCFCCAAE6C63D5EE045C45F7EC1751ED0271B231F3449C8C6CAC46E0A5400E497BCC44E3ABAEBEA4EFEB787EAC9C941FD74B"));
        } catch (Exception e) {
        }
    }

    /**
     * 解密字节数组
     * <p/>
     * <p/>
     * <p/>
     * param arrB 需解密的字节数组 return 解密后的字节数组 throws Exception
     */
    public static String encryptTradingPwd(String pwd) throws Exception {
        return getMd5(encrypt(encrypt(encrypt(pwd))));
    }


    public static String getMd5(String text) {
        try {
            char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                    '9', 'A', 'B', 'C', 'D', 'E', 'F' };
            MessageDigest md = MessageDigest.getInstance("md5");
            md.update(text.getBytes("UTF-8"));

            byte[] bytes = md.digest();
            int j = bytes.length;
            char[] c = new char[j * 2];
            int k = 0;

            for (int i = 0; i < j; i++) {
                byte b = bytes[i];
                c[k++] = hexDigits[b >>> 4 & 0xf];
                c[k++] = hexDigits[b & 0xf];
            }

            return new String(c);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

}
