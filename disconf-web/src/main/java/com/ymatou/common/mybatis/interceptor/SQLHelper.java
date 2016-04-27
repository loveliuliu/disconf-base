package com.ymatou.common.mybatis.interceptor;


import com.ymatou.common.mybatis.dialect.Dialect;
import com.ymatou.common.mybatis.dialect.db.OracleDialect;
import com.ymatou.common.mybatis.util.Reflections;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SQLHelper {
	
    /**
     * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.DefaultParameterHandler
     *
     * @param ps              表示预编译的 SQL 语句的对象。
     * @param mappedStatement MappedStatement
     * @param boundSql        SQL
     * @param parameterObject 参数对象
     * @throws SQLException 数据库异常
     */
    @SuppressWarnings("unchecked")
    public static void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null :
                    configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX) && boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }
                    @SuppressWarnings("rawtypes")
					TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement " + mappedStatement.getId());
                    }
                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }


    /**
     * 查询总纪录数
     * @param dialect
     * @param connection
     * @param sql
     * @return
     */
    public static Long getCount(Dialect dialect,final String sql, final Connection connection,
                                final MappedStatement mappedStatement, final Object parameterObject,
                                final BoundSql boundSql, Log log)throws SQLException {

		final String countSql;
		if(dialect instanceof OracleDialect){
			countSql = "select count(1) from (" + sql + ") tmp_count";
		}else{
			countSql = "select count(1) from (" + removeOrders(sql) + ") tmp_count";
		}
        Connection conn = connection;
        PreparedStatement ps = null;
        ResultSet rs = null;
        long count = 0L;
        try {
            if (log.isDebugEnabled()) {
                log.debug("COUNT SQL: " + StringUtils.replaceEach(countSql, new String[]{"\n","\t"}, new String[]{" "," "}));
            }
            ps = conn.prepareStatement(countSql);
            BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql,
                    boundSql.getParameterMappings(), parameterObject);
            //解决MyBatis 分页foreach 参数失效 start
            if (Reflections.getFieldValue(boundSql, "metaParameters") != null) {
                MetaObject mo = (MetaObject) Reflections.getFieldValue(boundSql, "metaParameters");
                Reflections.setFieldValue(countBS, "metaParameters", mo);
            }
            //解决MyBatis 分页foreach 参数失效 end
            SQLHelper.setParameters(ps, mappedStatement, countBS, parameterObject);
            rs = ps.executeQuery();

            if (rs.next()) {
                count = (Long) JdbcUtils.getResultSetValue(rs, 1, Long.class);
            }
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(ps);
            JdbcUtils.closeConnection(conn);
        }
        return count;
    }


    /**
     * 根据数据库方言，生成特定的分页sql
     * @param sql     Mapper中的Sql语句
     * @param pageable    分页对象
     * @param dialect 方言类型
     * @return 分页SQL
     */
    public static String generatePageSql(String sql, Pageable pageable, Dialect dialect) {
        sql = addOrder(sql,pageable);
        if (dialect.supportsLimit()) {
            return dialect.getLimitString(sql, pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize());
        } else {
            return sql;
        }
    }

    /**
     * 去除qlString的select子句。
     * @return
     */
    @SuppressWarnings("unused")
	private static String removeSelect(String qlString){
        int beginPos = qlString.toLowerCase().indexOf("from");
        return qlString.substring(beginPos);
    }

    /**
     * 去除hql的orderBy子句。
     * @return
     */
    @SuppressWarnings("unused")
	private static String removeOrders(String qlString) {
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(qlString);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 增加order by
     * @param qlString
     * @param pageable
     * @return
     */
    public static String addOrder(String qlString,Pageable pageable){

        Sort sort = pageable.getSort();
        if(null != sort){
            qlString += " order by ";
            Iterator<Sort.Order> iterator = sort.iterator();
            int i = 0;
            while (iterator.hasNext()){
                Sort.Order order = iterator.next();
                if(i !=0 ){
                    qlString += ",";
                }
                qlString += order.getProperty() + " "+ order.getDirection().name();
            }
        }
        return qlString;
    }
}
