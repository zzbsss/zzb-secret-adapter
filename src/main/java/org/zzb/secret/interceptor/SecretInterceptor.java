package org.zzb.secret.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.zzb.secret.config.SecureConfig;
import org.zzb.secret.constant.SecretKeyConstant;
import org.zzb.secret.context.SecretContext;
import org.zzb.secret.context.SecretContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Map;


public class SecretInterceptor implements HandlerInterceptor {
    private SecureConfig secureConfig;
    public SecretInterceptor(SecureConfig secureConfig) {
        this.secureConfig = secureConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 在请求处理之前可以设置ThreadLocal变量
        SecretContext secretContext = new SecretContext();
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, Object> requestHeader = secretContext.getRequestHeader();
        while (headerNames.hasMoreElements()) {
            String headName = headerNames.nextElement();
            requestHeader.put(headName, request.getHeader(headName));
        }
        // 请求url
        // 网关应用不需要删除context-path ,配置白名单时，需带上转发路径
        secretContext.setRequestUrl(secureConfig.getType().getName().equals(SecretKeyConstant.Type.comm.getName())?
                 request.getRequestURI().substring(request.getContextPath().length()): request.getRequestURI());
        SecretContextHolder.setSecretContext(secretContext);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 在请求处理完成之后，清除ThreadLocal变量
        SecretContextHolder.remove();
    }
}
