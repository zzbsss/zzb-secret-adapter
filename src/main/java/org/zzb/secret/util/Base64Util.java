package org.zzb.secret.util;


import org.bouncycastle.util.encoders.Base64;

/**
 * @author zzb
 * @version 1.0
 * @description:
 * @date 2024年4月14日11:15:13
 */
public class Base64Util {


    /**
     * Decoding to binary
     * @param base64 base64
     * @return byte
     * @throws Exception Exception
     */
    public static byte[] decode(String base64) throws Exception {
        return Base64.decode(base64);
    }

    /**
     * Binary encoding as a string
     * @param bytes byte
     * @return String
     * @throws Exception Exception
     */
    public static String encode(byte[] bytes) throws Exception {
        return new String(Base64.encode(bytes));
    }
}
