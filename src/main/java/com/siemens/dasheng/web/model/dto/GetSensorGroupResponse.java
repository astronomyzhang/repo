package com.siemens.dasheng.web.model.dto;

/**
 * @author allan
 * Created by z0041dpv on 4/18/2019.
 */
public class GetSensorGroupResponse {

    private Long id;

    private String groupName;

    /**
     * // 是否被当前机组所引用， true表示被当前机组引用个，false，表示没有被当前机组引用
     */
    private Boolean isChecked;

    /**
     * // 表示该测点组中的测点是否被实体（模型、规则、计算测点）所引用
     */
    private Boolean quoted;

    /**
     * // 测点组下的测点数目
     */
    private Integer quantity;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public Boolean getQuoted() {
        return quoted;
    }

    public void setQuoted(Boolean quoted) {
        this.quoted = quoted;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
