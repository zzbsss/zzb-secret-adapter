package org.zzb.secret.handler.common.encrypt;

import com.alibaba.fastjson.JSON;
import org.zzb.secret.algorithm.AlgorithmType;
import org.zzb.secret.annotation.Encrypt;
import org.zzb.secret.annotation.EncryptDecrypt;
import org.zzb.secret.config.SecureConfig;
import org.zzb.secret.constant.SecretKeyConstant;
import org.zzb.secret.factory.AlgorithmFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * @author zzb
 * @version 1.0
 * @description:
 * @date 2024年4月14日11:15:13
 */
@ControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SecureConfig secureConfig;

    public EncryptResponseBodyAdvice(SecureConfig secureConfig) {
        this.secureConfig = secureConfig;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        if (Objects.isNull(secureConfig) || !secureConfig.isEnable()) {
            return false;
        }
        // 不是通用实现 返回false
        if (secureConfig.getType() != SecretKeyConstant.Type.comm) {
            return false;
        }
        // 配置了全局打开
        if (secureConfig.getModel() == SecretKeyConstant.Model.all) {
            return true;
        }
        // 配置了单方向开启 出方向加密
        if (secureConfig.getModel() == SecretKeyConstant.Model.single && secureConfig.getDirection() == SecretKeyConstant.Direction.response) {
            return true;
        }
        // 判断优先级 方法上 Encrypt ->方法上 EncryptDecrypt -> 类上 Encrypt -> 类上 EncryptDecrypt
        if (secureConfig.getModel() == SecretKeyConstant.Model.support && (methodParameter.getMethod().isAnnotationPresent(Encrypt.class) || methodParameter.getMethod().isAnnotationPresent(EncryptDecrypt.class)
                ||  methodParameter.getContainingClass().isAnnotationPresent(Encrypt.class) ||  methodParameter.getContainingClass().isAnnotationPresent(EncryptDecrypt.class))){
            return true;
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        try {
            String content = JSON.toJSONString(body);
            // 获取到当前配置的解密算法
            AlgorithmType algorithmType = AlgorithmFactory.algorithmFactory.get();
            return algorithmType.encrypt(content);
        } catch (Exception e) {
            log.error("Encrypted data exception", e);
            throw new RuntimeException(MessageFormat.format("Encrypted data exception, message{0}",e.getMessage()));
        }
    }
}
