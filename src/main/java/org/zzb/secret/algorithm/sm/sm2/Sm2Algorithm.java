package org.zzb.secret.algorithm.sm.sm2;

import org.zzb.secret.algorithm.AlgorithmType;
import org.zzb.secret.config.SecureConfig;
import org.zzb.secret.constant.SecretKeyConstant;

public class Sm2Algorithm implements AlgorithmType {

    private String privateKey;

    private String publicKey;

    private SecureConfig secureConfig;

    public Sm2Algorithm(SecureConfig secureConfig){
        this.privateKey = secureConfig.getAlgorithm().getPrivateKey();
        this.publicKey = secureConfig.getAlgorithm().getPublicKey();
        if(!SecretKeyConstant.SM2.equals(secureConfig.getAlgorithm().getAlgorithmName())) {
            return;
        }
        if (privateKey == null || privateKey.isEmpty()) {
            throw new RuntimeException("sm2 privateKey can not be null");
        }
        if (publicKey == null || publicKey.isEmpty()) {
            throw new RuntimeException("sm2 privateKey can not be null");
        }
        this.secureConfig = secureConfig;
    }


    @Override
    public String encrypt(String data) {
        try {
            return Sm2Util.encryptBase64(data, publicKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decrypt(String data) {
        try {
            return Sm2Util.decryptBase64(data, privateKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
