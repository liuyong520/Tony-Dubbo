package com.nnk.rpc.register;

import com.nnk.rpc.api.entity.URL;

public interface RemoteRegister {
    /**
     * 注册到远程注册中心
     * @param interfaceName
     * @param host
     */
    void register(String interfaceName, URL host);

    /**
     * 根据服务名称获取调用者的地址信息
     * @param interfaceName
     * @return
     */
    URL getRadomURL(String interfaceName);
}
