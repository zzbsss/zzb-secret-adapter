package org.zzb.secret.algorithm.ras;

import org.zzb.secret.algorithm.AlgorithmType;
import org.zzb.secret.config.SecureConfig;
import org.zzb.secret.constant.SecretKeyConstant;

public class RsaAlgorithm implements AlgorithmType {

    private String privateKey;

    private String publicKey;

    private SecureConfig secureConfig;

    public RsaAlgorithm(SecureConfig secureConfig){
        this.privateKey = secureConfig.getAlgorithm().getPrivateKey();
        this.publicKey = secureConfig.getAlgorithm().getPublicKey();
        if(!SecretKeyConstant.SM2.equals(secureConfig.getAlgorithm().getAlgorithmName())) {
            return;
        }
        if (privateKey == null || privateKey.isEmpty()) {
            throw new RuntimeException("rsa privateKey can not be null");
        }
        if (publicKey == null || publicKey.isEmpty()) {
            throw new RuntimeException("rsa privateKey can not be null");
        }
        this.secureConfig = secureConfig;
    }


    @Override
    public String encrypt(String data) {
        try {
            return new String(RSAUtil.encrypt(data.getBytes(secureConfig.getCharset()), publicKey));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decrypt(String data) {
        try {
            return new String(RSAUtil.decrypt(data.getBytes(secureConfig.getCharset()), privateKey));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
