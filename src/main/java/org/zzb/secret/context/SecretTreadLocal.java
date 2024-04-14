package org.zzb.secret.context;


/**
 * @description: 预留ThreadLocal
 * todo
 */
public enum SecretTreadLocal {

    INSTANCE;

    private final ThreadLocal<Boolean>  secretContext = new ThreadLocal<>();

    public Boolean getFlag() {
        return secretContext.get();
    }

    public void setFlag(Boolean flag) {
        this.secretContext.set(flag);
    }

    public void remove() {
        secretContext.remove();
    }

}
