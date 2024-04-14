package org.zzb.secret.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Encrypt
@DecryptBody
@DecryptParam
public @interface EncryptDecrypt {
}
