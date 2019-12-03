package com.siemens.dasheng.web.request;

import com.siemens.dasheng.web.model.DaConfigConnectorSensorPlus;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/3/8
 */
public class ValidateSieCodeIsImportedRequest {
    @NotNull(message = DA_CONFIG_NOTBLANK_APPLICATION_ID)
    private Long applicationId;

    @NotNull(message = DA_CONFIG_NOTBLANK_CONNECTOR_SENSOR_LIST)
    private List<DaConfigConnectorSensorPlus> conSensorList;



    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public List<DaConfigConnectorSensorPlus> getConSensorList() {
        return conSensorList;
    }

    public void setConSensorList(List<DaConfigConnectorSensorPlus> conSensorList) {
        this.conSensorList = conSensorList;
    }
}
