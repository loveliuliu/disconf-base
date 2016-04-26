package com.baidu.disconf.web.service.config.service.impl;

import com.baidu.disconf.core.common.constants.DisConfigTypeEnum;
import com.baidu.disconf.web.common.Constants;
import com.baidu.disconf.web.service.app.bo.App;
import com.baidu.disconf.web.service.app.dao.AppDao;
import com.baidu.disconf.web.service.config.bo.Config;
import com.baidu.disconf.web.service.config.bo.ConfigDraft;
import com.baidu.disconf.web.service.config.condition.ConfigDraftCondition;
import com.baidu.disconf.web.service.config.dao.ConfigDraftDao;
import com.baidu.disconf.web.service.config.dao.ConfigDraftMapper;
import com.baidu.disconf.web.service.config.form.ConfDraftSubmitForm;
import com.baidu.disconf.web.service.config.service.ConfigDraftMgr;
import com.baidu.disconf.web.service.config.service.ConfigMgr;
import com.baidu.disconf.web.service.env.bo.Env;
import com.baidu.disconf.web.service.env.dao.EnvDao;
import com.baidu.disconf.web.service.task.bo.Task;
import com.baidu.disconf.web.service.task.constant.TaskExecStatusEnum;
import com.baidu.disconf.web.service.task.dao.TaskDao;
import com.baidu.disconf.web.service.task.dao.TaskMapper;
import com.baidu.disconf.web.service.task.service.TaskMgr;
import com.baidu.disconf.web.service.user.dto.Visitor;
import com.baidu.disconf.web.utils.CodeUtils;
import com.baidu.dsp.common.constant.DataFormatConstants;
import com.baidu.dsp.common.exception.ValidationException;
import com.baidu.ub.common.commons.ThreadContext;
import com.github.knightliao.apollo.utils.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by luoshiqian on 2016/4/20.
 */
@Service
public class ConfigDraftMgrImpl implements ConfigDraftMgr{

    @Autowired
    private ConfigDraftDao configDraftDao;

    @Autowired
    private ConfigDraftMapper configDraftMapper;

    @Autowired
    private AppDao appDao;

    @Autowired
    private EnvDao envDao;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskMgr taskMgr;

    @Autowired
    private ConfigMgr configMgr;

    @Override
    public ConfigDraft getConfByParameter(Long appId, Long envId, String version, String key, DisConfigTypeEnum disConfigTypeEnum) {
        return configDraftDao.getByParameter(appId,envId,version,key,disConfigTypeEnum);
    }

    @Override
    public Page<ConfigDraft> findByConfigDraft(ConfigDraftCondition configDraft, Pageable pageable) {
        return configDraftMapper.findByConfigDraft(configDraft,pageable);
    }

    @Override
    public ConfigDraft findById(Long id) {
        return configDraftDao.get(id);
    }

    @Override
    public void updateItemValue(Long id, String value) {

        configDraftDao.updateValue(id, CodeUtils.utf8ToUnicode(value));
    }

    @Override
    public void delete(Long id) {

        configDraftDao.updateStatus(id, Constants.STATUS_DELETE);

    }

    @Override
    @Transactional
    public Long submit(ConfDraftSubmitForm form) {

        Visitor visitor = ThreadContext.getSessionVisitor();

        Long appId = form.getAppId();
        Long envId = form.getEnvId();
        String version = form.getVersion();
        Long userId = visitor.getId();

        //找到当前用户某版本下所有正常的草稿
        List<ConfigDraft> configDraftList = configDraftDao.findSubmitDraft(
                appId,envId,version,userId,Constants.STATUS_NORMAL);

        if(CollectionUtils.isEmpty(configDraftList)){
            throw new ValidationException("您没有需要提交的草稿!");
        }

        //创建待审核任务
        App app = appDao.get(appId);
        Env env = envDao.get(envId);

        Task task = new Task(appId,app.getName(),envId,env.getName(),version,userId);

        if(form.getExecNow().intValue() == 1){
            task.setExecTime(ConfigDraft.NOW);
        }else{
            String execTimeStr = DateUtils.formatDateString(form.getExecTime(),DataFormatConstants.DATE_TIME_PICKER_FORMAT,DataFormatConstants.COMMON_TIME_FORMAT);
            task.setExecTime(execTimeStr);
        }

        task.setMemo(form.getMemo());

        String curTime = DateUtils.format(new Date(), DataFormatConstants.COMMON_TIME_FORMAT);
        task.setCreateTime(curTime);

        taskDao.create(task);

        //更新所有草稿状态 以及 邦定任务id
        for (ConfigDraft configDraft : configDraftList){
            configDraft.setStatus(Constants.STATUS_SUBMITED);
            configDraft.setTaskId(task.getId());
            configDraft.setUpdateTime(curTime);
        }
        configDraftDao.update(configDraftList);

        return task.getId();
    }

    @Override
    public List<ConfigDraft> findByTaskId(Long taskId) {

        ConfigDraftCondition configDraft = new ConfigDraftCondition();
        configDraft.setTaskId(taskId);
        Page<ConfigDraft> draftPage = this.findByConfigDraft(configDraft,new PageRequest(0,Integer.MAX_VALUE));

        return draftPage.getContent();

    }

    @Override
    public List<ConfigDraft> getTobeActiveConfigDraft(Task task) {
        return  configDraftMapper.findTobeActiveConfigDraft(task);
    }

    @Transactional
    @Override
    public void draftToConfig(Task task){

        //生效task相关联的configDraft
        List<Config> configList = new ArrayList<>();
        List<ConfigDraft> configDraftList = getTobeActiveConfigDraft(task);
        if (configDraftList != null && configDraftList.size() > 0) {
            for (ConfigDraft configDraft : configDraftList) {
                configList.add(configMgr.execDraftToCofing(configDraft));
            }
        }

        //更新task的执行状态
        Task condition = new Task();
        condition.setId(task.getId());
        condition.setExecStatus(TaskExecStatusEnum.done.getValue());
        taskMgr.updateTaskExecStatus(condition);

        //同步zk
        for(Config config : configList){
            configMgr.notifyZookeeper(config.getId());
        }
    }
}
