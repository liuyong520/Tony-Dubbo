package com.nnk.rpc.api.entity;

import java.io.Serializable;

public class URL implements Serializable {
    private String host;
    private Integer port;
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public URL(String host, int port) {
        this.host = host;
        this.port = port;
    }
}
