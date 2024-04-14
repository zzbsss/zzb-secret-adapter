package org.zzb.secret.handler.common.decrypt;

import org.zzb.secret.algorithm.AlgorithmType;
import org.zzb.secret.config.SecureConfig;
import org.zzb.secret.factory.AlgorithmFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.stream.Collectors;
/**
 * @author zzb
 * @version 1.0
 * @description:
 * @date 2024年4月14日11:14:52
 */
public class DecryptHttpInputMessage implements HttpInputMessage {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private HttpHeaders headers;
    private InputStream body;


    public DecryptHttpInputMessage(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                   Class<? extends HttpMessageConverter<?>> converterType, SecureConfig secureConfig) throws Exception {
        // 获取到当前配置的解密算法
        AlgorithmType algorithmType = AlgorithmFactory.algorithmFactory.get();
        this.headers = inputMessage.getHeaders();
        String content = new BufferedReader(new InputStreamReader(inputMessage.getBody()))
                .lines().collect(Collectors.joining(System.lineSeparator()));
        String decryptBody = algorithmType.decrypt(content);
        this.body = new ByteArrayInputStream(decryptBody.getBytes(secureConfig.getCharset()));
    }

    @Override
    public InputStream getBody(){
        return body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }


}
