package com.siemens.dasheng.web;

import com.siemens.dasheng.web.client.LicenseClientSingleton;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.siemens.dasheng.web.singleton.constant.ServiceConstant.ENV;
import static com.siemens.dasheng.web.singleton.constant.ServiceConstant.UNIT_TEST;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/7/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DaCoreApplication.class)
public class BaseTest {

    @BeforeClass
    public static void myBeforeClass() {
        System.setProperty("jasypt.encryptor.password", "dpp123456!@#");
        System.setProperty("jasypt.encryptor.algorithm", "PBEWithMD5AndDES");
        System.setProperty(ENV, UNIT_TEST);
    }

}
