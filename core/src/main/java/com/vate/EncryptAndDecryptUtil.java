package com.vate;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
public class EncryptAndDecryptUtil {

    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";//默认的加密算法
    private String encryptKey;
    private static final Logger log = LoggerFactory.getLogger(EncryptAndDecryptUtil.class);


    /**
     * 加密数据
     * @param content
     * @return
     */
    public String encryptContentForRqpanda(String content){
//        SymmetricCrypto des = new SymmetricCrypto(SymmetricAlgorithm.AES,getKey());
        try {
            return URLEncoder.encode(encrypt(content,encryptKey),"utf8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 解密数据
     * @param content
     * @return
     */
    public String decryptContentForRqpanda(String content){
//        SymmetricCrypto des = new SymmetricCrypto(SymmetricAlgorithm.AES,getKey());
        try {
            return URLDecoder.decode(decrypt(content,encryptKey),"utf8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

        /**
         * AES 加密操作
         *
         * @param content 待加密内容
         * @param password 加密密码
         * @return 返回Base64转码后的加密数据
         */
        public static String encrypt(String content, String password) {
            try {
                Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// 创建密码器

                byte[] byteContent = content.getBytes("utf-8");

                cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password));// 初始化为加密模式的密码器

                byte[] result = cipher.doFinal(byteContent);// 加密

                return Base64.encodeBase64String(result);//通过Base64转码返回
            } catch (Exception ex) {
                log.error("内容加密失败！");
            }
            return null;
        }

        /**
         * AES 解密操作
         *
         * @param content 待解密的内容
         * @param password 解密的密码
         * @return
         */
        public static String decrypt(String content, String password) {

            try {
                //实例化
                Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

                //使用密钥初始化，设置为解密模式
                cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password));

                //执行操作
                byte[] result = cipher.doFinal(Base64.decodeBase64(content));

                return new String(result, "utf-8");
            } catch (Exception ex) {
                log.error("内容解密失败,内容为:{}",content);
            }
            return null;
        }

        /**
         * 生成加密秘钥
         *
         * @return
         */
        private static SecretKeySpec getSecretKey(final String password) {
            //返回生成指定算法密钥生成器的 KeyGenerator 对象
            KeyGenerator kg = null;

            try {
                kg = KeyGenerator.getInstance(KEY_ALGORITHM);

                //AES 要求密钥长度为 128
                kg.init(128, new SecureRandom(password.getBytes()));

                //生成一个密钥
                SecretKey secretKey = kg.generateKey();

                return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);// 转换为AES专用密钥
            } catch (NoSuchAlgorithmException ex) {
                log.error("生成秘钥失败！");
            }
            return null;
        }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

        /**
         * 测试Main方法
         *
         * @param args
         */
        public static void main(String[] args) {
            String s = "你好，我是伍卿，helloWorld";
            System.out.println("s:" + s);
            String s1 = EncryptAndDecryptUtil.encrypt(s, "1234");
            System.out.println("s1:" + s1);
            System.out.println("s2:"+EncryptAndDecryptUtil.decrypt(s1, "1234"));
        }
}
