package org.zzb.secret.util;

import com.netflix.util.Pair;
import com.netflix.zuul.context.RequestContext;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.zzb.secret.annotation.DecryptBody;
import org.zzb.secret.annotation.DecryptParam;
import org.zzb.secret.annotation.EncryptBody;
import org.zzb.secret.config.SecureConfig;
import org.zzb.secret.constant.SecretKeyConstant;
import org.springframework.core.MethodParameter;

import java.util.Objects;

import static org.zzb.secret.constant.SecretKeyConstant.APP_JSON;
import static org.zzb.secret.constant.SecretKeyConstant.CONTENT_TYPE;

public class RequestSupport {

    /**
     * 检验通用响应是否执行
     * @param methodParameter
     * @param secureConfig
     * @param defaultVal
     * @return
     */
    public static boolean checkRequestBody(MethodParameter methodParameter, SecureConfig secureConfig, boolean defaultVal) {
        // 配置了全局打开
        if (secureConfig.getModel() == SecretKeyConstant.Model.all) {
            return true;
        }
        // 配置了单方向开启 入方向解密
        if (secureConfig.getModel() == SecretKeyConstant.Model.single && secureConfig.getDirection() == SecretKeyConstant.Direction.response) {
            return true;
        }
        // 判断优先级 方法上 EncryptBody  -> 类上 EncryptBody
        if (secureConfig.getModel() == SecretKeyConstant.Model.support  && (methodParameter.getMethod().isAnnotationPresent(EncryptBody.class)
                ||  methodParameter.getContainingClass().isAnnotationPresent(EncryptBody.class))){
            return true;
        }
        return defaultVal;
    }

    /**
     * 检验通用请求体响应是否执行
     * @param methodParameter
     * @param secureConfig
     * @param defaultVal
     * @return
     */
    public static boolean checkResponseParam(MethodParameter methodParameter, SecureConfig secureConfig, boolean defaultVal) {
        // 配置了全局打开
        if (secureConfig.getModel() == SecretKeyConstant.Model.all) {
            return true;
        }
        // 配置了单方向开启 入方向解密
        if (secureConfig.getModel() == SecretKeyConstant.Model.single && secureConfig.getDirection() == SecretKeyConstant.Direction.response) {
            return true;
        }
        // 判断优先级 方法上 DecryptBody  -> 类上 DecryptBody
        if (secureConfig.getModel() == SecretKeyConstant.Model.support  && (methodParameter.getMethod().isAnnotationPresent(DecryptBody.class)
                ||  methodParameter.getContainingClass().isAnnotationPresent(DecryptBody.class))){
            return true;
        }
        return defaultVal;
    }

    /**
     * 检验通用请求是否执行
     * @param parameter
     * @param secureConfig
     * @param defaultVal
     * @return
     */
    public static boolean checkRequestParam(MethodParameter parameter, SecureConfig secureConfig, boolean defaultVal) {
        // 配置了全局打开
        if (secureConfig.getModel() == SecretKeyConstant.Model.all) {
            return true;
        }
        // 配置了单方向开启 入方向解密
        if (secureConfig.getModel() == SecretKeyConstant.Model.single && secureConfig.getDirection() == SecretKeyConstant.Direction.request) {
            return true;
        }
        // 判断优先级 参数上 DecryptParam -> -> 方法上 DecryptParam
        if ((secureConfig.getModel() == SecretKeyConstant.Model.support  && (parameter.hasParameterAnnotation(DecryptParam.class)
                ||  parameter.getMethod().isAnnotationPresent(DecryptParam.class)))){
            return true;
        }
        return defaultVal;
    }

    /**
     * 校验zuul网关请求是否执行
     * @param secureConfig
     * @return
     */
    public static boolean checkZuulRequestParam(SecureConfig secureConfig) {
        // 配置了全局打开
        if (secureConfig.getModel() == SecretKeyConstant.Model.all) {
            return true;
        }
        // 配置了单方向开启 入方向解密
        if (secureConfig.getModel() == SecretKeyConstant.Model.single && secureConfig.getDirection() == SecretKeyConstant.Direction.request) {
            return true;
        }
        return false;
    }


    /**
     * 校验zuul网关响应是否执行
     * @param secureConfig
     * @return
     */
    public static boolean checkZuulResponseParam(SecureConfig secureConfig) {
        // 配置了全局打开
        if (secureConfig.getModel() == SecretKeyConstant.Model.all) {
            return true;
        }
        // 配置了单方向开启 入方向解密
        if (secureConfig.getModel() == SecretKeyConstant.Model.single && secureConfig.getDirection() == SecretKeyConstant.Direction.response) {
            return true;
        }
        // 仅对json请求进行记录
        List<String> contentTypeList = RequestContext.getCurrentContext().getZuulResponseHeaders().stream().
                filter(x -> x.first().equals(CONTENT_TYPE)).map(Pair::second).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(contentTypeList)) {
            return false;
        }
        String contentType = contentTypeList.get(0);
        // 仅对json请求记录出参 form表单是否需要支持？？  仅对200 状态码 进行日志记录，避免被攻击
        return Objects.nonNull(contentType) && contentType.contains(APP_JSON) && HttpStatus.OK.value() ==  RequestContext.getCurrentContext().getResponseStatusCode();
    }
}
