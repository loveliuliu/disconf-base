package com.baidu.disconf.client.core.filetype;

import java.util.*;

import com.baidu.disconf.client.utils.AppTagHelper;
import com.baidu.disconf.client.utils.PatternUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.disconf.client.common.constants.SupportFileTypeEnum;
import com.baidu.disconf.client.core.filetype.impl.DisconfAnyFileProcessorImpl;
import com.baidu.disconf.client.core.filetype.impl.DisconfPropertiesProcessorImpl;
import com.baidu.disconf.client.core.filetype.impl.DisconfXmlProcessorImpl;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PropertyPlaceholderHelper;

/**
 * @author knightliao
 */
public class FileTypeProcessorUtils {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileTypeProcessorUtils.class);

    private static final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}", null, false);
    private static final MapPlaceholderConfigurerResolver resolver = new MapPlaceholderConfigurerResolver();
    private static final Object lock = new Object();
    /**
     * 输入文件名，返回其相应的k-v数据
     */
    public static Map<String, Object> getKvMap(SupportFileTypeEnum supportFileTypeEnum, String fileName)
        throws Exception {

        DisconfFileTypeProcessor disconfFileTypeProcessor;

        //
        // 获取数据
        //
        Map<String, Object> dataMap;

        if (supportFileTypeEnum.equals(SupportFileTypeEnum.PROPERTIES)) {

            disconfFileTypeProcessor = new DisconfPropertiesProcessorImpl();

        } else if (supportFileTypeEnum.equals(SupportFileTypeEnum.XML)) {

            disconfFileTypeProcessor = new DisconfXmlProcessorImpl();

        } else {

            disconfFileTypeProcessor = new DisconfAnyFileProcessorImpl();
        }

        dataMap = disconfFileTypeProcessor.getKvMap(fileName);

        if (dataMap == null) {
            dataMap = new HashMap<String, Object>();
        }

        //
        // 进行数据过滤
        //
        for (String key : dataMap.keySet()) {

            if (key == null) {
                continue;
            }

            LOGGER.debug(key + "\t" + dataMap.get(key));

            //替换标签 ${placeHolder}
            String value = (String)dataMap.get(key);
            if(StringUtils.isNotBlank(value)){
                List<String> matchedTagNames = PatternUtils.findMatchedTagNames(value);

                if(!CollectionUtils.isEmpty(matchedTagNames)){
                    for(String tagName : matchedTagNames){
                        if(!AppTagHelper.TAG_STORE.containsKey(tagName)){
                            throw new Exception("found tagName:" + tagName + ",but tagStore do not had it ");
                        }
                    }
                    String replacedValue = propertyPlaceholderHelper.replacePlaceholders(value, resolver);
                    dataMap.put(key,replacedValue);

                    //保存已使用标签的
                    if(AppTagHelper.USED_TAG_File_FIELDS.get(fileName) == null){
                        synchronized (lock){
                            if(AppTagHelper.USED_TAG_File_FIELDS.get(fileName) == null){
                                AppTagHelper.USED_TAG_File_FIELDS.put(fileName, Collections.synchronizedSet(new HashSet<String>()));
                            }
                        }
                    }
                    ((Set)AppTagHelper.USED_TAG_File_FIELDS.get(fileName)).add(key);

                }
            }
        }

        return dataMap;
    }

    private static class MapPlaceholderConfigurerResolver implements PropertyPlaceholderHelper.PlaceholderResolver {
        @Override
        public String resolvePlaceholder(String placeholderName) {
            return AppTagHelper.TAG_STORE.get(placeholderName);
        }
    }

}
