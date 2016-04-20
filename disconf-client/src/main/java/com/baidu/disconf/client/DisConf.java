package com.baidu.disconf.client;

import java.io.File;

import com.baidu.disconf.client.common.model.DisconfCenterFile;

/**
 * 
 * @author tuwenjie
 *
 */
public class DisConf {
    
	public static final String UNIT_TEST_MODE_FLAG = "disconfUnitTestMode";
	
    /**
     * 如果需要，应用可以直接获取Disconf保存到本地的配置文件
     * @param fileName
     * @return
     */
    public static File getLocalConfig( String fileName ) {
        return new File(DisconfCenterFile.getFilePath(fileName));
    }
    
    /**
     * 设置Disconf为单元测试模式，配置文件不从远程加载/不监听Zookeeper，直接在classpath加载
     * 一般在测试类的静态初始化代码块里调用，示例：
     * <pre>
     * public class BaseTest {
	 * 	static {
     *		Disconf.setInUnitTestMode();
	 *	}
     * }
     * </pre>
     */
    public static void setInUnitTestMode( ) {
    	System.setProperty(UNIT_TEST_MODE_FLAG , "true");
    }

}
