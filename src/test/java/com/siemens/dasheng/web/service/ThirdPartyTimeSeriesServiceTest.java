package com.siemens.dasheng.web.service;

import com.siemens.dasheng.web.BaseTest;
import com.siemens.dasheng.web.conf.ThirdpartyServiceLoader;
import com.siemens.dasheng.web.thirdparty.TestConnect;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhangliming
 * Created by zhangliming on 2019/10/28.
 */
public class ThirdPartyTimeSeriesServiceTest extends BaseTest {

    @Test
    public void testConnect() throws Exception {
        ThirdpartyServiceLoader.init3rdpartyService();
        Class connectClass = ThirdpartyServiceLoader.getSpecifiedClass(TestConnect.class);
        try {
            TestConnect instance = (TestConnect)connectClass.newInstance();
            //调用业务
            System.out.println(instance.testConnect());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
