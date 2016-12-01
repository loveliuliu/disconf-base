package com.baidu.disconf.web.web.config.controller;

import com.baidu.disconf.web.common.Constants;
import com.baidu.disconf.web.config.ApplicationPropertyConfig;
import com.baidu.disconf.web.service.config.bo.Config;
import com.baidu.disconf.web.service.config.bo.ConfigDraft;
import com.baidu.disconf.web.service.config.condition.ConfigDraftCondition;
import com.baidu.disconf.web.service.config.form.ConfDraftSubmitForm;
import com.baidu.disconf.web.service.config.service.ConfigDraftMgr;
import com.baidu.disconf.web.service.config.service.ConfigMgr;
import com.baidu.disconf.web.service.task.bo.Task;
import com.baidu.disconf.web.service.task.service.TaskMgr;
import com.baidu.disconf.web.service.user.bo.User;
import com.baidu.disconf.web.service.user.constant.UserAppTypeEnum;
import com.baidu.disconf.web.service.user.dto.Visitor;
import com.baidu.disconf.web.service.user.service.UserMgr;
import com.baidu.disconf.web.web.config.validator.FileUploadValidator;
import com.baidu.dsp.common.constant.DataFormatConstants;
import com.baidu.dsp.common.constant.ErrorCode;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.exception.FileUploadException;
import com.baidu.dsp.common.exception.ValidationException;
import com.baidu.dsp.common.utils.email.LogMailBean;
import com.baidu.dsp.common.vo.JsonObjectBase;
import com.baidu.ub.common.commons.ThreadContext;
import com.github.knightliao.apollo.utils.time.DateUtils;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping(WebConstants.API_PREFIX + "/web/configDraft")
public class ConfigDraftController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(ConfigDraftController.class);

    @Autowired
    private ConfigMgr configMgr;

    @Autowired
    private ConfigDraftMgr configDraftMgr;

    @Autowired
    private FileUploadValidator fileUploadValidator;

    @Autowired
    private TaskMgr taskMgr;

    @Autowired
    private UserMgr userMgr;

    @Autowired
    private ApplicationPropertyConfig applicationPropertyConfig;

    @Autowired
    private LogMailBean logMailBean;
    @Autowired
    private TaskExecutor proExecutor;

    @RequestMapping(value = "/list")
    @ResponseBody
    public JsonObjectBase list(ConfigDraftCondition configDraft, Pageable pageable,String draftTypeStr) {

        Visitor visitor = ThreadContext.getSessionVisitor();
        configDraft.setUserId(visitor.getId());
        configDraft.setStatus(Constants.STATUS_NORMAL);

        if(StringUtils.isNotBlank(draftTypeStr)){
            configDraft.getDraftTypeList().addAll(Arrays.asList(draftTypeStr.split("[,]")));
        }
        Page<ConfigDraft> page = configDraftMgr.findByConfigDraft(configDraft,pageable);

        return buildSuccess(page);
    }


    @RequestMapping(value = "/findById")
    @ResponseBody
    public JsonObjectBase findById(Long id) {

        ConfigDraft configDraft = configDraftMgr.findById(id);
        Config config = configMgr.getConfigById(configDraft.getConfigId());

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("configDraft",configDraft);
        map.put("config",config);

        return buildSuccess(map);
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public JsonObjectBase delete(Long id) {

        configDraftMgr.delete(id);

        return buildSuccess("删除草稿成功!");
    }


    @RequestMapping(value = "/submit")
    @ResponseBody
    public JsonObjectBase submit(@Valid ConfDraftSubmitForm confDraftSubmitForm) {

        if(StringUtils.isNotBlank(confDraftSubmitForm.getExecTime()) &&
                !DateUtils.isValid(confDraftSubmitForm.getExecTime(), DataFormatConstants.DATE_TIME_PICKER_FORMAT)){
            return buildGlobalError("生效时间格式错误 正确格式为yyyy-MM-dd HH:mm", ErrorCode.DEFAULT_ERROR);
        }

        //验证一个app 环境 版本 只能有一个正在审核或审核通过未执行的任务
        this.validateDraftSubmit(confDraftSubmitForm);

        Task task = configDraftMgr.submit(confDraftSubmitForm);

        //UAT环境，直接系统自动审核通过
        if(!Constants.IS_STG_PRD){
            taskMgr.systemAutoAuditPass(task.getId());
        }
        //发送审核mail
        String emailToList = userMgr.getMailToList(confDraftSubmitForm.getAppId(), UserAppTypeEnum.auditor.name());
        String url = applicationPropertyConfig.getDomain() + "/task_config_audit.html?id="
                + task.getId() + "&jump=1";
        if(applicationPropertyConfig.isEmailMonitorOn()){
            proExecutor.execute(() -> {
                StringBuilder title = new StringBuilder();
                title.append("请审核配置变更: [").append(task.getAppName()).append("][").append(task.getEnvName())
                        .append("][").append(task.getVersion()).append("]");
                StringBuilder text = new StringBuilder();
                User user = userMgr.getUser(task.getCreateUserId());
                text.append("请审核").append(user.getName()).append("提交的配置变更!");

                logMailBean.sendHtmlEmail(emailToList, title.toString(),
                        "<br/><br/><a href='" + url + "'>" + text + "</a>");
            });
        }

        return buildSuccess("提交成功!");
    }


    /**
     * 配置项的更新
     *
     * @param id
     * @param value
     *
     * @return
     */
    @RequestMapping(value = "/item/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public JsonObjectBase updateItem(@PathVariable long id, String value) {

        this.validateDraftId(id);
        configDraftMgr.updateItemValue(id, value);

        return buildSuccess("保存成功!");
    }

    /**
     * 配置文件的更新
     *
     * @param id
     * @param file
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/file/{id}", method = RequestMethod.POST)
    public JsonObjectBase updateFile(@PathVariable long id, @RequestParam("myfilerar") MultipartFile file) {

        int fileSize = 1024 * 1024 * 4;
        String[] allowExtName = {".properties", ".xml"};
        fileUploadValidator.validateFile(file, fileSize, allowExtName);

        this.validateDraftId(id);

        try {
            updateFileText(id,file.getBytes());
        } catch (IOException e) {
            LOG.error(e.toString());
            throw new FileUploadException("upload file error", e);
        }

        return buildSuccess("保存成功!");
    }

    /**
     * 配置文件的更新(文本修改)
     *
     * @param id
     * @param fileContent
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/filetext/{id}", method = RequestMethod.PUT)
    public JsonObjectBase updateFileWithText(@PathVariable long id, @NotNull String fileContent) {


        // 业务校验
        this.validateDraftId(id);

        //
        // 更新
        //
        updateFileText(id,fileContent.getBytes());


        return buildSuccess("保存成功!");
    }

    private void validateDraftId(long id){

        ConfigDraft configDraft = configDraftMgr.findById(id);
        if(null == configDraft){
            throw new ValidationException("不存在此草稿 id:"+id);
        }
    }

    private void validateDraftSubmit(ConfDraftSubmitForm confDraftSubmitForm){

        Task task = new Task();
        task.setAppId(confDraftSubmitForm.getAppId());
        task.setEnvId(confDraftSubmitForm.getEnvId());
        task.setVersion(confDraftSubmitForm.getVersion());

        List<Task> taskList = taskMgr.findAuditingOrNotExecTask(task);

        if(!CollectionUtils.isEmpty(taskList)){
            throw new ValidationException("此版本已存在正在审核中的任务或审核通过还未执行的任务，不能提交!");
        }

    }

    private void updateFileText(long id,byte fileContentByte[]){

        try {

            String str = new String(fileContentByte, "UTF-8");
            LOG.info("receive file: " + str);

            configDraftMgr.updateItemValue(id,str);
            LOG.info("update " + id + " ok");

        } catch (Exception e) {

            LOG.error(e.toString());
            throw new FileUploadException("upload file error", e);
        }

    }


    @ResponseBody
    @RequestMapping(value = "/versionlist")
    public JsonObjectBase versionlist(ConfigDraftCondition configDraft){

        Visitor visitor = ThreadContext.getSessionVisitor();
        configDraft.setUserId(visitor.getId());
        configDraft.setStatus(Constants.STATUS_NORMAL);

        Page<ConfigDraft> page = configDraftMgr.findByConfigDraft(configDraft,new PageRequest(0,Integer.MAX_VALUE));

        Set<String> versionSet = Sets.newHashSet();

        List<ConfigDraft> draftList = page.getContent();

        if(!CollectionUtils.isEmpty(draftList)){

            for(ConfigDraft draft : draftList){
                versionSet.add(draft.getVersion());
            }
        }

        return buildSuccess(versionSet);
    }

}