package com.siemens.dasheng.web.utils;

import org.apache.commons.net.telnet.TelnetClient;
import org.junit.Test;

import java.io.IOException;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/11/28.
 */
public class TelnetClientTest  {

    @Test
    public void test(){
        long bgTime = System.currentTimeMillis();
        TelnetClient telnetClient = new TelnetClient("vt200");
        telnetClient.setDefaultTimeout(3000);
        telnetClient.setConnectTimeout(3000);
        try {
            telnetClient.connect("11.192.30.135",5462);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Use time:"+(endTime-bgTime)+"ms");
    }


}
