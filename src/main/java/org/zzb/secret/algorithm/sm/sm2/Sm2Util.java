package org.zzb.secret.algorithm.sm.sm2;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;

import java.util.HashMap;
import java.util.Map;

/**
 * SM2秘钥生成、签名、验签工具类
 */
public class Sm2Util {

    /**
     * 公钥常量
     */
    public static final String KEY_PUBLIC_KEY = "publicKey";
    /**
     * 私钥返回值常量
     */
    public static final String KEY_PRIVATE_KEY = "privateKey";

    /**
     * 生成SM2公私钥
     *
     * @return
     */
    public static Map<String, String> generateSm2Key() {
        SM2 sm2 = new SM2();
        ECPublicKey publicKey = (ECPublicKey) sm2.getPublicKey();
        ECPrivateKey privateKey = (ECPrivateKey) sm2.getPrivateKey();
        // 获取公钥
        byte[] publicKeyBytes = publicKey.getQ().getEncoded(false);
        String publicKeyHex = HexUtil.encodeHexStr(publicKeyBytes);

        // 获取64位私钥
        String privateKeyHex = privateKey.getD().toString(16);
        // BigInteger转成16进制时，不一定长度为64，如果私钥长度小于64，则在前方补0
        StringBuilder privateKey64 = new StringBuilder(privateKeyHex);
        while (privateKey64.length() < 64) {
            privateKey64.insert(0, "0");
        }

        Map<String, String> result = new HashMap<>();
        result.put(KEY_PUBLIC_KEY, publicKeyHex);
        result.put(KEY_PRIVATE_KEY, privateKey64.toString());
        return result;
    }

    /**
     * SM2私钥签名
     *
     * @param privateKey 私钥
     * @param content    待签名内容
     * @return 签名值
     */
    public static String sign(String privateKey, String content) {
        SM2 sm2 = new SM2(privateKey, null);
        return sm2.signHex(HexUtil.encodeHexStr(content));
    }

    /**
     * SM2公钥验签
     *
     * @param publicKey 公钥
     * @param content   原始内容
     * @param sign      签名
     * @return 验签结果
     */
    public static boolean verify(String publicKey, String content, String sign) {
        SM2 sm2 = new SM2(null, publicKey);
        return sm2.verifyHex(HexUtil.encodeHexStr(content), sign);
    }

    /**
     * SM2公钥加密
     *
     * @param content   原文
     * @param publicKey SM2公钥
     * @return
     */
    public static String encryptBase64(String content, String publicKey) {
        SM2 sm2 = new SM2(null, publicKey);
        return sm2.encryptBase64(content, KeyType.PublicKey);
    }

    /**
     * SM2私钥解密
     *
     * @param encryptStr SM2加密字符串
     * @param privateKey SM2私钥
     * @return
     */
    public static String decryptBase64(String encryptStr, String privateKey) {
        SM2 sm2 = new SM2(privateKey, null);
        return StrUtil.utf8Str(sm2.decrypt(encryptStr, KeyType.PrivateKey));
    }

    public static void main(String[] args) {
        SM2 sm2 = new SM2();
        ECPublicKey publicKey = (ECPublicKey) sm2.getPublicKey();
        ECPrivateKey privateKey = (ECPrivateKey) sm2.getPrivateKey();
        // 获取公钥
        byte[] publicKeyBytes = publicKey.getQ().getEncoded(false);
        String publicKeyHex = HexUtil.encodeHexStr(publicKeyBytes);

        // 获取64位私钥
        String privateKeyHex = privateKey.getD().toString(16);
        // BigInteger转成16进制时，不一定长度为64，如果私钥长度小于64，则在前方补0
        StringBuilder privateKey64 = new StringBuilder(privateKeyHex);
        while (privateKey64.length() < 64) {
            privateKey64.insert(0, "0");
        }

        Map<String, String> result = new HashMap<>();
        result.put(KEY_PUBLIC_KEY, publicKeyHex);
        result.put(KEY_PRIVATE_KEY, privateKey64.toString());
        System.out.println(result);

        String str = "{\"result\": {\"name\": \"zzb\", \"idNumber\": \"xxxxx\", \"address\": \"xxxx\", \"gender\": \"男\", \"nationality\": \"汉\"}}";
        String encrypt = encryptBase64(str, result.get(KEY_PUBLIC_KEY));
        System.out.println("加密后结果：" + encrypt);

        String decrypt = decryptBase64(encrypt, result.get(KEY_PRIVATE_KEY));
        System.out.println("解密后结果：" + decrypt);
    }
}
