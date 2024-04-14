package org.zzb.secret.util;

import org.zzb.secret.annotation.DecryptBody;
import org.zzb.secret.annotation.DecryptParam;
import org.zzb.secret.annotation.EncryptDecrypt;
import org.zzb.secret.config.SecureConfig;
import org.zzb.secret.constant.SecretKeyConstant;
import org.springframework.core.MethodParameter;

import java.util.Objects;

public class RequetSupport {

    public static boolean checkRequestBody(MethodParameter methodParameter, SecureConfig secureConfig, boolean defaultVal) {
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
        // 配置了单方向开启 入方向解密
        if (secureConfig.getModel() == SecretKeyConstant.Model.single && secureConfig.getDirection() == SecretKeyConstant.Direction.request) {
            return true;
        }
        // 判断优先级 方法上 Decrypt ->方法上 EncryptDecrypt -> 类上 Decrypt -> 类上 EncryptDecrypt
        if (secureConfig.getModel() == SecretKeyConstant.Model.support  && (methodParameter.getMethod().isAnnotationPresent(DecryptBody.class) || methodParameter.getMethod().isAnnotationPresent(EncryptDecrypt.class)
                ||  methodParameter.getContainingClass().isAnnotationPresent(DecryptBody.class) ||  methodParameter.getContainingClass().isAnnotationPresent(EncryptDecrypt.class))){
            return true;
        }
        return defaultVal;
    }

    public static boolean checkRequestParam(MethodParameter parameter, SecureConfig secureConfig, boolean defaultVal) {
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
        // 配置了单方向开启 入方向解密
        if (secureConfig.getModel() == SecretKeyConstant.Model.single && secureConfig.getDirection() == SecretKeyConstant.Direction.request) {
            return true;
        }
        // 判断优先级 参数上 DecryptParam ->方法上 EncryptDecrypt -> 类上 DecryptParam -> 类上 EncryptDecrypt
        if ((secureConfig.getModel() == SecretKeyConstant.Model.support  && (parameter.hasParameterAnnotation(DecryptParam.class) || parameter.getMethod().isAnnotationPresent(EncryptDecrypt.class)
                ||  parameter.getContainingClass().isAnnotationPresent(DecryptParam.class) ||  parameter.getContainingClass().isAnnotationPresent(EncryptDecrypt.class)))){
            return true;
        }
        return defaultVal;
    }
}
