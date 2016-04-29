package com.baidu.disconf.web.tasks.impl;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.web.config.ApplicationPropertyConfig;
import com.baidu.disconf.web.innerapi.zookeeper.ZooKeeperDriver;
import com.baidu.disconf.web.service.config.bo.Config;
import com.baidu.disconf.web.service.config.service.ConfigDraftMgr;
import com.baidu.disconf.web.service.config.service.ConfigMgr;
import com.baidu.disconf.web.service.task.bo.Task;
import com.baidu.disconf.web.service.task.service.TaskMgr;
import com.baidu.disconf.web.service.user.bo.User;
import com.baidu.disconf.web.service.user.service.UserAppMgr;
import com.baidu.disconf.web.service.user.service.UserMgr;
import com.baidu.disconf.web.tasks.DraftToConfigService;
import com.baidu.disconf.web.tasks.runnable.DraftToConfigLock;
import com.baidu.dsp.common.interceptor.session.SessionInterceptor;
import com.baidu.dsp.common.utils.email.LogMailBean;
import com.github.knightliao.apollo.utils.tool.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qianmin on 2016/4/25.
 */
@Component
public class DraftToConfigServiceImpl implements DraftToConfigService{
    protected static final Logger LOG = LoggerFactory.getLogger(DraftToConfigServiceImpl.class);

    @Autowired
    private ZooKeeperDriver zooKeeperDriver;

    @Autowired
    private TaskExecutor proExecutor;

    @Autowired
    private ConfigDraftMgr configDraftMgr;

    @Autowired
    private ConfigMgr configMgr;

    @Autowired
    private TaskMgr taskMgr;

    @Autowired
    private UserAppMgr userAppMgr;

    @Autowired
    private UserMgr userMgr;

    @Autowired
    private LogMailBean logMailBean;

    @Autowired
    private ApplicationPropertyConfig applicationPropertyConfig;

    @Scheduled(fixedDelay = Constants.DRAFT_TO_CONFIG_SCHEDULE_TIME)
    @Override
    public void doService() {

        if(zooKeeperDriver.tryLockDraftToConfig()){
            LOG.info("-------获取到锁，开始执行!");
            try {
                MDC.put(SessionInterceptor.SESSION_KEY, TokenUtil.generateToken());

                draftToConfig();
            }catch (Exception e){

            } finally {
                proExecutor.execute(new DraftToConfigLock(zooKeeperDriver));
            }
            LOG.info("-------执行结束");
        }else {
            LOG.info("-------未获取到锁，退出!");
        }
    }


    public void draftToConfig(){
        List<Task> toBeActiveTask = taskMgr.findToBeActiveTask();

        List<Config> configList = new ArrayList<Config>();
        for(Task task : toBeActiveTask) {
            try {
                //task关联的ConfigDraft转换为Config， task状态变更为done， 同步zk
                configDraftMgr.draftToConfig(task);

                //发送mail
                sendEmail(task);

            }catch (Exception e){
                LOG.error(e.toString(), e);
            }
        }
    }

    private void sendEmail(Task task){
        String mailToList = userMgr.getMailToList(task.getAppId(), null);
        String url = applicationPropertyConfig.getDomain() + "/task_config_detail.html?id="
                + task.getId() + "&jump=1";

        if(applicationPropertyConfig.isEmailMonitorOn()){
            StringBuilder titile = new StringBuilder();
            titile.append("配置变更已生效: [").append(task.getAppName()).append("][").append(task.getEnvName())
                    .append("][").append(task.getVersion()).append("]");
            StringBuilder text = new StringBuilder();
            User user = userMgr.getUser(task.getCreateUserId());
            text.append(user.getName()).append("提交的配置变更已生效，点击查看详情!");

            logMailBean.sendHtmlEmail(mailToList, titile.toString(),
                    "<br/><br/><a href='" + url + "'>" + text + "</a>");
        }

    }
}
