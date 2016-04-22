package com.baidu.disconf.web.service.user.bo;

import com.baidu.dsp.common.dao.Columns;
import com.baidu.dsp.common.dao.DB;
import com.baidu.unbiz.common.genericdao.annotation.Column;
import com.baidu.unbiz.common.genericdao.annotation.Table;
import com.github.knightliao.apollo.db.bo.BaseObject;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by luoshiqian on 2016/4/19.
 */
@Table(db = DB.DB_NAME, name = "user_app",keyColumn = Columns.NO_KEY)
public class UserApp extends BaseObject<Long> {

    @Column(value = "id",ignore = true)
    private Long id;

    @Column(value = "user_id")
    private Long userId;
    @Column(value = "app_id")
    private Long appId;
    @Column(value = "type")
    private String type; //关系类型（normal 普通用户,auditor 审核员 ）

    public UserApp(Long userId, Long appId, String type) {
        this.userId = userId;
        this.appId = appId;
        this.type = type;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

