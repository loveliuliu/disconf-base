package com.ymatou.common.mybatis.interceptor;


import com.ymatou.common.mybatis.dialect.Dialect;
import com.ymatou.common.mybatis.dialect.db.*;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.Properties;


public abstract class BaseInterceptor implements Interceptor, Serializable {
	
	private static final long serialVersionUID = 1L;

    
    protected static final String DELEGATE = "delegate";

    protected static final String MAPPED_STATEMENT = "mappedStatement";

    protected Log log = LogFactory.getLog(this.getClass());

    protected Dialect DIALECT;


    /**
     * 获取分页信息
     * @param parameterObject 参数对象
     * @return 分页对象
     */
    @SuppressWarnings("unchecked")
	protected static Pageable getPageable(Object parameterObject) {
    	try{
            if(null == parameterObject){
                return null;
            }
            if(parameterObject instanceof MapperMethod.ParamMap){

                MapperMethod.ParamMap paramMapObject = (MapperMethod.ParamMap)parameterObject ;


                if(paramMapObject != null){
                    for(Object key : paramMapObject.keySet()){
                        if(paramMapObject.get(key) instanceof  Pageable){
                           return (Pageable) paramMapObject.get(key);
                        }
                    }
                }
            }
    	}catch (Exception e) {
		}
        return null;
    }

    /**
     * 设置属性，支持自定义方言类和制定数据库的方式
     * <code>dialectClass</code>,自定义方言类。可以不配置这项
     * <ode>dbms</ode> 数据库类型，插件支持的数据库
     * <code>sqlPattern</code> 需要拦截的SQL ID
     * @param p 属性
     */
    protected void initProperties(Properties p) {
        Dialect dialect = null;
        String dbType = p.getProperty("dialect");
        if ("db2".equals(dbType)){
        	dialect = new DB2Dialect();
        }else if("derby".equals(dbType)){
        	dialect = new DerbyDialect();
        }else if("h2".equals(dbType)){
        	dialect = new H2Dialect();
        }else if("hsql".equals(dbType)){
        	dialect = new HSQLDialect();
        }else if("mysql".equals(dbType)){
        	dialect = new MySQLDialect();
        }else if("oracle".equals(dbType)){
        	dialect = new OracleDialect();
        }else if("postgre".equals(dbType)){
        	dialect = new PostgreSQLDialect();
        }else if("mssql".equals(dbType) || "sqlserver".equals(dbType)){
        	dialect = new SQLServer2005Dialect();
        }else if("sybase".equals(dbType)){
        	dialect = new SybaseDialect();
        }
        if (dialect == null) {
            throw new RuntimeException("mybatis dialect error.");
        }
        DIALECT = dialect;
//        _SQL_PATTERN = p.getProperty("sqlPattern");
//        _SQL_PATTERN = Global.getConfig("mybatis.pagePattern");
//        if (StringUtils.isEmpty(_SQL_PATTERN)) {
//            throw new RuntimeException("sqlPattern property is not found!");
//        }
    }
}
