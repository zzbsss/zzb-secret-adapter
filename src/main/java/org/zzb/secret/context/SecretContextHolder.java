package org.zzb.secret.context;


/**
 * @description: 加解密 ThreadLocal
 *
 */
public class SecretContextHolder {

    private final static ThreadLocal<SecretContext>  secretContextHolder = new ThreadLocal<SecretContext>(){
        @Override
        protected SecretContext initialValue() {
            return new SecretContext();
        }

        @Override
        public String toString() {
            return "Secret-Context";
        }
    };

    public static SecretContext getSecretContext() {
        return secretContextHolder.get();
    }

    public static void setSecretContext(SecretContext secretContext) {
        secretContextHolder.set(secretContext);
    }

    public static void remove() {
        secretContextHolder.remove();
    }

}
