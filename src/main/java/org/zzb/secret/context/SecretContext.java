package org.zzb.secret.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SecretContext implements Serializable {

    private Map<String, Object> requestHeader = new HashMap<>();

    private String requestUrl;


    public Map<String, Object> getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(Map<String, Object> requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }
}
