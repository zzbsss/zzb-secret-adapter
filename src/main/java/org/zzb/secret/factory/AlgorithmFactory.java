package org.zzb.secret.factory;


import org.zzb.secret.algorithm.AlgorithmType;
import org.zzb.secret.constant.SecretKeyConstant;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class AlgorithmFactory {

    public static AlgorithmFactory algorithmFactory;

    private final Map<String, AlgorithmType> algorithmTypeMap = new ConcurrentHashMap<>();

    private final String algorithmName;

    /**
     *
     * @param strategyMap
     * @param algorithmName
     */
    public AlgorithmFactory(Map<String, AlgorithmType> strategyMap, String algorithmName){
        this.algorithmTypeMap.putAll(strategyMap);
        SecretKeyConstant.Algorithm algorithm = SecretKeyConstant.Algorithm.get(algorithmName);
        // 如果当前有用户自定义的算法，则取用户自定义的
        this.algorithmName =  Objects.isNull(algorithm) ? algorithmName + SecretKeyConstant.AlgorithmName : algorithm.getBeanName();
    }

    @PostConstruct
    public void init () {
        algorithmFactory = this;
    }

    /**
     * 获取
     * @param type
     * @return
     */
    public AlgorithmType get(String type){
        AlgorithmType strategy = algorithmTypeMap.get(type);
        if (null == strategy){
            throw new RuntimeException(MessageFormat.format("不存在处理算法{0}", type));
        }
        return strategy;
    }

    /**
     * 获取当前配置的算法
     * @return
     */
    public AlgorithmType get(){
        AlgorithmType strategy = algorithmTypeMap.get(algorithmName);
        if (null == strategy){
            throw new RuntimeException(MessageFormat.format("不存在处理算法{0}", algorithmName));
        }
        return strategy;
    }
}
