package org.zzb.secret.config;


import org.zzb.secret.constant.SecretKeyConstant;

import static org.zzb.secret.constant.SecretKeyConstant.UTF_8;


/**
 * @author zzb
 * @version 1.0
 * @description:
 * @date 2024年4月14日11:15:13
 */
public class SecureConfig {

    public static class Algorithm  {
        /**
         * 默认使用aes
         */
        private String algorithmName = SecretKeyConstant.AES;

        /**
         * 私钥
         */
        private String privateKey;

        /**
         * 公钥
         */
        private String publicKey;

        /**
         * 密钥
         */
        private String key;

        public String getAlgorithmName() {
            return algorithmName;
        }

        public void setAlgorithmName(String algorithmName) {
            this.algorithmName = algorithmName;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

    }

    /**
     * 实现方式 可使用comm、zuul 实现方式
     */
    private SecretKeyConstant.Type type = SecretKeyConstant.Type.comm;

    /**
     * 算法配置
     */
    private  Algorithm algorithm = new Algorithm();

    /**
     * 编码
     */
    private String charset = UTF_8;

    /**
     * 是否开启
     */
    private boolean enable = true;

    /**
     * 模式，默认使用注解
     */
    private SecretKeyConstant.Model model = SecretKeyConstant.Model.support;

    /**
     * 方向，默认入参解密，出参加密
     */
    private SecretKeyConstant.Direction direction = SecretKeyConstant.Direction.all;

    /**
     * 是否打印日志
     */
    private boolean enableLogged = false;

    public void setType(SecretKeyConstant.Type type) {
        this.type = type;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public SecretKeyConstant.Model getModel() {
        return model;
    }

    public void setModel(SecretKeyConstant.Model model) {
        this.model = model;
    }

    public SecretKeyConstant.Direction getDirection() {
        return direction;
    }

    public void setDirection(SecretKeyConstant.Direction direction) {
        this.direction = direction;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnableLogged() {
        return enableLogged;
    }

    public void setEnableLogged(boolean enableLogged) {
        this.enableLogged = enableLogged;
    }

    public SecretKeyConstant.Type getType() {
        return type;
    }

}
