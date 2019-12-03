package com.siemens.dasheng.web;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import static com.siemens.dasheng.web.singleton.constant.ServiceConstant.ENV;
import static com.siemens.dasheng.web.singleton.constant.ServiceConstant.UNIT_TEST;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/7/23.
 */
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = DaCoreApplication.class)
public class BaseMockitoTest {

    @BeforeClass
    public static void myBeforeClass() {
        System.setProperty("jasypt.encryptor.password", "dpp123456!@#");
        System.setProperty("jasypt.encryptor.algorithm", "PBEWithMD5AndDES");
        System.setProperty(ENV, UNIT_TEST);
    }

}
