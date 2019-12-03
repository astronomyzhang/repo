package com.siemens.dasheng.web.request;

import com.siemens.dasheng.web.model.DaConfigConnector;
import com.siemens.dasheng.web.validators.interfaces.CharacterBytesLength;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/3/20
 */
public class ModifyProviderRequest {
    private Long id;

    @NotBlank(message = DA_CONFIG_CONNECTOR_NOTBLANK_PROVODER_NAME)
    @CharacterBytesLength(message = DATA_PROVODER_NAME_LENGTH_MAXSIZE_30,value = 30)
    private String name;

    private String connectorType;

    private String connectorClass;

    @CharacterBytesLength(message = CONNECTOR_DESCRIPTION_LENGTH_MAXSIZE_600,value = 600)
    private String description;

    private List<DaConfigConnector> connectorList;

    @NotNull(message = DA_CONFIG_CONNECTOR_NOTBLANK_PROVIDER_STATUS)
    private Long status;

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    public String getConnectorClass() {
        return connectorClass;
    }

    public void setConnectorClass(String connectorClass) {
        this.connectorClass = connectorClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DaConfigConnector> getConnectorList() {
        return connectorList;
    }

    public void setConnectorList(List<DaConfigConnector> connectorList) {
        this.connectorList = connectorList;
    }
}
