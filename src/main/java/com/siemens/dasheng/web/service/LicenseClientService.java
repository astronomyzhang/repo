package com.siemens.dasheng.web.service;

import com.alibaba.fastjson.JSONObject;
import com.siemens.dasheng.web.client.LicenseClientSingleton;
import org.jarbframework.utils.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @description: LicenseClientService
 * @date: 6/6/2019 9:20
 * @author zhangliming
 */
@Service
public class LicenseClientService {

    /**
     * @Description getLicenseScale
     * @author zhangliming
     * @return sensorScale
     */
    public int getLicenseScale() {
        LicenseClientSingleton instance = LicenseClientSingleton.getInstance();
        JSONObject jsonObject = instance.licenseDetails();
        JSONObject platformObject = jsonObject.getJSONObject("Platform");

        int sensorScale = 0;
        if (platformObject != null) {
            String sensorScaleStr = (String) platformObject.get("sensorScale");
            if (StringUtils.isNotBlank(sensorScaleStr)) {
                sensorScale = Integer.parseInt(sensorScaleStr);
            }
        }
        return  sensorScale;
    }

}
