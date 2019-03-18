package com.nnk.rpc.register.factory;


import com.nnk.rpc.register.LocalRegister;
import com.nnk.rpc.register.RegisterType;
import com.nnk.rpc.register.local.LocalMapRegister;

public class LocalRegisterFactory {

    private static LocalMapRegister localMapRegister = new LocalMapRegister();
    public static LocalRegister getLocalRegister(RegisterType registerType){
        switch (registerType){
            case LOCAL: return localMapRegister;
            default:return null;
        }
    }
}
