package org.zzb.secret.algorithm.sm.sm4;

import org.zzb.secret.algorithm.sm.GmBaseUtil;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
/**
 * @author zzb
 * @version 1.0
 * @description:
 * @date 2024年4月14日11:15:13
 */
public class Sm4Util extends GmBaseUtil {
    public static final String ALGORITHM_NAME = "SM4";

    public static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS5Padding";

    public static final String ALGORITHM_NAME_CBC_PADDING = "SM4/CBC/PKCS5Padding";

    public static final int DEFAULT_KEY_SIZE = 128;

    public static final int DEFAULT_IV_SIZE = 16;

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    public static String generateIvToString() throws Exception {
        return generateIvToString(DEFAULT_IV_SIZE);
    }

    public static byte[] generateIv() throws Exception {
        return generateIv(DEFAULT_IV_SIZE);
    }

    public static String generateIvToString(int size) throws Exception {
        return Base64.getEncoder().encodeToString(generateIv(size));
    }

    public static byte[] generateIv(int size) throws Exception {
        byte[] iv = new byte[size];
        (new SecureRandom()).nextBytes(iv);
        return iv;
    }

    public static String generateKeyToString() throws Exception {
        return generateKeyToString(DEFAULT_KEY_SIZE);
    }

    public static byte[] generateKey() throws Exception {
        return generateKey(DEFAULT_KEY_SIZE);
    }

    public static String generateKeyToString(int keySize) throws Exception {
        return Base64.getEncoder().encodeToString(generateKey(keySize));
    }

    public static byte[] generateKey(int keySize) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_NAME, "BC");
        kg.init(keySize, new SecureRandom());
        return kg.generateKey().getEncoded();
    }

    private static Cipher generateEcbCipher(String algorithmName, int mode, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithmName, "BC");
        Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        cipher.init(mode, sm4Key);
        return cipher;
    }

    public static byte[] encryptEcbPadding(byte[] key, byte[] data) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, 1, key);
        return cipher.doFinal(data);
    }

    public static String encryptEcb(String key, String data) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(key);
        return Base64.getEncoder().encodeToString(encryptEcbPadding(decoded, data.getBytes(CHARSET)));
    }

    public static byte[] decryptEcbPadding(byte[] key, byte[] cipherText) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, 2, key);
        return cipher.doFinal(cipherText);
    }

    public static String decryptEcb(String key, String cipherText) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(key);
        return new String(decryptEcbPadding(decoded, Base64.getDecoder().decode(cipherText.getBytes(CHARSET))), CHARSET);
    }

    public static byte[] encryptCbcPadding(byte[] key, byte[] iv, byte[] data) throws Exception {
        Cipher cipher = generateCbcCipher(ALGORITHM_NAME_CBC_PADDING, 1, key, iv);
        return cipher.doFinal(data);
    }

    public static String encryptCbc(String key, String iv, String data) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(key);
        byte[] ivb = Base64.getDecoder().decode(iv);
        return Base64.getEncoder().encodeToString(encryptCbcPadding(decoded, ivb, data.getBytes(CHARSET)));
    }

    public static byte[] decryptCbcPadding(byte[] key, byte[] iv, byte[] cipherText) throws Exception {
        Cipher cipher = generateCbcCipher(ALGORITHM_NAME_CBC_PADDING, 2, key, iv);
        return cipher.doFinal(cipherText);
    }

    public static String decryptCbc(String key, String iv, String cipherText) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(key);
        byte[] ivb = Base64.getDecoder().decode(iv);
        return new String(decryptCbcPadding(decoded, ivb, Base64.getDecoder().decode(cipherText.getBytes(CHARSET))), CHARSET);
    }

    private static Cipher generateCbcCipher(String algorithmName, int mode, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithmName, "BC");
        Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(mode, sm4Key, ivParameterSpec);
        return cipher;
    }
}
