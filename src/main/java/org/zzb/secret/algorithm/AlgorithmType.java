package org.zzb.secret.algorithm;
/**
 * @author zzb
 * @version 1.0
 * @description:
 * @date 2024年4月14日11:15:13
 */
public interface AlgorithmType {

    /**
     * 加密
     * @return
     */
    String encrypt(String data);

    /**
     * 解密
     *
     * @return
     */
    String decrypt(String data);

}
