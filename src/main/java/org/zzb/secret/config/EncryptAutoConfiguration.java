package org.zzb.secret.config;


import org.zzb.secret.algorithm.AlgorithmType;
import org.zzb.secret.algorithm.aes.AesAlgorithm;
import org.zzb.secret.algorithm.ras.RsaAlgorithm;
import org.zzb.secret.algorithm.sm.sm2.Sm2Algorithm;
import org.zzb.secret.handler.common.decrypt.DecryptRequestBodyAdvice;
import org.zzb.secret.handler.common.encrypt.EncryptResponseBodyAdvice;
import org.zzb.secret.factory.AlgorithmFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Map;

/**
 * @author zzb
 * @version 1.0
 * @description:
 * @date 2024年4月14日11:15:13
 */
@Import({EncryptResponseBodyAdvice.class, DecryptRequestBodyAdvice.class, WebConfig.class})
public class EncryptAutoConfiguration {


    /**
     * 算法工厂
     * @param algorithmTypeMap
     * @return
     */
    @Bean
    public AlgorithmFactory algorithmFactory(Map<String, AlgorithmType> algorithmTypeMap, SecureConfig secureConfig) {
        return new AlgorithmFactory(algorithmTypeMap,secureConfig.getAlgorithm().getAlgorithmName());
    }

    /**
     * AesAlgorithm
     * @param secureConfig
     * @return
     */
    @Bean()
    public AesAlgorithm aesAlgorithm(SecureConfig secureConfig) {
        return new AesAlgorithm(secureConfig);
    }

    /**
     * Sm2Algorithm
     * @param secureConfig
     * @return
     */
    @Bean
    public Sm2Algorithm sm2Algorithm(SecureConfig secureConfig) {
        return new Sm2Algorithm(secureConfig);
    }

    /**
     * RsaAlgorithm
     * @param secureConfig
     * @return
     */
    @Bean()
    public RsaAlgorithm rsaAlgorithm(SecureConfig secureConfig) {
        return new RsaAlgorithm(secureConfig);
    }

    /**
     * 配置
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "zzb.secure")
    public SecureConfig secureConfig() {
        return new SecureConfig();
    }


}
