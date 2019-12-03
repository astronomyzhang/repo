package com.siemens.dasheng.web.model.batchregist;


import java.util.List;

/**
 * @author zhangliming
 * @Date: 2019/10/12 17:10
 */
public class RegistData {

    private String tag;

    private String siecode;

    private Boolean createTag;

    private String description;

    private Boolean validate;

    private String prefix;

    private List<ErrorInfo> errorInfos;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSiecode() {
        return siecode;
    }

    public void setSiecode(String siecode) {
        this.siecode = siecode;
    }

    public Boolean getCreateTag() {
        return createTag;
    }

    public void setCreateTag(Boolean createTag) {
        this.createTag = createTag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getValidate() {
        return validate;
    }

    public void setValidate(Boolean validate) {
        this.validate = validate;
    }

    public List<ErrorInfo> getErrorInfos() {
        return errorInfos;
    }

    public void setErrorInfos(List<ErrorInfo> errorInfos) {
        this.errorInfos = errorInfos;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}


