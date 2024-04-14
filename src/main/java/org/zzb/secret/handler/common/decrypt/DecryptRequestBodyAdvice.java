package org.zzb.secret.handler.common.decrypt;

import org.zzb.secret.config.SecureConfig;
import org.zzb.secret.util.RequetSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.lang.reflect.Type;
import java.text.MessageFormat;

/**
 * @author zzb
 * @version 1.0
 * @description:
 * @date 2024年4月14日11:15:13
 */
@ControllerAdvice
public class DecryptRequestBodyAdvice  implements RequestBodyAdvice {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    private final SecureConfig secureConfig;


    public DecryptRequestBodyAdvice(SecureConfig secureConfig) {
        this.secureConfig = secureConfig;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return RequetSupport.checkRequestBody(methodParameter, secureConfig, false);
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType){

        try {
            return new DecryptHttpInputMessage(inputMessage,parameter, targetType, converterType, secureConfig);
        } catch (Exception e) {
            log.error("Decryption failed", e);
            throw new RuntimeException(MessageFormat.format("Decryption failed message {0}", e.getMessage()));
        }

    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}
