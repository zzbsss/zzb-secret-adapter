package org.zzb.secret.constant;

import java.util.Arrays;
import java.util.Iterator;
/**
 * @author zzb
 * @version 1.0
 * @description:
 * @date 2024年4月14日11:15:13
 */
public class SecretKeyConstant {
    // SM2 加解密
    public final static String SM2 = "sm2";
    // RAS 加解密
    public final static String RAS = "ras";
    // AES 加解密
    public final static String AES = "aes";

    public final static String AlgorithmName = "Algorithm";

    public enum Algorithm {
        SM2Algorithm(SM2, SM2 + AlgorithmName ,"SM2"),
        RASAlgorithm(RAS, RAS + AlgorithmName ,"RAS"),
        AESAlgorithm(AES, AES + AlgorithmName ,"AES"),
        ;
        private final String name;
        private final String beanName;
        private final String desc;

        Algorithm(String name, String beanName, String desc) {
            this.name = name;
            this.beanName = beanName;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public String getBeanName() {
            return beanName;
        }

        public String getDesc() {
            return desc;
        }

        public static Algorithm get(String name){
            Iterator<Algorithm> iterator = Arrays.stream(Algorithm.values()).iterator();
            while (iterator.hasNext()){
                Algorithm next = iterator.next();
                if(next.name.equals(name)){
                    return next ;
                }
            }
            return null;
        }

    }

    /**
     * 加解密模式
     */
    public enum Model {
        all( "all" ,"全局开启"),

        single ( "single " ,"单方向开启"),
        support( "support" ,"指定请求开启(使用注解或者配置不需要加解密url)"),
        ;
        private final String name;
        private final String desc;

        Model(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     *
     */
    public enum Direction {
        all("all", "入参解密，出参加密"),
        request( "request" ,"入方向开启解密"),
        response( "response" ,"出方向开启加密"),
        ;
        private final String name;
        private final String desc;

        Direction(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 实现方式
     */
    public enum Type {
        comm("comm", "传统方式"),
        zuul( "zuul" ,"zuul"),
        gateway( "gateway" ,"gateway"),
        custom( "custom" ,"自定义方式"),
        ;
        private final String name;
        private final String desc;

        Type(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }
    }
}
