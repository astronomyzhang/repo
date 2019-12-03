package com.siemens.dasheng.web.service;

import com.siemens.dasheng.web.BaseTest;
import com.siemens.dasheng.web.mapper.DaConfigApplicationMapper;
import com.siemens.dasheng.web.model.DaConfigApplication;
import com.siemens.dasheng.web.response.AplicationName;
import com.siemens.dasheng.web.response.AppDetail;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author liming
 * @Date: 2019/4/10 10:21
 */
public class ApplicationViewServiceTest extends BaseTest {

    @Autowired
    ApplicationViewService applicationViewService;

    @Autowired
    DaConfigApplicationMapper daConfigApplicationMapper;


    @Test
    public void selectAppList() {
        List<AplicationName> aplicationNames = applicationViewService.selectAppList();
        Assert.assertNotNull(aplicationNames);
    }

    @Test
    public void selectAppDetail() {
        List<DaConfigApplication> daConfigApplications = daConfigApplicationMapper.selectAppList();
        if (null != daConfigApplications) {
            for (DaConfigApplication daConfigApplication : daConfigApplications) {

                AppDetail appDetail = applicationViewService.selectAppDetail(daConfigApplication.getId());
            }
        }

    }
}