package com.siemens.dasheng.web.service;

/**
 * PisdkConnectionService
 *
 * @author xuxin
 * @date 2019/1/3
 */
public class PisdkConnectionService {

    static {
        // in classpath or -Djava.library.path=/a/b/c
        System.loadLibrary("pi-connection-test.dll");
    }

    public native void foo();

}