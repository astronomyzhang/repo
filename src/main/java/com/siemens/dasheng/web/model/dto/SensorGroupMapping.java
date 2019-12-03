package com.siemens.dasheng.web.model.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author alaln
 * Created by z0041dpv on 4/18/2019.
 */
public class SensorGroupMapping {
    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("机组名称")
    private String crewName;

    @ApiModelProperty("测点组名称")
    private String groupName;

    @ApiModelProperty("描述")
    private String description;

    /**
     * // 测点组下的测点数目
     */
    private Integer quantity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCrewName() {
        return crewName;
    }

    public void setCrewName(String crewName) {
        this.crewName = crewName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
