package org.zzb.secret.handler.common.decrypt;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.zzb.secret.config.SecureConfig;
import org.zzb.secret.util.RequestSupport;

public class DecryptPathVariableResolver implements HandlerMethodArgumentResolver {

    private final SecureConfig secureConfig;


    public DecryptPathVariableResolver(SecureConfig secureConfig) {
        this.secureConfig = secureConfig;
    }


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return RequestSupport.checkRequestParam(parameter, secureConfig, false);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return null;
    }
}
