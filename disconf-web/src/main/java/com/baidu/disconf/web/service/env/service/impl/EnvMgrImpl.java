package com.baidu.disconf.web.service.env.service.impl;

import java.util.*;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baidu.disconf.web.common.Constants;
import com.baidu.disconf.web.service.env.bo.Env;
import com.baidu.disconf.web.service.env.dao.EnvDao;
import com.baidu.disconf.web.service.env.service.EnvMgr;
import com.baidu.disconf.web.service.env.vo.EnvListVo;

/**
 * @author liaoqiqi
 * @version 2014-6-16
 */
@Service
public class EnvMgrImpl implements EnvMgr {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvMgrImpl.class);

    @Autowired
    private EnvDao envDao;

    @Override
    public Env getByName(String name) {

        return envDao.getByName(name);
    }

    /**
     *
     */
    @Override
    public List<EnvListVo> getVoList() {

        List<Env> envs = envDao.findAll();

        List<EnvListVo> envListVos = new ArrayList<EnvListVo>();
        for (Env env : envs) {
            EnvListVo envListVo = new EnvListVo();
            envListVo.setId(env.getId());
            envListVo.setName(env.getName());

            envListVos.add(envListVo);
        }

        return envListVos;
    }

    @Override
    public Map<Long, Env> getByIds(Set<Long> ids) {

        if (ids.size() == 0) {
            return new HashMap<Long, Env>();
        }

        List<Env> envs = envDao.get(ids);

        Map<Long, Env> map = new HashMap<Long, Env>();
        for (Env env : envs) {
            map.put(env.getId(), env);
        }

        return map;
    }

    @Override
    public Env getById(Long id) {
        return envDao.get(id);
    }

    /**
     *
     */
    @Override
    public List<Env> getList() {
        return envDao.findAll();
    }


    @PostConstruct
    public void initDefineEnv(){
        List<Env> envList = getList();
        for (Env env : envList) {
            if(env.getName().equals("STG")||env.getName().equals("PRD")){
                Constants.IS_STG_PRD = true;
                break;
            }
        }
        LOGGER.info("system init current env is :{}", Constants.IS_STG_PRD ? "PRD" : "UAT");
    }
}
