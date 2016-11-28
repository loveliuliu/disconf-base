package com.baidu.disconf.web.service.config.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.core.common.json.ValueVo;
import com.baidu.disconf.web.service.config.bo.Config;

public class ConfigUtils {

    /**
     * @param errorMsg
     *
     * @return
     */
    public static ValueVo getErrorVo(String errorMsg) {

        ValueVo confItemVo = new ValueVo();
        confItemVo.setStatus(Constants.NOTOK);
        confItemVo.setValue("");
        confItemVo.setMessage(errorMsg);

        return confItemVo;
    }
    
    public static String getMetasJson( List<Config> configs ) {
        if ( configs == null || configs.isEmpty()) {
            return "";
        }
        List<Map<String, String>> metas = new ArrayList<Map<String, String>>( );
        for ( Config config : configs ) {
            Map<String, String> map = new HashMap<String, String>( );
            map.put("name", config.getName());
            map.put("type", config.getType() == null ? null : "" + config.getType() );
            map.put("updateTime", config.getUpdateTime());
            metas.add(map);
        }
        com.google.gson.Gson gson = new com.google.gson.Gson();
        return gson.toJson(metas);
    }
    
    public static String getValuesJson( List<Config> configs ) {
        if ( configs == null || configs.isEmpty()) {
            return "";
        }
        List<Map<String, String>> values = new ArrayList<Map<String, String>>( );
        for ( Config config : configs ) {
            Map<String, String> map = new HashMap<String, String>( );
            map.put("name", config.getName());
            map.put("value",config.getValue());
            values.add(map);
        }
        com.google.gson.Gson gson = new com.google.gson.Gson();
        return gson.toJson(values);
    }
}
