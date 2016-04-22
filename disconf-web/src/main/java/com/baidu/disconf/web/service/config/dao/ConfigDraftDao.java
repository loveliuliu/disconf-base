package com.baidu.disconf.web.service.config.dao;

import com.baidu.disconf.core.common.constants.DisConfigTypeEnum;
import com.baidu.disconf.web.service.config.bo.ConfigDraft;
import com.baidu.unbiz.common.genericdao.dao.BaseDao;

import java.util.List;

/**
 * @author liaoqiqi
 * @version 2014-6-16
 */
public interface ConfigDraftDao extends BaseDao<Long, ConfigDraft> {


    ConfigDraft getByParameter(Long appId, Long envId, String version, String key,
                                      DisConfigTypeEnum disConfigTypeEnum);

    /**
     * 查找需要提交的草稿
     * @param appId
     * @param envId
     * @param version
     * @param userId
     * @return
     */
    List<ConfigDraft> findSubmitDraft(Long appId, Long envId, String version, Long userId,Integer status);


    void updateValue(Long id, String value);

    void updateStatus(Long id,Integer status);
}
