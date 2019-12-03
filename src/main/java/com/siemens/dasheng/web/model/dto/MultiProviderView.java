package com.siemens.dasheng.web.model.dto;

import java.util.List;

/**
 * @author allan
 * Created by z0041dpv on 5/15/2019.
 */
public class MultiProviderView {

    private Boolean status;

    List<MultiConnecotrInfoList>  multiConnecotrInfoLists;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<MultiConnecotrInfoList> getMultiConnecotrInfoLists() {
        return multiConnecotrInfoLists;
    }

    public void setMultiConnecotrInfoLists(List<MultiConnecotrInfoList> multiConnecotrInfoLists) {
        this.multiConnecotrInfoLists = multiConnecotrInfoLists;
    }
}
