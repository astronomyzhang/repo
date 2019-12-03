package com.siemens.dasheng.web.response;

import com.siemens.dasheng.web.model.DaConfigProvider;

import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/3/8
 */
public class ModifyProviderResponse {
    private Long status;

    private Long providerId;

    private String msg;

    private Map errorMap;

    private List<List<String>> lists;

    private List<String> names;

    private String repeatNames;

    private Integer nums;

    private List<RemoveProviderConnectorResponse> responsesList;

    public List<RemoveProviderConnectorResponse> getResponsesList() {
        return responsesList;
    }

    public void setResponsesList(List<RemoveProviderConnectorResponse> responsesList) {
        this.responsesList = responsesList;
    }

    public Integer getNums() {
        return nums;
    }

    public void setNums(Integer nums) {
        this.nums = nums;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public String getRepeatNames() {
        return repeatNames;
    }

    public void setRepeatNames(String repeatNames) {
        this.repeatNames = repeatNames;
    }

    public List<List<String>> getLists() {
        return lists;
    }

    public void setLists(List<List<String>> lists) {
        this.lists = lists;
    }

    public Map getErrorMap() {
        return errorMap;
    }

    public void setErrorMap(Map errorMap) {
        this.errorMap = errorMap;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }
}
