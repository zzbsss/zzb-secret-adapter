package org.zzb.secret.handler.common.encrypt;

import com.alibaba.fastjson.JSON;
import java.text.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.zzb.secret.algorithm.AlgorithmType;
import org.zzb.secret.config.SecureConfig;
import org.zzb.secret.factory.AlgorithmFactory;
import org.zzb.secret.util.RequestSupport;

/**
 * @author zzb
 * @version 1.0
 * @description:
 * @date 2024年4月14日11:15:13
 */
@ControllerAdvice
@ConditionalOnExpression("#{${zzb.secure.enable} && T(org.zzb.secret.constant.SecretKeyConstant.Type).valueOf('${zzb.secure.type}') == T(org.zzb.secret.constant.SecretKeyConstant.Type).comm}")
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SecureConfig secureConfig;

    public EncryptResponseBodyAdvice(SecureConfig secureConfig) {
        this.secureConfig = secureConfig;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        return RequestSupport.checkRequestBody(methodParameter, secureConfig, false);
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
