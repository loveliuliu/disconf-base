package com.baidu.disconf.web.web.config.controller;

import com.baidu.disconf.web.common.Constants;
import com.baidu.disconf.web.service.config.bo.Config;
import com.baidu.disconf.web.service.config.bo.ConfigDraft;
import com.baidu.disconf.web.service.config.condition.ConfigDraftCondition;
import com.baidu.disconf.web.service.config.form.ConfDraftSubmitForm;
import com.baidu.disconf.web.service.config.service.ConfigDraftMgr;
import com.baidu.disconf.web.service.config.service.ConfigMgr;
import com.baidu.disconf.web.service.user.dto.Visitor;
import com.baidu.disconf.web.web.config.validator.FileUploadValidator;
import com.baidu.dsp.common.constant.DataFormatConstants;
import com.baidu.dsp.common.constant.ErrorCode;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.exception.FileUploadException;
import com.baidu.dsp.common.exception.ValidationException;
import com.baidu.dsp.common.vo.JsonObjectBase;
import com.baidu.ub.common.commons.ThreadContext;
import com.github.knightliao.apollo.utils.common.StringUtil;
import com.github.knightliao.apollo.utils.time.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

        //todo 验证一个app 环境 版本 只能有一个正在 审核或审核通过未执行的任务
        configDraftMgr.submit(confDraftSubmitForm);

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

}