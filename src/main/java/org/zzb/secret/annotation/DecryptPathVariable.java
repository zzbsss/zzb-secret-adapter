package org.zzb.secret.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptPathVariable {
    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";


    boolean required() default true;

}
