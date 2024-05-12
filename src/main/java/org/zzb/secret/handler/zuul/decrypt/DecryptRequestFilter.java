package org.zzb.secret.handler.zuul.decrypt;

import com.alibaba.fastjson2.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.zzb.secret.algorithm.AlgorithmType;
import org.zzb.secret.config.SecureConfig;
import org.zzb.secret.factory.AlgorithmFactory;
import org.zzb.secret.util.RequestSupport;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.springframework.http.HttpMethod.*;


/**
 *
 */
public class DecryptRequestFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger(DecryptRequestFilter.class);

    private SecureConfig secureConfig;

    public DecryptRequestFilter(SecureConfig secureConfig) {
        this.secureConfig = secureConfig;
    }

    /**
     * pre：路由之前
     * routing：路由之时
     * post： 路由之后
     * error：发送错误调用
     *
     * @return
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * filterOrder：过滤的顺序
     *
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * shouldFilter：
     *
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return RequestSupport.checkZuulRequestParam(secureConfig);
    }

    /**
     * run：过滤器的具体逻辑。
     * 要把请求参数进行验签（解密）之后传给后续的微服务，首先获取到request，但是在request中只有getParameter()而没有setParameter()方法
     * 所以直接修改url参数不可行，另外在reqeust中虽然可以使用setAttribute(),但是可能由于作用域（request）的不同，一台服务器中才能getAttribute
     * 在这里设置的attribute在后续的微服务中是获取不到的，因此必须考虑另外的方式：即获取请求的输入流，并重写，即重写json参数，
     * ctx.setRequest(new HttpServletRequestWrapper(request) {})，这种方式可重新构造上下文中的request
     *
     * @return
     */
    @Override
    public Object run() {
        // 获取到request
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        try {
            // 请求方法
            String method = request.getMethod();
            log.debug(String.format("zuul accept request %s >>> %s", method, request.getRequestURL().toString()));
            // 获取请求的输入流
            InputStream in = request.getInputStream();
            String body = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
            // 如果body为空初始化为空json
            if ( StringUtils.isEmpty(body)) {
                body = "{}";
            }
            log.debug("zuul request body" + body);
            // get方法
            if (GET.name().equals(method)) {
                extractRequestParam(ctx, request);
            }
            if(POST.name().equals(method) || PUT.name().equals(method) || DELETE.name().equals(method)) {
                extractRequestParam(ctx, request);
                AlgorithmType algorithmType = AlgorithmFactory.algorithmFactory.get();
                String decodedStr;
                Map<String, String> requestParamMap = SecureConfig.getRequestParamMap();
                // 配置了指定参数解密，后续可以考虑统配符号 例如 *Param 以Param的都要解密
                if (requestParamMap.size() > 0) {
                    JSONObject from = JSONObject.parseObject(body);
                    from.forEach((k,v) -> {
                        if (Objects.nonNull(requestParamMap.get(k)) && Objects.nonNull(v)) {
                            String decrypted = algorithmType.decrypt(String.valueOf(v));
                            from.put(k,decrypted);
                        }
                    });
                    decodedStr = from.toJSONString();
                }else {
                    decodedStr = algorithmType.decrypt(body);
                }
                final byte[] reqBodyBytes = decodedStr.getBytes(StandardCharsets.UTF_8);
                // 重写上下文的HttpServletRequestWrapper
                ctx.setRequest(new HttpServletRequestWrapper(request) {
                    @Override
                    public ServletInputStream getInputStream() {
                        return new ServletInputStreamWrapper(reqBodyBytes);
                    }

                    @Override
                    public int getContentLength() {
                        return reqBodyBytes.length;
                    }

                    @Override
                    public long getContentLengthLong() {
                        return reqBodyBytes.length;
                    }
                });
            }
        } catch (IOException e) {
            log.debug("zuul decrypt failed", e);
        }
        return null;
    }

    private static void extractRequestParam(RequestContext ctx, HttpServletRequest request) {
        AlgorithmType algorithmType = AlgorithmFactory.algorithmFactory.get();
        // 获取请求参数
        Enumeration<String> parameterNames = request.getParameterNames();
        Map<String, List<String>> requestQueryParams = ctx.getRequestQueryParams();
        if (requestQueryParams == null) {
            requestQueryParams = new HashMap<>();
        }
        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String parameter = request.getParameter(key);
            if (parameter == null || StringUtils.isEmpty(parameter)) {
               continue;
            }
            // 关键步骤，一定要get一下,下面才能取到值requestQueryParams
            request.getParameterMap();
            List<String> arrayList = new ArrayList<>();
            Map<String, String> requestParamMap = SecureConfig.getRequestParamMap();
            // 配置了指定参数解密，后续可以考虑通配符号 例如 *Param 以Param的都要解密
            if (requestParamMap.size() > 0 && Objects.nonNull(requestParamMap.get(key))) {
                String decodedStr = algorithmType.decrypt(parameter);
                arrayList.add(decodedStr);
                requestQueryParams.put(key, arrayList);
            }else {
                arrayList.add(parameter);
                requestQueryParams.put(key, arrayList);
            }
        }
        ctx.setRequestQueryParams(requestQueryParams);
    }
}
