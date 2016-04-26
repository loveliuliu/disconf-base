package com.baidu.disconf.web.tasks.impl;

import java.util.ArrayList;
import java.util.List;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.web.innerapi.zookeeper.ZooKeeperDriver;
import com.baidu.disconf.web.service.user.service.UserMgr;
import com.baidu.disconf.web.tasks.runnable.ReleaseConfigConsistencyLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baidu.disconf.web.config.ApplicationPropertyConfig;
import com.baidu.disconf.web.service.app.bo.App;
import com.baidu.disconf.web.service.app.service.AppMgr;
import com.baidu.disconf.web.service.config.form.ConfListForm;
import com.baidu.disconf.web.service.config.service.ConfigMgr;
import com.baidu.disconf.web.service.config.vo.ConfListVo;
import com.baidu.disconf.web.service.env.bo.Env;
import com.baidu.disconf.web.service.env.service.EnvMgr;
import com.baidu.disconf.web.service.zookeeper.dto.ZkDisconfData.ZkDisconfDataItem;
import com.baidu.disconf.web.service.zookeeper.service.ZkDeployMgr;
import com.baidu.disconf.web.tasks.IConfigConsistencyMonitorService;
import com.baidu.dsp.common.interceptor.session.SessionInterceptor;
import com.baidu.dsp.common.utils.email.LogMailBean;
import com.baidu.ub.common.db.DaoPageResult;
import com.github.knightliao.apollo.utils.tool.TokenUtil;

/**
 * http://blog.csdn.net/sd4000784/article/details/7745947 <br/>
 * http://blog.sina.com.cn/s/blog_6925c03c0101d1hi.html
 *
 * @author knightliao
 */
@Component
public class ConfigConsistencyMonitorServiceImpl implements IConfigConsistencyMonitorService {

    protected static final Logger LOG = LoggerFactory.getLogger(ConfigConsistencyMonitorServiceImpl.class);

    @Autowired
    private ApplicationPropertyConfig applicationPropertyConfig;

    @Autowired
    private ZkDeployMgr zkDeployMgr;

    @Autowired
    private AppMgr appMgr;

    @Autowired
    private EnvMgr envMgr;

    @Autowired
    private ConfigMgr configMgr;

    @Autowired
    private UserMgr userMgr;

    @Autowired
    private LogMailBean logMailBean;

    @Autowired
    private ZooKeeperDriver zooKeeperDriver;

    @Autowired
    private TaskExecutor proExecutor;


    /**
     *
     */
    // 每30分钟执行一次自动化校验
    @Scheduled(fixedDelay = Constants.CONFIG_CONSISTENCY_SCHEDULE_TIME)
    @Override
    public void check() {

        if (!applicationPropertyConfig.isCheckConsistencyOn()) {
            return;
        }

        if(zooKeeperDriver.tryLockConfigConsistency()){
            LOG.info("-------获取到锁，开始执行!");
            try {
                MDC.put(SessionInterceptor.SESSION_KEY, TokenUtil.generateToken());

                checkMgr();

            } finally {
                proExecutor.execute(new ReleaseConfigConsistencyLock(zooKeeperDriver));
            }
        }else {
            LOG.info("-------未获取到锁，退出!");
        }

    }

    /**
     * 主check MGR
     */
    private void checkMgr() {

        List<App> apps = appMgr.getAppList();
        List<Env> envs = envMgr.getList();

        // app
        for (App app : apps) {

            checkAppConfigConsistency(app, envs);
        }
    }

    /**
     * 校验APP 一致性
     */
    private void checkAppConfigConsistency(App app, List<Env> envs) {

        // env
        for (Env env : envs) {

            // version
            List<String> versionList = configMgr.getVersionListByAppEnv(app.getId(), env.getId());

            for (String version : versionList) {

                checkAppEnvVersionConfigConsistency(app, env, version);
            }
        }
    }

    /**
     * 校验APP/ENV/VERSION 一致性
     */
    private void checkAppEnvVersionConfigConsistency(App app, Env env, String version) {

        String monitorInfo = "monitor " + app.getName() + "\t" + env.getName() + "\t" + version;
        LOG.info(monitorInfo);

        //
        //
        //
        ConfListForm confiConfListForm = new ConfListForm();
        confiConfListForm.setAppId(app.getId());
        confiConfListForm.setEnvId(env.getId());
        confiConfListForm.setVersion(version);

        //
        //
        //
        DaoPageResult<ConfListVo> daoPageResult = configMgr.getConfigList(confiConfListForm, true, true);

        // 准备发送邮件通知
        String toEmails = userMgr.getMailToList(app.getId(), null);

        List<ConfListVo> confListVos = daoPageResult.getResult();

        List<String> errorList = new ArrayList<String>();
        for (ConfListVo confListVo : confListVos) {

            if (confListVo.getErrorNum() != 0) {

                List<ZkDisconfDataItem> zkDisconfDataItems = confListVo.getMachineList();
                for (ZkDisconfDataItem zkDisconfDataItem : zkDisconfDataItems) {

                    if (zkDisconfDataItem.getErrorList().size() != 0) {

                        String data = zkDisconfDataItem.toString() + "<br/><br/><br/><br/><br/><br/>original:" +
                                confListVo.getValue();

                        LOG.warn(data);

                        errorList.add(data + "<br/><br/><br/>");

                    }
                }
            }
        }

        if (errorList.size() != 0) {

            logMailBean.sendHtmlEmail(toEmails, " monitor ConfigConsistency ",
                    monitorInfo + "<br/><br/><br/>" + errorList.toString());
        }
    }
}
