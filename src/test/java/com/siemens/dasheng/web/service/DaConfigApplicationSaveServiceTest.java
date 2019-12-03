package com.siemens.dasheng.web.service;

import com.siemens.dasheng.web.BaseTest;
import com.siemens.dasheng.web.model.dto.AppInfo;
import com.siemens.dasheng.web.model.dto.AppPublicInfo;
import com.siemens.dasheng.web.request.AppFilter;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**@author allan
 * Created by ofm on 2019/4/9.
 */
public class DaConfigApplicationSaveServiceTest extends BaseTest {

    @Autowired
    private DaConfigApplicationSaveService daConfigApplicationSaveService;


    @Test
    public void getAppInfoTest() {
        AppInfo appInfo = daConfigApplicationSaveService.getAppInfo(5151L);
        Assert.assertEquals("5151", appInfo.getId() + "");

        AppInfo appInfo1 = daConfigApplicationSaveService.getAppInfo(-1L);
        Assert.assertNull(appInfo1);
    }

    @Test
    public void selectAppListTest() {
        AppFilter appFilter = new AppFilter();
        appFilter.setPage("0");
        appFilter.setPageSize("500");
        List<AppInfo> appInfoList = daConfigApplicationSaveService.selectAppList(appFilter, false);
        Assert.assertNotNull(appInfoList);

    }

    @Test
    public void selectPublicAppListTest() {
        List<AppPublicInfo> appInfoList = daConfigApplicationSaveService.selectPublicAppList(null);
        Assert.assertNotNull(appInfoList);
    }

    @Test
    public void exceptMultiConnectorTest() {
        List<Map<Long, List<String>>> providerConnectorMapList = new ArrayList<>();
        List<String> r1 = new ArrayList<>();
        r1.add("2799");
        r1.add("2800");

        Map<Long, List<String>> m1 = new HashMap<>();
        m1.put(2799L, r1);

        List<String> r2 = new ArrayList<>();
        r2.add("2799");
        r2.add("2801");

        Map<Long, List<String>> m2 = new HashMap<>();
        m2.put(2800L, r2);

        List<String> r3 = new ArrayList<>();
        r3.add("2811");
        r3.add("2808");

        Map<Long, List<String>> m3 = new HashMap<>();
        m3.put(3L, r3);


        providerConnectorMapList.add(m1);
        providerConnectorMapList.add(m2);
        providerConnectorMapList.add(m3);

        System.out.println(daConfigApplicationSaveService.exceptMultiConnector(providerConnectorMapList));


    }

    @Test
    public void vefifyAppProviderTest() {
        ModelMap modelMap = new ModelMap();
        List<Long> deleteProviderIdList = new ArrayList<>();
        deleteProviderIdList.add(2966L);
        deleteProviderIdList.add(2967L);
        Long appId = 1146L;
        modelMap = daConfigApplicationSaveService.vefifyAppProvider(modelMap, deleteProviderIdList, appId);
        System.out.println(modelMap);
    }
    @Test
    public void vefifyPublicAppTest() {
        ModelMap modelMap = new ModelMap();
        List<Long> deleteProviderIdList = new ArrayList<>();
        deleteProviderIdList.add(1326L);
        Long appId = 1146L;
        modelMap = daConfigApplicationSaveService.vefifyPublicApp(modelMap, deleteProviderIdList, appId);
        System.out.println(modelMap);
    }

}
