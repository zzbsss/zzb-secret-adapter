package org.zzb.secret.config;


import org.zzb.secret.constant.SecretKeyConstant;

import java.util.*;

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

    public static Map<String,String> requestParamMap = new HashMap<>();

    public static Map<String,String> responseParamMap = new HashMap<>();


    /**
     * 需要加解密的参数
     */
    public static class SecureParams  {

        /**
         * 需要解密的请求参数，多个请以逗号隔开
         */
        private String request;

        /**
         * 需要加密的响应参数，多个请以逗号隔开
         */
        private String response;

        public String getRequest() {
            return request;
        }

        public void setRequest(String request) {
            this.request = request;
            if (Objects.isNull(request) || request.length() == 0) {
               return;
            }
            String[] split = request.split(",");
            for (String str : split) {
                requestParamMap.put(str, str);
            }
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
            if (Objects.isNull(response) || response.length() == 0) {
                return;
            }
            String[] split = response.split(",");
            for (String str : split) {
                responseParamMap.put(str, str);
            }
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
     * 参数配置
     */
    private SecureParams secureParams = new SecureParams();

    /**
     * 编码
     */
    private String charset = UTF_8;


    /**
     * 模式，默认使用注解
     */
    private SecretKeyConstant.Model model = SecretKeyConstant.Model.support;

    /**
     * 方向，默认入参解密，出参加密
     */
    private SecretKeyConstant.Direction direction = SecretKeyConstant.Direction.all;

    /**
     * 请求白名单， 不做加解密
     */
    private Set<String> whiteUrls =  new HashSet<>();


    /**
     * 请求头标识，只对指定请求进行加解密
     */
    private String headerFlag;


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

    public SecretKeyConstant.Type getType() {
        return type;
    }

    public Set<String> getWhiteUrls() {
        return whiteUrls;
    }

    public void setWhiteUrls(Set<String> whiteUrls) {
        this.whiteUrls = whiteUrls;
    }

    public String getHeaderFlag() {
        return headerFlag;
    }

    public void setHeaderFlag(String headerFlag) {
        this.headerFlag = headerFlag;
    }

    public static Map<String, String> getRequestParamMap() {
        return requestParamMap;
    }

    public static void setRequestParamMap(Map<String, String> requestParamMap) {
        SecureConfig.requestParamMap = requestParamMap;
    }

    public static Map<String, String> getResponseParamMap() {
        return responseParamMap;
    }

    public static void setResponseParamMap(Map<String, String> responseParamMap) {
        SecureConfig.responseParamMap = responseParamMap;
    }

    public SecureParams getSecureParams() {
        return secureParams;
    }

    public void setSecureParams(SecureParams secureParams) {
        this.secureParams = secureParams;
    }
}
