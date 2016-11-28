package com.baidu.disconf.web.service.config.form;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * 新建配置文件表格
 *
 * @author luoshiqian
 * @version 2016-3-28
 */
public class ConfCopyForm {

    @NotNull(message = "app.empty")
    private Long appId;
    public static final String APPID = "appId";

    @NotNull(message = "version.empty")
    @NotEmpty(message = "version.empty")
    private String version;
    public static final String VERSION = "version";

    @NotNull(message = "env.empty")
    private Long envId;
    public static final String ENVID = "envId";

    @NotNull(message = "env.empty")
    private Long newEnvId;
    @NotNull(message = "version.empty")
    @NotEmpty(message = "version.empty")
    private String newVersion;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public Long getNewEnvId() {
        return newEnvId;
    }

    public void setNewEnvId(Long newEnvId) {
        this.newEnvId = newEnvId;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    @Override
    public String toString() {
        return "ConfCopyForm [appId=" + appId + ", version=" + version + ", envId=" + envId + "]";
    }

    public ConfCopyForm(Long appId, String version, Long envId) {
        super();
        this.appId = appId;
        this.version = version;
        this.envId = envId;
    }

    public ConfCopyForm() {
        super();
    }

}
