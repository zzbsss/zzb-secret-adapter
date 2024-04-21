package org.zzb.secret.handler.common.decrypt;

import cn.hutool.core.util.ReflectUtil;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.zzb.secret.algorithm.AlgorithmType;
import org.zzb.secret.annotation.DecryptParam;
import org.zzb.secret.config.SecureConfig;
import org.zzb.secret.factory.AlgorithmFactory;
import org.zzb.secret.util.RequestSupport;

import java.lang.reflect.Field;
import java.util.Objects;


public class DecryptRequestParamResolver implements HandlerMethodArgumentResolver {
    private final SecureConfig secureConfig;

    public DecryptRequestParamResolver(SecureConfig secureConfig) {
        this.secureConfig = secureConfig;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return RequestSupport.checkRequestParam(methodParameter, secureConfig, false);
    }


    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        // 获取到当前配置的解密算法
        AlgorithmType algorithmType = AlgorithmFactory.algorithmFactory.get();
        DecryptParam decryptParam = parameter.getParameterAnnotation(DecryptParam.class);
        String paramName = decryptParam.value();
        if (Objects.isNull(paramName) || StringUtils.isEmpty(paramName)) {
            paramName = parameter.getParameterName();
        }
        Class<?> parameterType = parameter.getParameterType();
        if (String.class.isAssignableFrom(parameterType)) {
            String value = webRequest.getParameter(paramName);
            // 为空值不解密
            if (Objects.isNull(value) || StringUtils.isEmpty(value)) {
                return value;
            }
            return algorithmType.decrypt(value);
        }
        // 非String ,通过反射赋值
        Object obj = ReflectUtil.newInstance(parameterType);
        Field[] fields = ReflectUtil.getFields(parameterType);
        for (Field field : fields) {
            field.setAccessible(true);
            String val = webRequest.getParameter(field.getName());
            if (Objects.nonNull(val) && !StringUtils.isEmpty(val)) {
                String decrypted = algorithmType.decrypt(val);
                ReflectUtil.setFieldValue(obj, field, decrypted);
            }
        }
        return obj;
    }
}

