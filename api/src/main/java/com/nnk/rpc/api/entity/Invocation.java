package com.nnk.rpc.api.entity;

import java.io.Serializable;

public class Invocation implements Serializable {

    private String interfaceName;
    private String methodName;
    private Class[] paramtypes;
    private Object[] objects;

    public Invocation(String interfaceName, String methodName, Class[] paramtypes, Object[] objects) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.paramtypes = paramtypes;
        this.objects = objects;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class[] getParamtypes() {
        return paramtypes;
    }

    public void setParamtypes(Class[] paramtypes) {
        this.paramtypes = paramtypes;
    }

    public Object[] getObjects() {
        return objects;
    }

    public void setObjects(Object[] objects) {
        this.objects = objects;
    }
}
