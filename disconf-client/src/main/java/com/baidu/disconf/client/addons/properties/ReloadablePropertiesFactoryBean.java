package com.baidu.disconf.client.addons.properties;

import com.baidu.disconf.client.DisconfMgr;
import com.baidu.disconf.client.common.model.DisconfCenterFile;
import com.baidu.disconf.client.core.filetype.FileTypeProcessorUtils;
import com.baidu.disconf.client.utils.AppTagHelper;
import com.baidu.disconf.client.utils.PatternUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A properties factory bean that creates a reconfigurable Properties object.
 * When the Properties' reloadConfiguration method is called, and the file has
 * changed, the properties are read again from the file.
 * <p/>
 * 真正的 reload bean 定义，它可以定义多个 resource 为 reload config file
 */
public class ReloadablePropertiesFactoryBean extends PropertiesFactoryBean implements DisposableBean,
        ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}", null, false);
    private static final FileTypeProcessorUtils.MapPlaceholderConfigurerResolver resolver = new FileTypeProcessorUtils.MapPlaceholderConfigurerResolver();
    protected static final Logger log = LoggerFactory.getLogger(ReloadablePropertiesFactoryBean.class);

    private Resource[] locations;
    private long[] lastModified;
    private List<IReloadablePropertiesListener> preListeners;

    /**
     * 定义资源文件
     *
     * @param fileNames
     */
    public void setLocation(final String fileNames)throws Exception {
        List<String> list = new ArrayList<String>();
        list.add(fileNames);
        setLocations(list);
    }

    /**
     */
    public void setLocations(List<String> fileNames)throws Exception {

        List<Resource> resources = new ArrayList<Resource>();
        for (String fileName : fileNames) {

            // trim
            fileName = fileName.trim();

            //
            // register to disconf
            //
            try {
                DisconfMgr.getInstance().reloadableScan(fileName);
            } catch (Throwable t ) {
                throw new RuntimeException("Failed to load config in DisConf NonAnnotation Mode:" + fileName, t);
            }

            //
            // only properties will reload
            //
            String ext = FilenameUtils.getExtension(fileName);
            if (ext.equals("properties")) {
                Resource resource = new FileSystemResource(DisconfCenterFile.getFilePath(fileName));

                String fileStr = StreamUtils.copyToString(resource.getInputStream(), Charset.forName("UTF-8"));
                List<String> matchedTagNames = PatternUtils.findMatchedTagNames(fileStr);
                if(!CollectionUtils.isEmpty(matchedTagNames)){
                    for(String tagName : matchedTagNames){
                        if(!AppTagHelper.TAG_STORE.containsKey(tagName)){
                            throw new RuntimeException(("found tagName:" + tagName + ",but tagStore do not had it "));
                        }
                    }
                    String replacedValue = propertyPlaceholderHelper.replacePlaceholders(fileStr, resolver);
                    resource = new ByteArrayResource(replacedValue.getBytes(Charset.forName("UTF-8")));
                }

                resources.add(resource);
            }
        }

        this.locations = resources.toArray(new Resource[resources.size()]);
        lastModified = new long[locations.length];
        super.setLocations(locations);
    }

    protected Resource[] getLocations() {
        return locations;
    }

    /**
     * listener , 用于通知回调
     *
     * @param listeners
     */
    public void setListeners(final List listeners) {
        // early type check, and avoid aliassing
        this.preListeners = new ArrayList<IReloadablePropertiesListener>();
        for (Object o : listeners) {
            preListeners.add((IReloadablePropertiesListener) o);
        }
    }

    private ReloadablePropertiesBase reloadableProperties;

    /**
     * @return
     *
     * @throws IOException
     */
    @Override
    protected Properties createProperties() throws IOException {

        return (Properties) createMyInstance();
    }

    /**
     * createInstance 废弃了
     *
     * @throws IOException
     */
    protected Object createMyInstance() throws IOException {
        // would like to uninherit from AbstractFactoryBean (but it's final!)
        if (!isSingleton()) {
            throw new RuntimeException("ReloadablePropertiesFactoryBean only works as singleton");
        }

        // set listener
        reloadableProperties = new ReloadablePropertiesImpl();
        if (preListeners != null) {
            reloadableProperties.setListeners(preListeners);
        }

        // reload
        reload(true);

        // add for monitor
        ReloadConfigurationMonitor.addReconfigurableBean((ReconfigurableBean) reloadableProperties);

        return reloadableProperties;
    }

    public void destroy() throws Exception {
        reloadableProperties = null;
    }

    /**
     * 根据修改时间来判定是否reload
     *
     * @param forceReload
     *
     * @throws IOException
     */
    protected void reload(final boolean forceReload) throws IOException {

        boolean reload = forceReload;
        for (int i = 0; i < locations.length; i++) {
            Resource location = locations[i];
            File file;

            try {
                file = location.getFile();
            } catch (IOException e) {
                // not a file resource
                // may be spring boot
                log.warn(e.toString());
                continue;
            }
            try {
                long l = file.lastModified();

                if (l > lastModified[i]) {
                    lastModified[i] = l;
                    reload = true;
                }
            } catch (Exception e) {
                // cannot access file. assume unchanged.
                if (log.isDebugEnabled()) {
                    log.debug("can't determine modification time of " + file + " for " + location, e);
                }
            }
        }
        if (reload) {
            doReload();
        }
    }

    /**
     * 设置新的值
     *
     * @throws IOException
     */
    private void doReload() throws IOException {
        reloadableProperties.setProperties(mergeProperties());
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 回调自己
     */
    class ReloadablePropertiesImpl extends ReloadablePropertiesBase implements ReconfigurableBean {

        // reload myself
        public void reloadConfiguration() throws Exception {
            ReloadablePropertiesFactoryBean.this.reload(false);
        }
    }

}
