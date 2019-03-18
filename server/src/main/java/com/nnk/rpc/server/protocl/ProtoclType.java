package com.nnk.rpc.server.protocl;

public enum ProtoclType {
    HTTP("http",1),NETTY("netty",2);
    private String protoclName;
    private int proctolNum;

    ProtoclType(String protoclName, int proctolNum) {
        this.protoclName = protoclName;
        this.proctolNum = proctolNum;
    }

    public String getProtoclName() {
        return protoclName;
    }

    public void setProtoclName(String protoclName) {
        this.protoclName = protoclName;
    }

    public int getProctolNum() {
        return proctolNum;
    }

    public void setProctolNum(int proctolNum) {
        this.proctolNum = proctolNum;
    }
}
