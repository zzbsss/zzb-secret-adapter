package org.zzb.secret.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.zzb.secret.context.SecretTreadLocal;
import org.springframework.web.servlet.HandlerInterceptor;


public class SecretInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 在请求处理之前可以设置ThreadLocal变量
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 在请求处理完成之后，清除ThreadLocal变量
        SecretTreadLocal.INSTANCE.remove();
    }
}
