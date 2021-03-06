package com.baidu.disconf.web.service.config.dao.impl;

import com.baidu.disconf.core.common.constants.DisConfigTypeEnum;
import com.baidu.disconf.web.common.Constants;
import com.baidu.disconf.web.service.config.bo.Config;
import com.baidu.disconf.web.service.config.dao.ConfigDao;
import com.baidu.dsp.common.constant.DataFormatConstants;
import com.baidu.dsp.common.dao.AbstractDao;
import com.baidu.dsp.common.dao.Columns;
import com.baidu.dsp.common.form.RequestListBase.Page;
import com.baidu.dsp.common.utils.DaoUtils;
import com.baidu.ub.common.db.DaoPage;
import com.baidu.ub.common.db.DaoPageResult;
import com.baidu.unbiz.common.genericdao.operator.Match;
import com.baidu.unbiz.common.genericdao.operator.Modify;
import com.baidu.unbiz.common.genericdao.operator.Order;
import com.github.knightliao.apollo.utils.time.DateUtils;
import com.google.common.collect.Lists;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author liaoqiqi
 * @version 2014-6-16
 */
@Repository
public class ConfigDaoImpl extends AbstractDao<Long, Config> implements ConfigDao {
    
    private String SQL_SELECT_METAS = "select name, type, update_time from disconf.config where app_id=? and env_id=? and version=? and status=1";
    private String SQL_SELECT_META = "select name, type, update_time from disconf.config where app_id=? and env_id=? and version=? and name=? and status=1";

    /**
     *
     */
    @Override
    public Config getByParameter(Long appId, Long envId, String version, String key,
                                 DisConfigTypeEnum disConfigTypeEnum) {

        return findOne(new Match(Columns.APP_ID, appId), new Match(Columns.ENV_ID, envId),
                new Match(Columns.VERSION, version), new Match(Columns.TYPE, disConfigTypeEnum.getType()),
                new Match(Columns.NAME, key), new Match(Columns.STATUS, Constants.STATUS_NORMAL));
    }

    /**
     *
     */
    @Override
    public List<Config> getConfByAppEnv(Long appId, Long envId) {

        List<Order> orders = Lists.newArrayList(new Order(Columns.VERSION,false));
        List<Match> matches = Lists.newArrayList();
        if (envId == null) {
            matches.add(new Match(Columns.APP_ID, appId));
            matches.add(new Match(Columns.STATUS, Constants.STATUS_NORMAL));
            return find(matches,orders);
        } else {
            matches.add(new Match(Columns.APP_ID, appId));
            matches.add(new Match(Columns.ENV_ID, envId));
            matches.add(new Match(Columns.STATUS, Constants.STATUS_NORMAL));
            return find(matches,orders);
        }
    }


    @Override
    public List<Config> getConfByApp(Long appId) {

        List<Order> orders = Lists.newArrayList(new Order(Columns.VERSION,false));
        List<Match> matches = Lists.newArrayList();

        matches.add(new Match(Columns.APP_ID, appId));
        matches.add(new Match(Columns.STATUS, Constants.STATUS_NORMAL));
        return find(matches,orders);
    }

    /**
     *
     */
    @Override
    public DaoPageResult<Config> getConfigList(Long appId, Long envId, String version, Page page) {

        DaoPage daoPage = DaoUtils.daoPageAdapter(page);
        List<Match> matchs = new ArrayList<Match>();

        matchs.add(new Match(Columns.APP_ID, appId));

        matchs.add(new Match(Columns.ENV_ID, envId));

        matchs.add(new Match(Columns.VERSION, version));

        matchs.add(new Match(Columns.STATUS, Constants.STATUS_NORMAL));

        return page2(matchs, daoPage);
    }

    /**
     *
     */
    @Override
    public List<Config> getConfigList(Long appId, Long envId, String version) {

        List<Match> matchs = new ArrayList<Match>();
        matchs.add(new Match(Columns.APP_ID, appId));
        matchs.add(new Match(Columns.ENV_ID, envId));
        matchs.add(new Match(Columns.VERSION, version));
        matchs.add(new Match(Columns.STATUS, Constants.STATUS_NORMAL));

        return find(matchs, new ArrayList<Order>());
    }

    /**
     * @param configId
     */
    @Override
    public void deleteItem(Long configId) {
        String curTime = DateUtils.format(new Date(), DataFormatConstants.COMMON_TIME_FORMAT);
        List<Modify> modifyList = new ArrayList<Modify>();
        modifyList.add(modify(Columns.STATUS, Constants.STATUS_DELETE));
        modifyList.add(modify(Columns.UPDATE_TIME, curTime));

        update(modifyList, match(Columns.CONFIG_ID, configId));
    }

    /**
     *
     */
    @Override
    public void updateValue(Long configId, String value) {

        // 时间
        String curTime = DateUtils.format(new Date(), DataFormatConstants.COMMON_TIME_FORMAT);

        List<Modify> modifyList = new ArrayList<Modify>();
        modifyList.add(modify(Columns.VALUE, value));
        modifyList.add(modify(Columns.UPDATE_TIME, curTime));

        update(modifyList, match(Columns.CONFIG_ID, configId));
    }

    @Override
    public String getValue(Long configId) {
        Config config = get(configId);
        return config.getValue();
    }

    @Override
    public List<Config> getConfigMetas(Long appId, Long envId, String version, String name) {
        boolean isNameSpecified = name != null && !name.isEmpty();
        return this.findBySQL(isNameSpecified ? SQL_SELECT_META : SQL_SELECT_METAS,
                isNameSpecified ? Arrays.asList(appId, envId, version, name) : Arrays.asList(appId, envId, version),
                new RowMapper<Config>( ) {

                    @Override
                    public Config mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Config config = new Config( );
                        config.setName(rs.getString(Columns.NAME));
                        config.setType(rs.getInt(Columns.TYPE));
                        config.setUpdateTime(rs.getString(Columns.UPDATE_TIME));
                        return config;
                    }
            
        });
    }

    @Override
    public List<Config> getConfigItems(Long appId, Long envId, String version) {
        return find(new Match(Columns.APP_ID, appId), new Match(Columns.ENV_ID, envId),
                new Match(Columns.VERSION, version), new Match(Columns.TYPE, DisConfigTypeEnum.ITEM.getType()),
             new Match(Columns.STATUS, Constants.STATUS_NORMAL));
    }  
}
