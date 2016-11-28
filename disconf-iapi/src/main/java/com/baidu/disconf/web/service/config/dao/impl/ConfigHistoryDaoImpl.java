package com.baidu.disconf.web.service.config.dao.impl;

import com.baidu.disconf.web.service.config.bo.ConfigHistory;
import com.baidu.disconf.web.service.config.dao.ConfigHistoryDao;
import com.baidu.dsp.common.dao.AbstractDao;
import org.springframework.stereotype.Repository;

/**
 * Created by knightliao on 15/12/25.
 */
@Repository
public class ConfigHistoryDaoImpl extends AbstractDao<Long, ConfigHistory> implements ConfigHistoryDao {
}
