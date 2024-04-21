package org.zzb.secret.handler.zuul.encrypt;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.zzb.secret.algorithm.AlgorithmType;
import org.zzb.secret.config.SecureConfig;
import org.zzb.secret.factory.AlgorithmFactory;
import org.zzb.secret.util.RequestSupport;

public class EncryptResponseFilter extends ZuulFilter {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private SecureConfig secureConfig;

    public EncryptResponseFilter(SecureConfig secureConfig) {
        this.secureConfig = secureConfig;
    }

    /**
     * 过滤器类型为后置过滤器,否则无法获取返回值
     * @return
     */
    @Override
    public String filterType() {
        return  "post";
    }

    /**
     * 过滤器顺序，越小越先执行, 如果太过于滞后，会出现responsed出现关闭异常
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 是否执行该过滤器
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return RequestSupport.checkZuulResponseParam(secureConfig);
    }

    /**
     *
     * @return
     */
    @Override
    public Object run() {
        try {
            // 获取到当前配置的解密算法
            AlgorithmType algorithmType = AlgorithmFactory.algorithmFactory.get();
            RequestContext currentContext = RequestContext.getCurrentContext();
            String respParams = getRespParams(currentContext);
            String content = JSON.toJSONString(respParams);
            String encrypt = algorithmType.encrypt(content);
            // 修复可能乱码问题
            currentContext.getResponse().setCharacterEncoding(secureConfig.getCharset());
            currentContext.setResponseBody(encrypt);
            return null;
        }catch (Exception e) {
            log.error("Encrypted data exception", e);
            throw new RuntimeException(MessageFormat.format("Encrypted data exception, message{0}",e.getMessage()));
        }
    }



    /**
     *  获取出参
     * @param currentContext
     * @return
     */
    private String getRespParams(RequestContext currentContext) {
        String body = null;
        try {
            InputStream responseDataStream = currentContext.getResponseDataStream();
            if (Objects.isNull(responseDataStream)) {
                return null;
            }
            body = StreamUtils.copyToString(responseDataStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }


}
