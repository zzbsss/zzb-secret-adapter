package org.zzb.secret.algorithm.aes;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Security;

/**
 * @author zzb
 * @version 1.0
 * @description:
 * @date 2024年4月14日11:15:13
 */
public class AesUtils {
    /**
     * 密钥算法
     */
    private static final String KEY_ALGORITHM = "AES";

    /**
     * 安全令牌字符串.
     */
    public static final String SECURITY_TOKEN_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * 安全字符串.
     */
    public static final String SECURITY_PASSWORD_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*-=<>/`~?_";


    /**
     * 加密/解密算法 / 工作模式 / 填充方式
     * Java 6支持PKCS5Padding填充方式
     * Bouncy Castle支持PKCS7Padding填充方式
     */
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS7Padding";

    static {
        //如果是PKCS7Padding填充方式，则必须加上下面这行
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 生成密钥(16位随机字母数字)
     *
     * @return 密钥 string
     * @throws Exception the exception
     */
    public static String generateKey() throws Exception {
        return RandomUtil.randomString(SECURITY_TOKEN_CHARS, 16);
    }

    /**
     * 生成随机密码(16位随机字母数字特殊字符)
     *
     * @return 密钥 string
     */
    public static String generatePassWord() {
        return RandomUtil.randomString(SECURITY_PASSWORD_CHARS, 16);
    }

    /**
     * AES加密
     *
     * @param source  源字符串
     * @param key     密钥
     * @return 加密后的密文 string
     * @throws Exception the exception
     */
    public static String encrypt(String source, String key) throws Exception {
       return  encrypt(source, key, StandardCharsets.UTF_8);
    }

    /**
     * AES加密
     *
     * @param source  源字符串
     * @param key     密钥
     * @param charset
     * @return 加密后的密文 string
     * @throws Exception the exception
     */
    public static String encrypt(String source, String key, Charset charset) throws Exception {
        byte[] sourceBytes = source.getBytes(charset);
        byte[] keyBytes = key.getBytes(charset);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, KEY_ALGORITHM));
        byte[] decrypted = cipher.doFinal(sourceBytes);
        return new String(HexUtil.encodeHex(decrypted));
    }

    /**
     * AES解密
     *
     * @param encryptStr 加密后的密文
     * @param key        密钥
     * @return 源字符串 string
     * @throws Exception the exception
     */
    public static String decrypt(String encryptStr, String key) throws Exception {
       return decrypt(encryptStr, key, StandardCharsets.UTF_8);
    }

    /**
     * AES解密
     *
     * @param encryptStr 加密后的密文
     * @param key        密钥
     * @param charset
     * @return 源字符串 string
     * @throws Exception the exception
     */
    public static String decrypt(String encryptStr, String key, Charset charset) throws Exception {
        byte[] sourceBytes = HexUtil.decodeHex(encryptStr);
        byte[] keyBytes = key.getBytes(charset);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, KEY_ALGORITHM));
        byte[] decoded = cipher.doFinal(sourceBytes);
        return new String(decoded, charset);
    }

    public static void main(String[] args) throws Exception {
        String s = generateKey();
        System.out.println( s);
        System.out.println(encrypt("{\"result\": {\"name\": \"zzb\", \"idNumber\": \"xxxx\", \"address\": \"xxxx\", \"gender\": \"男\", \"nationality\": \"汉\"}}", s));
        System.out.println(decrypt("8694008fea61ef49aa8ed46f208cdf61", "sS5V2GTxgdv67eQm"));
        System.out.println();
        System.out.println(encrypt("testparam", "qabAXTk9Z1akNjkQ"));
        System.out.println(encrypt("18", "qabAXTk9Z1akNjkQ"));
    }
}
