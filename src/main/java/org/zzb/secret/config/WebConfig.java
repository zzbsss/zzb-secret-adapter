package org.zzb.secret.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zzb.secret.handler.common.decrypt.DecryptPathVariableResolver;
import org.zzb.secret.handler.common.decrypt.DecryptRequestParamResolver;
import org.zzb.secret.interceptor.SecretInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer  {

    private final SecureConfig secureConfig;

    public WebConfig(SecureConfig secureConfig) {
        this.secureConfig = secureConfig;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册自定义拦截器
        registry.addInterceptor(new SecretInterceptor(secureConfig))
                .addPathPatterns("/**"); // 指定拦截所有请求，可以根据需要指定特定路径
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new DecryptRequestParamResolver(secureConfig));
        resolvers.add(new DecryptPathVariableResolver(secureConfig));
    }
}
