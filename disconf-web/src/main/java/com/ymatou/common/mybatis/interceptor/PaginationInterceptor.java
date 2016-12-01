package com.ymatou.common.mybatis.interceptor;

import com.baidu.ub.common.commons.ThreadContext;
import com.ymatou.common.mybatis.util.Reflections;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Intercepts({
        @Signature(type = Executor.class, method = "query",args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class PaginationInterceptor extends BaseInterceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object target = invocation.getTarget();
        if(target instanceof Executor){ //获取分页总数，更改分页sql
            final MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

            Object parameter = invocation.getArgs()[1];
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Object parameterObject = boundSql.getParameterObject();

            //获取分页参数对象
            Pageable pageable = getPageable(parameterObject);

            //如果设置了分页对象，则进行分页
            if (pageable != null && pageable.getPageSize() != -1) {

                if (StringUtils.isBlank(boundSql.getSql())){
                    return null;
                }
                String originalSql = boundSql.getSql().trim();
                Connection connection = mappedStatement.getConfiguration().getEnvironment().getDataSource().getConnection();
                //得到总记录数
                long totalRecord = SQLHelper.getCount(DIALECT,originalSql,connection,mappedStatement,parameterObject,boundSql,log);
                if(ThreadContext.getContext() == null){
                    ThreadContext.init();
                }
                ThreadContext.putContext("page_totalRecord",totalRecord);
                //分页查询 本地化对象 修改数据库注意修改实现
                String pageSql = SQLHelper.generatePageSql(originalSql, pageable, DIALECT);
                if (log.isDebugEnabled()) {
                    log.debug("PAGE SQL: " + StringUtils.replace(pageSql, "\n", ""));
                }
                invocation.getArgs()[2] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
                BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
                //解决MyBatis 分页foreach 参数失效 start
                if (Reflections.getFieldValue(boundSql, "metaParameters") != null) {
                    MetaObject mo = (MetaObject) Reflections.getFieldValue(boundSql, "metaParameters");
                    Reflections.setFieldValue(newBoundSql, "metaParameters", mo);
                }
                //解决MyBatis 分页foreach 参数失效 end
                MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));

                if (log.isDebugEnabled()) {
                    log.debug("Generate SQL : \n\r " + newBoundSql.getSql());
                }

                invocation.getArgs()[0] = newMs;
            }
        }else if(target instanceof ResultSetHandler){ //更改返回值类型

            DefaultResultSetHandler resultSetHandler = (DefaultResultSetHandler) invocation.getTarget();
            MetaObject metaResultSetHandler = MetaObject.forObject(resultSetHandler, new DefaultObjectFactory(), new DefaultObjectWrapperFactory());
            try {
                ParameterHandler parameterHandler = (ParameterHandler) metaResultSetHandler.getValue("parameterHandler");
                Object parameterObject = parameterHandler.getParameterObject();

                Pageable pageable = getPageable(parameterObject);

                if ( pageable != null && pageable.getPageSize() != -1 ) {

                    long totalRecord = ThreadContext.getContext("page_totalRecord");

                    Object result = invocation.proceed();
                    Page page = new PageImpl((List)result, pageable, totalRecord);

                    // 设置返回值
                    List<Page> pageList = new ArrayList<Page>();
                    pageList.add(page);

                    ThreadContext.getContext().remove("page_totalRecord");

                    return pageList;
                }
            } catch (Exception e) {
                throw new Exception("Overwrite SQL : Fail!",e);
            }

        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        super.initProperties(properties);
    }



    private MappedStatement copyFromMappedStatement(MappedStatement ms,
                                                    SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(),
                ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null) {
            for (String keyProperty : ms.getKeyProperties()) {
                builder.keyProperty(keyProperty);
            }
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.cache(ms.getCache());
        return builder.build();
    }

    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
