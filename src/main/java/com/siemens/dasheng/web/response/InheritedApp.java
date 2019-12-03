package com.siemens.dasheng.web.response;

import com.siemens.dasheng.web.model.DaConfigProvider;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author liming
 * @Date: 2019/4/8 11:17
 */
public class InheritedApp {

    private Long key;

    private String name;

    private String description;

    private Long id;

    private List<DaConfigProvider> providers;

    private Long availability;

    private String availabilityRate;

    /**
     * 继承的公用app是否可以移除
     */
    private Boolean selected;

    @ApiModelProperty("标识（0内部应用（不可新增），1普通应用，2远程桌面，3后台服务应用）")
    private Integer flag;

    public Long getAvailability() {
        return availability;
    }

    public void setAvailability(Long availability) {
        this.availability = availability;
    }

    public String getAvailabilityRate() {
        return availabilityRate;
    }

    public void setAvailabilityRate(String availabilityRate) {
        this.availabilityRate = availabilityRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DaConfigProvider> getProviders() {
        return providers;
    }

    public void setProviders(List<DaConfigProvider> providers) {
        this.providers = providers;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }
}
