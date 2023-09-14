package com.dromara.auth.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    private static final Logger LOGGER = LogManager.getLogger(AESUtil.class);

    //key
    private static final String sKey = "absoietlj32fai12";

    //IV
    private static final String ivParameter = "absoietlj32fai12";

    //正常加解密逻辑使用
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * 加密
     *
     * @param srcData
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    public static byte[] AES_cbc_encrypt(byte[] srcData, byte[] key, byte[] iv) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
        byte[] encData = cipher.doFinal(srcData);
        return encData;
    }

    /**
     * 解密
     *
     * @param encData
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    public static byte[] AES_cbc_decrypt(byte[] encData, byte[] key, byte[] iv) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
        byte[] decData = cipher.doFinal(encData);
        return decData;
    }

    public static String encrypt(String data) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        try {
            byte[] bytes = AES_cbc_encrypt(data.getBytes(), sKey.getBytes(), ivParameter.getBytes());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            LOGGER.error("encrypt error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public static String decrypt(String data) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        try {
            byte[] decode = Base64.getDecoder().decode(data);
            return new String(AES_cbc_decrypt(decode, sKey.getBytes(), ivParameter.getBytes()));
        } catch (Exception e) {
            LOGGER.error("encrypt error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


}
