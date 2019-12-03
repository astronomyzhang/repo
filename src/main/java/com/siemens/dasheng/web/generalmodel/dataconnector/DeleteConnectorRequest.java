package com.siemens.dasheng.web.generalmodel.dataconnector;

/**
 * @author liming
 * @Date: 2019/1/7 14:12
 */
public class DeleteConnectorRequest {
    private Long id;

    private Boolean isConfirm;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getConfirm() {
        return isConfirm;
    }

    public void setConfirm(Boolean confirm) {
        isConfirm = confirm;
    }
}
