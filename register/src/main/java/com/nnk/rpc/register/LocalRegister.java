package com.nnk.rpc.register;

public interface LocalRegister {
    /**
     *
     * @param interfaceName 接口名称
     * @param interfaceImplClass 接口实现类
     */
    void register(String interfaceName,Class interfaceImplClass);

    /**
     * 获取实现类
     * @param interfaceName
     * @return
     */
    Class get(String interfaceName);
}
