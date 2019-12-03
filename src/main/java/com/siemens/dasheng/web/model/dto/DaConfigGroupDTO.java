package com.siemens.dasheng.web.model.dto;

/**
 * 配置测点组实体
 *
 * @author jin.liu.ext@siemens.com
 * @date 2019/7/18
 */
public class DaConfigGroupDTO {

    private Long id;

    private String name;

    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "DaConfigGroupDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}