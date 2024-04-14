package org.zzb.secret.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER,ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptParam {

    String value() default  "";
}
