package org.zzb.secret.handler.zuul.encrypt;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.zzb.secret.algorithm.AlgorithmType;
import org.zzb.secret.config.SecureConfig;
import org.zzb.secret.constant.SecretKeyConstant;
import org.zzb.secret.factory.AlgorithmFactory;

public class EncryptResponseFilter extends ZuulFilter {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String APP_JSON = "application/json";

    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptResponseFilter.class );

    private static final String REFERER_HEADER = "referer";


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
        if (Objects.isNull(secureConfig) || !secureConfig.isEnable()) {
            return false;
        }
        // 不是通用实现 返回false
        if (secureConfig.getType() != SecretKeyConstant.Type.zuul) {
            return false;
        }
        // 配置了全局打开
        if (secureConfig.getModel() == SecretKeyConstant.Model.all) {
            return true;
        }
        // 配置了单方向开启 出方向加密
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
