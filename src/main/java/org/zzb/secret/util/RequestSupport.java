package org.zzb.secret.util;

import com.netflix.util.Pair;
import com.netflix.zuul.context.RequestContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zzb.secret.annotation.DecryptBody;
import org.zzb.secret.annotation.EncryptBody;
import org.zzb.secret.config.SecureConfig;
import org.zzb.secret.constant.SecretKeyConstant;
import org.zzb.secret.context.SecretContext;
import org.zzb.secret.context.SecretContextHolder;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.zzb.secret.constant.SecretKeyConstant.APP_JSON;
import static org.zzb.secret.constant.SecretKeyConstant.CONTENT_TYPE;

public class RequestSupport {

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 检验通用响应是否执行
     * @param methodParameter
     * @param secureConfig
     * @param defaultVal
     * @return
     */
    public static boolean checkResponseBody(MethodParameter methodParameter, SecureConfig secureConfig, boolean defaultVal) {
        Result result = getResult(secureConfig);
        // 配置了全局打开
        if (secureConfig.getModel() == SecretKeyConstant.Model.all) {
            return check(result);
        }
        // 配置了单方向开启 chu方向解密
        if (secureConfig.getModel() == SecretKeyConstant.Model.single && secureConfig.getDirection() == SecretKeyConstant.Direction.response) {
            return check(result);
        }
        // 判断优先级 方法上 EncryptBody  -> 类上 EncryptBody
        if (secureConfig.getModel() == SecretKeyConstant.Model.support  && (methodParameter.getMethod().isAnnotationPresent(EncryptBody.class)
                ||  methodParameter.getContainingClass().isAnnotationPresent(EncryptBody.class))){
            return check(result);
        }
        return defaultVal;
    }

    private static Result getResult(SecureConfig secureConfig) {
        SecretContext secretContext = SecretContextHolder.getSecretContext();
        // 当前路径是否为白名单
        boolean isWhite = false;
        if (secureConfig.getWhiteUrls().size() > 0) {
            String requestUrl = secretContext.getRequestUrl();
            Set<String> whiteUrls = secureConfig.getWhiteUrls();
            for (String whiteUrl : whiteUrls) {
                if (pathMatcher.match(whiteUrl, requestUrl)) {
                    isWhite = true;
                    break;
                }
            }
        }
        // 当前路径是否有指定请求头
        boolean isHeadFlag = false;
        String headerFlag = secureConfig.getHeaderFlag();
        // 包含即可，不校验内容
        if (headerFlag != null && !StringUtils.isEmpty(headerFlag)) {
            isHeadFlag = Objects.nonNull(secretContext.getRequestHeader().get(headerFlag));
        }
        return new Result(isWhite, isHeadFlag, headerFlag);
    }

    private static class Result {
        public final boolean isWhite;
        public final boolean isHeadFlag;
        public final String headerFlag;

        public Result(boolean isWhite, boolean isHeadFlag, String headerFlag) {
            this.isWhite = isWhite;
            this.isHeadFlag = isHeadFlag;
            this.headerFlag = headerFlag;
        }
    }

    private static boolean check(Result result) {
        // 是白名单 返回false 不执行加解密
        if (result.isWhite) {
            return false;
        }
        // 不是白名单，但包含指定请求头
        if (result.headerFlag != null && !StringUtils.isEmpty(result.headerFlag)) {
            return result.isHeadFlag;
        }
        // 不是白名单，也没有指定头，默认返回true
        return true;
    }

    /**
     * 检验通用请求体响应是否执行
     * @param methodParameter
     * @param secureConfig
     * @param defaultVal
     * @return
     */
    public static boolean checkRequestBody(MethodParameter methodParameter, SecureConfig secureConfig, boolean defaultVal) {
        Result result = getResult(secureConfig);
        // 配置了全局打开
        if (secureConfig.getModel() == SecretKeyConstant.Model.all) {
            return check(result);
        }
        // 配置了单方向开启 入方向解密
        if (secureConfig.getModel() == SecretKeyConstant.Model.single && secureConfig.getDirection() == SecretKeyConstant.Direction.response) {
            return check(result);
        }
        // 判断优先级 方法上 DecryptBody  -> 类上 DecryptBody
        if (secureConfig.getModel() == SecretKeyConstant.Model.support  && (methodParameter.getMethod().isAnnotationPresent(DecryptBody.class)
                ||  methodParameter.getContainingClass().isAnnotationPresent(DecryptBody.class))){
            return check(result);
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
    public static boolean checkRequestParam(MethodParameter parameter, SecureConfig secureConfig, Class<? extends Annotation> classes, boolean defaultVal) {
        Result result = getResult(secureConfig);
        // 配置了全局打开
        if (secureConfig.getModel() == SecretKeyConstant.Model.all) {
            return check(result);
        }
        // 配置了单方向开启 入方向解密
        if (secureConfig.getModel() == SecretKeyConstant.Model.single && secureConfig.getDirection() == SecretKeyConstant.Direction.request) {
            return check(result);
        }
        // 判断优先级 参数上 DecryptParam -> -> 方法上 DecryptParam
        if ((secureConfig.getModel() == SecretKeyConstant.Model.support  && (parameter.hasParameterAnnotation(classes)
                ||  parameter.getMethod().isAnnotationPresent(classes)))){
            return check(result);
        }
        return defaultVal;
    }

    /**
     * 校验zuul网关请求是否执行
     * @param secureConfig
     * @return
     */
    public static boolean checkZuulRequestParam(SecureConfig secureConfig) {
        Result result = getResult(secureConfig);
        // 配置了全局打开
        if (secureConfig.getModel() == SecretKeyConstant.Model.all) {
            return check(result);
        }
        // 配置了单方向开启 入方向解密
        if (secureConfig.getModel() == SecretKeyConstant.Model.single && secureConfig.getDirection() == SecretKeyConstant.Direction.request) {
            return check(result);
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
        // 仅对json请求记录出参  仅对200 状态码 进行日志记录，避免被攻击
        return Objects.nonNull(contentType) && contentType.contains(APP_JSON) && HttpStatus.OK.value() ==  RequestContext.getCurrentContext().getResponseStatusCode();
    }
}
