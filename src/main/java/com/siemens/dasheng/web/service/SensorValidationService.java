package com.siemens.dasheng.web.service;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.apolloconfig.ApolloAppConfig;
import com.siemens.dasheng.web.model.dto.OutputValidSensorDTO;
import com.siemens.dasheng.web.model.dto.ValidSensorDTO;
import com.siemens.dasheng.web.model.dto.ValidSensorInput;
import com.siemens.dasheng.web.response.ResponseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2019/7/3.
 */
@Service
public class SensorValidationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Resource(name = "ribbonRestTemplate")
    private RestTemplate ribbonRestTemplate;

    /**
     * sensorOnlineValidComputing(online)
     *
     * @param validSensorInput
     * @return
     */
    public OutputValidSensorDTO sensorOnlineValidComputing(ValidSensorInput validSensorInput) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<ValidSensorInput> entity = new HttpEntity<>(validSensorInput, headers);

        String response = null;
        try {
            response = ribbonRestTemplate.postForObject("http://" + ApolloAppConfig.getInstance().getDppCoreAppName() + "/commonSensorValidation/validSendor", entity, String.class);
        } catch (Exception e) {
            logger.error("sensorOnlineValidComputing error :" + e.getMessage(),e);
        }

        ResponseObject responseObject = response == null ? null : JSON.parseObject(response, ResponseObject.class);
        if (null != responseObject) {
            if (null != responseObject.getData()) {

                OutputValidSensorDTO validSensorReturn = JSON.parseObject(responseObject.getData(),
                        OutputValidSensorDTO.class);
                return validSensorReturn;

            }
        }

        return null;
    }

    /**
     * sensorOnlineValidComputing(offline)
     *
     * @param validSensorDTO
     * @return
     */
    public OutputValidSensorDTO sensorOfflineValidComputing(ValidSensorDTO validSensorDTO) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<ValidSensorDTO> entity = new HttpEntity<>(validSensorDTO, headers);

        String response = null;
        try {
            response = ribbonRestTemplate.postForObject("http://" + ApolloAppConfig.getInstance().getDppCoreAppName() + "/commonSensorValidation/validSendorService", entity, String.class);
        } catch (Exception e) {
            logger.error("sensorOfflineValidComputing error :" + e.getMessage(),e);
        }

        ResponseObject responseObject = response == null ? null : JSON.parseObject(response, ResponseObject.class);
        if (null != responseObject) {
            if (null != responseObject.getData()) {

                OutputValidSensorDTO validSensorReturn = JSON.parseObject(responseObject.getData(),
                        OutputValidSensorDTO.class);
                return validSensorReturn;

            }
        }

        return null;
    }


}
