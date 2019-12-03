package com.siemens.dasheng.web.model;

/**
 * @author allan
 *  DaConfigAppExtension
 * Created by allan on 2019/4/03.
 */
public class DaConfigAppExtension {
    private Long id;

    private Long appId;

    private Long extensionAppId;

    public DaConfigAppExtension(Long id, Long appId, Long extensionAppId) {
        this.id = id;
        this.appId = appId;
        this.extensionAppId = extensionAppId;
    }

    public DaConfigAppExtension() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getExtensionAppId() {
        return extensionAppId;
    }

    public void setExtensionAppId(Long extensionAppId) {
        this.extensionAppId = extensionAppId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}

        DaConfigAppExtension that = (DaConfigAppExtension) o;

        if (!id.equals(that.id)) {return false;}
        if (!appId.equals(that.appId)) {return false;}
        return extensionAppId.equals(that.extensionAppId);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + appId.hashCode();
        result = 31 * result + extensionAppId.hashCode();
        return result;
    }
}