package com.baidu.disconf.web.service.config.service.impl;

import com.baidu.disconf.core.common.constants.DisConfigTypeEnum;
import com.baidu.disconf.web.common.Constants;
import com.baidu.disconf.web.common.SupportFileTypeEnum;
import com.baidu.disconf.web.config.ApplicationPropertyConfig;
import com.baidu.disconf.web.innerapi.zookeeper.ZooKeeperDriver;
import com.baidu.disconf.web.service.app.bo.App;
import com.baidu.disconf.web.service.app.service.AppMgr;
import com.baidu.disconf.web.service.config.bo.Config;
import com.baidu.disconf.web.service.config.bo.ConfigDraft;
import com.baidu.disconf.web.service.config.constant.ConfigDraftTypeEnum;
import com.baidu.disconf.web.service.config.dao.ConfigDao;
import com.baidu.disconf.web.service.config.dao.ConfigDraftDao;
import com.baidu.disconf.web.service.config.form.ConfCopyForm;
import com.baidu.disconf.web.service.config.form.ConfListForm;
import com.baidu.disconf.web.service.config.form.ConfNewItemForm;
import com.baidu.disconf.web.service.config.service.ConfigHistoryMgr;
import com.baidu.disconf.web.service.config.service.ConfigMgr;
import com.baidu.disconf.web.service.config.vo.ConfListVo;
import com.baidu.disconf.web.service.config.vo.MachineListVo;
import com.baidu.disconf.web.service.env.bo.Env;
import com.baidu.disconf.web.service.env.service.EnvMgr;
import com.baidu.disconf.web.service.user.dto.Visitor;
import com.baidu.disconf.web.service.zookeeper.dto.ZkDisconfData;
import com.baidu.disconf.web.service.zookeeper.dto.ZkDisconfData.ZkDisconfDataItem;
import com.baidu.disconf.web.service.zookeeper.service.ZkDeployMgr;
import com.baidu.disconf.web.utils.CodeUtils;
import com.baidu.disconf.web.utils.DiffUtils;
import com.baidu.disconf.web.utils.MyStringUtils;
import com.baidu.dsp.common.constant.DataFormatConstants;
import com.baidu.dsp.common.utils.BeanUtils;
import com.baidu.dsp.common.utils.DataTransfer;
import com.baidu.dsp.common.utils.ServiceUtil;
import com.baidu.dsp.common.utils.email.LogMailBean;
import com.baidu.ub.common.commons.ThreadContext;
import com.baidu.ub.common.db.DaoPageResult;
import com.github.knightliao.apollo.utils.data.GsonUtils;
import com.github.knightliao.apollo.utils.io.OsUtil;
import com.github.knightliao.apollo.utils.time.DateUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author liaoqiqi
 * @version 2014-6-16
 */
@Service
public class ConfigMgrImpl implements ConfigMgr {

    protected static final Logger LOG = LoggerFactory.getLogger(ConfigMgrImpl.class);

    @Autowired
    private ConfigDao configDao;

    @Autowired
    private AppMgr appMgr;

    @Autowired
    private EnvMgr envMgr;

    @Autowired
    private ZooKeeperDriver zooKeeperDriver;

    @Autowired
    private ZkDeployMgr zkDeployMgr;

    @Autowired
    private LogMailBean logMailBean;

    @Autowired
    private ApplicationPropertyConfig applicationPropertyConfig;

    @Autowired
    private ConfigHistoryMgr configHistoryMgr;

    @Autowired
    private ConfigDraftDao configDraftDao;

    /**
     * 根据APPid获取其版本列表
     */
    @Override
    public List<String> getVersionListByAppEnv(Long appId, Long envId) {

        List<String> versionList = new ArrayList<String>();

        List<Config> configs = configDao.getConfByAppEnv(appId, envId);

        for (Config config : configs) {

            if (!versionList.contains(config.getVersion())) {
                versionList.add(config.getVersion());
            }
        }

        return versionList;
    }

    /**
     * 配置文件的整合
     *
     * @param confListForm
     *
     * @return
     */
    public List<File> getDisconfFileList(ConfListForm confListForm) {

        List<Config> configList =
                configDao.getConfigList(confListForm.getAppId(), confListForm.getEnvId(), confListForm.getVersion());

        // 时间作为当前文件夹
        String curTime = DateUtils.format(new Date(), DataFormatConstants.COMMON_TIME_FORMAT);
        curTime = "tmp" + File.separator + curTime;
        OsUtil.makeDirs(curTime);

        List<File> files = new ArrayList<File>();
        for (Config config : configList) {

            if (config.getType().equals(DisConfigTypeEnum.FILE.getType())) {

                File file = new File(curTime, config.getName());
                try {
                    FileUtils.writeByteArrayToFile(file, config.getValue().getBytes());
                } catch (IOException e) {
                    LOG.warn(e.toString());
                }

                files.add(file);
            }
        }

        return files;
    }

    /**
     * 配置列表
     */
    @Override
    public DaoPageResult<ConfListVo> getConfigList(ConfListForm confListForm, boolean fetchZk,
                                                   final boolean getErrorMessage) {

        //
        // 数据据结果
        //
        DaoPageResult<Config> configList = configDao.getConfigList(confListForm.getAppId(), confListForm.getEnvId(),
                confListForm.getVersion(),
                confListForm.getPage());

        //
        //
        //
        final App app = appMgr.getById(confListForm.getAppId());
        final Env env = envMgr.getById(confListForm.getEnvId());

        //
        //
        //
        final boolean myFetchZk = fetchZk;
        Map<String, ZkDisconfData> zkDataMap = new HashMap<String, ZkDisconfData>();
        if (myFetchZk) {
            zkDataMap = zkDeployMgr.getZkDisconfDataMap(app.getName(), env.getName(), confListForm.getVersion());
        }
        final Map<String, ZkDisconfData> myzkDataMap = zkDataMap;

        //
        // 进行转换
        //
        DaoPageResult<ConfListVo> configListVo =
                ServiceUtil.getResult(configList, new DataTransfer<Config, ConfListVo>() {

                    @Override
                    public ConfListVo transfer(Config input) {

                        String appNameString = app.getName();
                        String envName = env.getName();

                        ZkDisconfData zkDisconfData = null;
                        if (myzkDataMap != null && myzkDataMap.keySet().contains(input.getName())) {
                            zkDisconfData = myzkDataMap.get(input.getName());
                        }
                        ConfListVo configListVo = convert(input, appNameString, envName, zkDisconfData);

                        // 列表操作不要显示值, 为了前端显示快速(只是内存里操作)
                        if (!myFetchZk || !getErrorMessage) {

                            // 列表 value 设置为 ""
                            configListVo.setValue("");
                            configListVo.setMachineList(new ArrayList<ZkDisconfData.ZkDisconfDataItem>());
                        }

                        return configListVo;
                    }
                });

        return configListVo;
    }

    /**
     * 获取ZK data
     */
    private MachineListVo getZkData(List<ZkDisconfDataItem> datalist, Config config) {

        int errorNum = 0;
        try {
            for (ZkDisconfDataItem zkDisconfDataItem : datalist) {

                if (config.getType().equals(DisConfigTypeEnum.FILE.getType())) {

                    List<String> errorKeyList = compareConfig(zkDisconfDataItem.getValue(), config);

                    if (errorKeyList.size() != 0) {
                        zkDisconfDataItem.setErrorList(errorKeyList);
                        errorNum++;
                    }
                } else {

                    //
                    // 配置项
                    //

                    if (zkDisconfDataItem.getValue().trim().equals(config.getValue().trim())) {

                    } else {
                        List<String> errorKeyList = new ArrayList<String>();
                        errorKeyList.add(config.getValue().trim());
                        zkDisconfDataItem.setErrorList(errorKeyList);
                        errorNum++;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("----检查zk 与 db 出现 异常: {}",e.getCause());
        }

        MachineListVo machineListVo = new MachineListVo();
        machineListVo.setDatalist(datalist);
        machineListVo.setErrorNum(errorNum);
        machineListVo.setMachineSize(datalist.size());

        return machineListVo;
    }

    /**
     * 转换成配置返回
     *
     * @param config
     *
     * @return
     */
    private ConfListVo convert(Config config, String appNameString, String envName, ZkDisconfData zkDisconfData) {

        ConfListVo confListVo = new ConfListVo();

        confListVo.setConfigId(config.getId());
        confListVo.setAppId(config.getAppId());
        confListVo.setAppName(appNameString);
        confListVo.setEnvName(envName);
        confListVo.setEnvId(config.getEnvId());
        confListVo.setCreateTime(config.getCreateTime());
        confListVo.setModifyTime(config.getUpdateTime().substring(0, 12));
        confListVo.setKey(config.getName());
        // StringEscapeUtils.escapeHtml escape
        confListVo.setValue(CodeUtils.unicodeToUtf8(config.getValue()));
        confListVo.setVersion(config.getVersion());
        confListVo.setType(DisConfigTypeEnum.getByType(config.getType()).getModelName());
        confListVo.setTypeId(config.getType());

        //
        //
        //
        if (zkDisconfData != null) {

            confListVo.setMachineSize(zkDisconfData.getData().size());

            List<ZkDisconfDataItem> datalist = zkDisconfData.getData();

            MachineListVo machineListVo = getZkData(datalist, config);

            confListVo.setErrorNum(machineListVo.getErrorNum());
            confListVo.setMachineList(machineListVo.getDatalist());
            confListVo.setMachineSize(machineListVo.getMachineSize());
        }

        return confListVo;
    }

    /**
     *
     */
    private List<String> compareConfig(String zkData, Config config) {

        String dbData = config.getValue();
        String configName = config.getName();

        List<String> errorKeyList = new ArrayList<String>();

        //首先 直接字符串比对  相同退出、不相同则进一步 ： 暂时只针对properties文件key value对比 非properties文件则直接不一至

        if(!zkData.equals(dbData)){
            if(SupportFileTypeEnum.PROPERTIES == SupportFileTypeEnum.getByFileName(configName)){
                comparePropertiesConfig(errorKeyList,zkData,dbData);
            }else {
                errorKeyList.add(zkData);
            }
        }

        return errorKeyList;
    }
    private Properties loadPropertiesFromString(String properties) throws Exception{

        Properties prop = new Properties();

        prop.load(IOUtils.toInputStream(properties));

        return prop;
    }

    private void comparePropertiesConfig(List<String> errorKeyList,String zkData,String dbData){

        Properties zkProp = null;
        Properties dbProp = null;
        try {
            zkProp = this.loadPropertiesFromString(zkData);
            dbProp = this.loadPropertiesFromString(dbData);
        } catch (Exception e) {
            LOG.error(e.toString());
            errorKeyList.add(zkData);
            return;
        }


        for (Object keyInZk : zkProp.keySet()) {

            String zkDataStr = (String)zkProp.get(keyInZk);
            String valueInDb = (String)dbProp.get(keyInZk);

            try {

                if ((zkDataStr == null && valueInDb != null) || (zkDataStr != null && valueInDb == null)) {
                    errorKeyList.add((String)keyInZk);

                } else {

                    zkDataStr = zkDataStr.trim();
                    boolean isEqual = true;

                    if (MyStringUtils.isDouble(zkDataStr) && MyStringUtils.isDouble(valueInDb.toString())) {

                        if (Math.abs(Double.parseDouble(zkDataStr) - Double.parseDouble(valueInDb.toString())) >
                                0.001d) {
                            isEqual = false;
                        }

                    } else {
                        if (!zkDataStr.equals(valueInDb.toString().trim())) {
                            isEqual = false;
                        }
                    }

                    if (!isEqual) {
                        errorKeyList
                                .add(keyInZk + "\t" + DiffUtils.getDiffSimple(zkDataStr, valueInDb.toString().trim()));
                    }
                }

            } catch (Exception e) {

                LOG.warn(e.toString() + " ; " + keyInZk + " ; " + zkProp.get(keyInZk) + " ; " + valueInDb);
            }
        }

    }

    /**
     * 根据 配置ID获取配置返回
     */
    @Override
    public ConfListVo getConfVo(Long configId) {
        Config config = configDao.get(configId);

        App app = appMgr.getById(config.getAppId());
        Env env = envMgr.getById(config.getEnvId());

        return convert(config, app.getName(), env.getName(), null);
    }

    /**
     * 根据 配置ID获取ZK对比数据
     */
    @Override
    public MachineListVo getConfVoWithZk(Long configId) {

        Config config = configDao.get(configId);

        App app = appMgr.getById(config.getAppId());
        Env env = envMgr.getById(config.getEnvId());

        //
        //
        //

        DisConfigTypeEnum disConfigTypeEnum = DisConfigTypeEnum.FILE;
        if (config.getType().equals(DisConfigTypeEnum.ITEM.getType())) {
            disConfigTypeEnum = DisConfigTypeEnum.ITEM;
        }

        ZkDisconfData zkDisconfData = zkDeployMgr.getZkDisconfData(app.getName(), env.getName(), config.getVersion(),
                disConfigTypeEnum, config.getName());

        if (zkDisconfData == null) {
            return new MachineListVo();
        }

        MachineListVo machineListVo = getZkData(zkDisconfData.getData(), config);
        return machineListVo;
    }

    /**
     * 根据配置ID获取配置
     */
    @Override
    public Config getConfigById(Long configId) {

        return configDao.get(configId);
    }

    /**
     * 更新 配置项/配置文件 的值
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    public void updateOneItemValue(Config config, String value) {

        String oldValue = config.getValue();
        Long configId = config.getId();

        //
        // 配置数据库的值 encode to db
        //
        configDao.updateValue(configId, CodeUtils.utf8ToUnicode(value));
        configHistoryMgr.createOne(configId, oldValue, CodeUtils.utf8ToUnicode(value));

    }

    @Override

    public String updateItemValue(Long configId,String value){

        Config config = getConfigById(configId);
        config.setValue(value);

        this.newConfigDraft(config,ConfigDraftTypeEnum.modify); //保存草稿
        //
        // 发送邮件通知
        //
//        String toEmails = appMgr.getEmails(config.getAppId());
//
//        if (applicationPropertyConfig.isEmailMonitorOn() == true) {
//            boolean isSendSuccess = logMailBean.sendHtmlEmail(toEmails,
//                    " config update", DiffUtils.getDiff(CodeUtils.unicodeToUtf8(oldValue),
//                            value,
//                            config.toString(),
//                            getConfigUrlHtml(config)));
//            if (isSendSuccess) {
//                return "修改成功，邮件通知成功";
//            } else {
//                return "修改成功，邮件发送失败，请检查邮箱配置";
//            }
//        }

        return "修改成功";
    }

    /**
     * 主要用于邮箱发送
     *
     * @return
     */
    private String getConfigUrlHtml(Config config) {

        return "<br/>点击<a href='http://" + applicationPropertyConfig.getDomain() + "/modifyFile.html?configId=" +
                config.getId() + "'> 这里 </a> 进入查看<br/>";
    }

    /**
     * 主要用于邮箱发送
     *
     * @param newValue
     * @param identify
     *
     * @return
     */
    private String getNewValue(String newValue, String identify, String htmlClick) {

        String contentString = StringEscapeUtils.escapeHtml(identify) + "<br/>" + htmlClick + "<br/><br/> ";

        String data = "<br/><br/><br/><span style='color:#FF0000'>New value:</span><br/>";
        contentString = contentString + data + StringEscapeUtils.escapeHtml(newValue);

        return contentString;
    }

    /**
     * 通知Zookeeper, 失败时不回滚数据库,通过监控来解决分布式不一致问题
     */
    @Override
    public void notifyZookeeper(Long configId) {

        ConfListVo confListVo = getConfVo(configId);

        if (confListVo.getTypeId().equals(DisConfigTypeEnum.FILE.getType())) {

            zooKeeperDriver.notifyNodeUpdate(confListVo.getAppName(), confListVo.getEnvName(), confListVo.getVersion(),
                    confListVo.getKey(), GsonUtils.toJson(confListVo.getValue()),
                    DisConfigTypeEnum.FILE);

        } else {

            zooKeeperDriver.notifyNodeUpdate(confListVo.getAppName(), confListVo.getEnvName(), confListVo.getVersion(),
                    confListVo.getKey(), confListVo.getValue(), DisConfigTypeEnum.ITEM);
        }

    }

    /**
     * 获取配置值
     */
    @Override
    public String getValue(Long configId) {
        return configDao.getValue(configId);
    }

    /**
     * 新建配置
     */
    @Override
    public Config newConfig(ConfNewItemForm confNewForm, DisConfigTypeEnum disConfigTypeEnum) {

        Config config = new Config();

        config.setAppId(confNewForm.getAppId());
        config.setEnvId(confNewForm.getEnvId());
        config.setName(confNewForm.getKey());
        config.setType(disConfigTypeEnum.getType());
        config.setVersion(confNewForm.getVersion());
        config.setValue(CodeUtils.utf8ToUnicode(confNewForm.getValue()));
        config.setStatus(Constants.STATUS_NORMAL);

        // 时间
        String curTime = DateUtils.format(new Date(), DataFormatConstants.COMMON_TIME_FORMAT);
        config.setCreateTime(curTime);
        config.setUpdateTime(curTime);

        this.newConfigDraft(config,ConfigDraftTypeEnum.create); //保存草稿

//        // 发送邮件通知
//        //
//        String toEmails = appMgr.getEmails(config.getAppId());
//        if (applicationPropertyConfig.isEmailMonitorOn() == true) {
//            logMailBean.sendHtmlEmail(toEmails, " config new", getNewValue(confNewForm.getValue(), config.toString(),
//                    getConfigUrlHtml(config)));
//        }
        return config;
    }

    /**
     * 草稿生效 转化成config
     * @param configDraft
     * @return
     */
    public Config execDraftToCofing(ConfigDraft configDraft){

        ConfigDraftTypeEnum draftType = ConfigDraftTypeEnum.getByValue(configDraft.getDraftType());

        Config config = null;

        switch (draftType){
            case create:
                    config = createConfigByDraft(configDraft);
                break;
            case modify:
                    config = modifyConfigByDraft(configDraft);
                break;
            case delete:
                    config = deleteConfigByDraft(configDraft);
                break;
        }

        //todo 所有的邮件处理暂时没有做
        //todo 生效后给zookeeper发通知 configMgr.notifyZookeeper(config.getId());

        return config;
    }

    /**
     * 生效新增
     * @param configDraft
     * @return
     */
    private Config createConfigByDraft(ConfigDraft configDraft){

        Config config = new Config();

        config.setAppId(configDraft.getAppId());
        config.setEnvId(configDraft.getEnvId());
        config.setName(configDraft.getName());
        config.setType(configDraft.getType());
        config.setVersion(configDraft.getVersion());
        config.setValue(CodeUtils.utf8ToUnicode(configDraft.getValue()));
        config.setStatus(Constants.STATUS_NORMAL);

        // 时间
        String curTime = DateUtils.format(new Date(), DataFormatConstants.COMMON_TIME_FORMAT);
        config.setCreateTime(curTime);
        config.setUpdateTime(curTime);

        configDao.create(config);
        configHistoryMgr.createOne(config.getId(), "", config.getValue());

        return config;
    }

    /**
     * 生效修改
     * @param configDraft
     * @return
     */
    private Config modifyConfigByDraft(ConfigDraft configDraft){

        Config config = configDao.get(configDraft.getConfigId());
        String value = configDraft.getValue();

        this.updateOneItemValue(config,value);
        config.setVersion(value);

        return config;
    }

    /**
     * 生效删除
     * @param configDraft
     * @return
     */
    private Config deleteConfigByDraft(ConfigDraft configDraft){

        Long configId = configDraft.getConfigId();
        Config config = configDao.get(configId);

        configHistoryMgr.createOne(configId, config.getValue(), "");

        configDao.deleteItem(configId);

        return config;
    }

    @Override
    public ConfigDraft newConfigDraft(Config config,ConfigDraftTypeEnum configDraftTypeEnum) {

        ConfigDraft configDraft = new ConfigDraft();

        App app = appMgr.getById(config.getAppId());
        Env env = envMgr.getById(config.getEnvId());

        configDraft.setAppId(config.getAppId());
        configDraft.setAppName(app.getName());
        configDraft.setEnvId(config.getEnvId());
        configDraft.setEnvName(env.getName());
        configDraft.setName(config.getName());
        configDraft.setType(config.getType());
        configDraft.setVersion(config.getVersion());
        configDraft.setValue(CodeUtils.utf8ToUnicode(config.getValue()));
        configDraft.setStatus(Constants.STATUS_NORMAL);
        configDraft.setDraftType(configDraftTypeEnum.getValue()); // 草稿类型
        Visitor visitor = ThreadContext.getSessionVisitor();
        configDraft.setUserId(visitor.getId()); //当前用户

        configDraft.setConfigId(config.getId());

        // 时间
        String curTime = DateUtils.format(new Date(), DataFormatConstants.COMMON_TIME_FORMAT);
        configDraft.setCreateTime(curTime);
        configDraft.setUpdateTime(curTime);

        configDraftDao.create(configDraft);

        return configDraft;
    }

    /**
     * 删除配置
     *
     * @param configId
     */
    @Override
    public void delete(Long configId) {

        Config config = configDao.get(configId);

        this.newConfigDraft(config,ConfigDraftTypeEnum.delete); //保存草稿
    }

    @Override
    @Transactional
    public void copyConfig(ConfCopyForm confCopyForm) {

        Long appId = confCopyForm.getAppId();
        Long envId = confCopyForm.getEnvId();
        String version = confCopyForm.getVersion();

        Long destEnvId = confCopyForm.getNewEnvId();
        String destVersion = confCopyForm.getNewVersion();

        Visitor visitor = ThreadContext.getSessionVisitor();
        Long userId = visitor.getId();

        String curTime = DateUtils.format(new Date(), DataFormatConstants.COMMON_TIME_FORMAT);

        //找到当前用户 目的地版本下所有正常的草稿
        List<ConfigDraft> configDraftList = configDraftDao.findSubmitDraft(
                appId,destEnvId,destVersion,userId,Constants.STATUS_NORMAL);

        if(!CollectionUtils.isEmpty(configDraftList)){
            for(ConfigDraft configDraft:configDraftList){
                configDraftDao.updateStatus(configDraft.getId(),Constants.STATUS_DELETE);
            }
        }

        List<Config> configList = configDao.getConfigList(appId, envId, version); //源配置

        List<Config> existConfigList = configDao.getConfigList(appId, destEnvId, destVersion); //目的地已存在的配置

        if(!CollectionUtils.isEmpty(existConfigList)){//如果存在 则直接删除
            for(Config config : existConfigList){
                this.newConfigDraft(config,ConfigDraftTypeEnum.delete);
            }
        }

        if(!CollectionUtils.isEmpty(configList)){


            for(Config config:configList){

                Config newConfig = (Config)BeanUtils.getClone(config);
                newConfig.setId(null);
                newConfig.setVersion(confCopyForm.getNewVersion());
                newConfig.setEnvId(confCopyForm.getNewEnvId());

                newConfig.setCreateTime(curTime);
                newConfig.setUpdateTime(curTime);


                this.newConfigDraft(newConfig,ConfigDraftTypeEnum.create);
            }

        }
    }

    @Override
    public boolean isEnvAndVersionExist(ConfCopyForm confCopyForm) {

        List<Config> configList = configDao.getConfigList(confCopyForm.getAppId(), confCopyForm.getNewEnvId(), confCopyForm.getNewVersion());

        if(!CollectionUtils.isEmpty(configList)){
            return true;
        }
        return false;
    }
}
