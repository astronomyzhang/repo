package com.siemens.dasheng.web.model.batchregist;

import java.util.List;

/**
 * @author zhangliming
 * @Date: 2019/10/12 17:10
 */
public class BatchRegistResponse {

    private String fileName;

    private List<RegistData> lists;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<RegistData> getLists() {
        return lists;
    }

    public void setLists(List<RegistData> lists) {
        this.lists = lists;
    }
}
