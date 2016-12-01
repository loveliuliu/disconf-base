package com.baidu.disconf.client.common.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.baidu.disconf.client.common.constants.SupportFileTypeEnum;
import com.baidu.disconf.client.config.DisClientConfig;
import com.baidu.disconf.client.utils.AppTagHelper;
import com.baidu.disconf.core.common.utils.ClassLoaderUtil;
import com.baidu.disconf.core.common.utils.OsUtil;

/**
 * 配置文件表示
 *
 * @author liaoqiqi
 * @version 2014-5-20
 */
public class DisconfCenterFile extends DisconfCenterBaseModel {

    // -----key: 配置文件中的项名
    // -----value: 默认值
    private Map<String, FileItemValue> keyMaps = new HashMap<String, FileItemValue>();

    // 额外的配置数据，非注解式使用它来存储
    private Map<String, Object> additionalKeyMaps = new HashMap<String, Object>();

    // 配置文件类
    private Class<?> cls;

    // 文件名
    private String fileName;


    // 文件类型
    private SupportFileTypeEnum supportFileTypeEnum = SupportFileTypeEnum.ANY;

    public Class<?> getCls() {
        return cls;
    }

    public void setCls(Class<?> cls) {
        this.cls = cls;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Map<String, FileItemValue> getKeyMaps() {
        return keyMaps;
    }

    public void setKeyMaps(Map<String, FileItemValue> keyMaps) {
        this.keyMaps = keyMaps;
    }

    public Map<String, Object> getAdditionalKeyMaps() {
        return additionalKeyMaps;
    }

    public void setAdditionalKeyMaps(Map<String, Object> additionalKeyMaps) {
        this.additionalKeyMaps = additionalKeyMaps;
    }

    public SupportFileTypeEnum getSupportFileTypeEnum() {
        return supportFileTypeEnum;
    }

    public void setSupportFileTypeEnum(SupportFileTypeEnum supportFileTypeEnum) {
        this.supportFileTypeEnum = supportFileTypeEnum;
    }



    @Override
    public String toString() {
        return "\n\tDisconfCenterFile [\n\tkeyMaps=" + printKeyMaps(keyMaps,fileName) + "\n\tcls=" + cls + "\n\tfileName=" + fileName
                 +
                super.toString() + "]";
    }

    @Override
    public String infoString() {
        return "\n\tDisconfCenterFile [\n\tkeyMaps=" + printKeyMaps(keyMaps,fileName) + "\n" +
                "\tadditionalKeyMaps=" + additionalKeyMaps + "\n\tcls=" + cls + super.infoString() + "]";
    }

    private String printKeyMaps(Map<String, FileItemValue> keyMaps, String fileName) {
        if (AppTagHelper.USED_TAG_File_FIELDS.containsKey(getFilePath(fileName))) {
            Set<String> fields = AppTagHelper.USED_TAG_File_FIELDS.get(getFilePath(fileName));
            StringBuffer stringBuffer = new StringBuffer("keymaps={");
            for (Map.Entry<String, FileItemValue> entry : keyMaps.entrySet()) {
                stringBuffer.append(
                        entry.getKey() + "=" + entry.getValue().toEncryptString(fields.contains(entry.getKey())));
            }
            stringBuffer.append("}");
            return stringBuffer.toString();
        }
        return keyMaps.toString();
    }

    /**
     * 获取可以表示的KeyMap对
     */
    public Map<String, Object> getKV() {

        // 非注解式的
        if (keyMaps.size() == 0) {
            return additionalKeyMaps;
        }

        //
        // 注解式的
        //
        Map<String, Object> map = new HashMap<String, Object>();
        for (String key : keyMaps.keySet()) {
            map.put(key, keyMaps.get(key).getValue());
        }

        return map;
    }

    /**
     * 配置文件的本地完整路径
     */
    public String getFilePath( ) {

        return DisconfCenterFile.getFilePath(fileName);
    }
    
    public static String getFilePath( String fileName ) {
        if ( DisClientConfig.getInstance().unitTestMode) {
            try {
                return ClassLoaderUtil.getLoader().getResource(fileName).getFile();
            } catch ( Throwable t ) {
                throw new RuntimeException("Failed to load conf in classpath with unitTest mode:" + fileName, t);
            }
        } else {
            return OsUtil.pathJoin(getFileDir( ), fileName);
        }
    }

    /**
     * 配置文件的本地目录
     */
    public static String getFileDir() {
        return DisClientConfig.getInstance().userDefineDownloadDir;
    }
    
    
    public static void main(String[] args ) {
        DisClientConfig.getInstance().unitTestMode = true;
        System.out.println(DisconfCenterFile.getFilePath("disconf_sys.properties"));
    }

    /**
     * 配置文件Item项表示，包括了值，还有其类型
     *
     * @author liaoqiqi
     * @version 2014-6-16
     */
    public static class FileItemValue {

        private Object value;
        private Field field;
        private Method setMethod;

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Field getField() {
            return field;
        }

        public void setField(Field field) {
            this.field = field;
        }

        public Method getSetMethod() {
            return setMethod;
        }

        public void setSetMethod(Method setMethod) {
            this.setMethod = setMethod;
        }

        @Override
        public String toString() {
            return "FileItemValue{" +
                    "value=" + value +
                    ", field=" + field +
                    ", setMethod=" + setMethod +
                    '}';
        }

        public String toEncryptString(boolean isTag) {
            
            return "FileItemValue{" +
                    "value=" + (isTag ? "${}" : value) +
                    ", field=" + field +
                    ", setMethod=" + setMethod +
                    '}';
        }

        public FileItemValue(Object value, Field field) {
            super();
            this.value = value;
            this.field = field;
        }

        public FileItemValue(Object value, Field field, Method setMethod) {
            super();
            this.value = value;
            this.field = field;
            this.setMethod = setMethod;
        }
    }
}
