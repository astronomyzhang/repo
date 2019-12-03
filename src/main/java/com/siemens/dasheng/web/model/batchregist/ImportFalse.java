package com.siemens.dasheng.web.model.batchregist;

import java.util.List;

/**
 * @author zhangliming
 * @Date: 2019/10/12 17:10
 */
public class ImportFalse {

    private String tag;

    private String siecode;

    public ImportFalse() {
        super();
    };

    public ImportFalse(String tag, String siecode) {
        this.tag = tag;
        this.siecode = siecode;
    }

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

}
