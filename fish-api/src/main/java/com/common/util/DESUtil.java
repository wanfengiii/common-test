package com.common.util;

import lombok.extern.log4j.Log4j2;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Log4j2
public class DESUtil {

    /**
     * 偏移变量，固定占8位字节
     */
    private final static String IV_PARAMETER = "traceDes";
    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "DES";
    /**
     * 加密/解密算法-工作模式-填充模式
     */
    private static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";
    /**
     * 默认编码
     */
    private static final String CHARSET = "utf-8";

    /**
     * 生成key
     *
     * @param key
     * @return
     * @throws Exception
     */
    private static Key generateKey(String key) {
        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes(CHARSET));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            return keyFactory.generateSecret(dks);
        } catch (Exception e) {
            throw new RuntimeException("url加密生成key失败", e);
        }
    }

    /**
     * DES加密字符串
     *
     * @param key 加密密码
     * @param data 待加密字符串
     * @return 加密后内容
     */
    public static String encrypt(String key, String data) throws IllegalBlockSizeException, BadPaddingException {
        if (data == null)
            return null;
        try {
            Key secretKey = generateKey(key);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] bytes = cipher.doFinal(data.getBytes(CHARSET));
            return new String(Base64.getEncoder().encode(bytes));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException
                | InvalidKeyException | InvalidAlgorithmParameterException e) {
            log.error("url encrypt failed", e);
            throw new RuntimeException("文件签名加密发生错误", e);
        }
    }

    /**
     * DES解密字符串
     *
     * @param key
     * @param data 待解密字符串
     * @return 解密后内容
     */
    public static String decrypt(String key, String data) throws IllegalBlockSizeException, BadPaddingException {
        if (data == null) {
            return null;
        }
        try {
            Key secretKey = generateKey(key);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] b = Base64.getDecoder().decode(data.getBytes(CHARSET));
            b = cipher.doFinal(b);
            return new String(b, CHARSET);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException
                | InvalidKeyException | InvalidAlgorithmParameterException e) {
            log.error("url decrypt failed", e);
            throw new RuntimeException("文件签名解密发生错误", e);
        }
    }
}