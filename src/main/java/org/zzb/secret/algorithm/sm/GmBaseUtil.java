package org.zzb.secret.algorithm.sm;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
/**
 * @author zzb
 * @version 1.0
 * @description:
 * @date 2024年4月14日11:15:13
 */
public class GmBaseUtil {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
}
