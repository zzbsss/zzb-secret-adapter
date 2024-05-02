package org.zzb.secret.algorithm.aes;

import org.zzb.secret.algorithm.AlgorithmType;
import org.zzb.secret.config.SecureConfig;
import org.zzb.secret.constant.SecretKeyConstant;
import org.zzb.secret.exception.SecretException;

import java.nio.charset.Charset;

/**
 * @author zzb
 * @version 1.0
 * @description:
 * @date 2024年4月14日11:15:13
 */
public class AesAlgorithm implements AlgorithmType {

    private String key;

    private SecureConfig secureConfig;

    public AesAlgorithm(String key) {
        this.key = key;
    }

    public AesAlgorithm(SecureConfig secureConfig) {
        String sk = secureConfig.getAlgorithm().getKey();
        if(!SecretKeyConstant.AES.equals(secureConfig.getAlgorithm().getAlgorithmName())) {
            return;
        }
        if (sk == null || sk.isEmpty()) {
            throw new SecretException("aes key can not be null");
        }
        this.key = secureConfig.getAlgorithm().getKey();
        this.secureConfig = secureConfig;
    }

    @Override
    public String encrypt(String data) {
        try {
            return AesUtils.encrypt(data, key, Charset.forName(secureConfig.getCharset()));
        } catch (Exception e) {
            throw new SecretException(e);
        }
    }

    @Override
    public String decrypt(String data) {
        try {
            return  AesUtils.decrypt(data, key, Charset.forName(secureConfig.getCharset()));
        } catch (Exception e) {
            throw new SecretException(e);
        }
    }
}
