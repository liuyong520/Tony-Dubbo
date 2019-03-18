package com.nnk.rpc.register;

import com.nnk.rpc.api.entity.URL;

public interface RemoteRegister {

    void register(String interfaceName, URL host);

    URL getRadomURL(String interfaceName);
}
