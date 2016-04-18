package com.ymatou.common.mybatis.typehandler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by luoshiqian on 2016/4/15.
 */
public class SerializableTypeHandler implements TypeHandler<Object>{


    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        if( null != parameter){
            preparedStatement.setObject(i,parameter);
        }
    }

    @Override
    public Object getResult(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getObject(columnName);
    }

    @Override
    public Object getResult(ResultSet resultSet, int index) throws SQLException {
        return resultSet.getObject(index);
    }

    @Override
    public Object getResult(CallableStatement callableStatement, int index) throws SQLException {
        return callableStatement.getObject(index);
    }

}
