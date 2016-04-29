package com.baidu.disconf.web.service.config.service;

import com.baidu.disconf.core.common.constants.DisConfigTypeEnum;
import com.baidu.disconf.web.service.config.bo.Config;
import com.baidu.disconf.web.service.config.bo.ConfigDraft;
import com.baidu.disconf.web.service.task.bo.Task;
import com.baidu.disconf.web.service.config.condition.ConfigDraftCondition;
import com.baidu.disconf.web.service.config.form.ConfDraftSubmitForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by luoshiqian on 2016/4/20.
 */
public interface ConfigDraftMgr {


    ConfigDraft getConfByParameter(Long appId, Long envId, String version, String key,
                                   DisConfigTypeEnum disConfigTypeEnum);


    Page<ConfigDraft> findByConfigDraft(ConfigDraftCondition configDraft, Pageable pageable);


    ConfigDraft findById(Long id);

    void updateItemValue(Long id,String value);

    void delete(Long id);

    //提交草稿
    Task submit(ConfDraftSubmitForm confDraftSubmitForm);

    List<ConfigDraft> findByTaskId(Long taskId);


    List<ConfigDraft> getTobeActiveConfigDraft(Task task);

    void draftToConfig(Task task);
}
