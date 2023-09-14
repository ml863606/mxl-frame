package org.dromara.common.mybatis.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>
 * list转换string
 * </p>
 *
 * @author zsh
 * @since 2020/11/25
 */
public class JSONObjectToStringHandler extends BaseTypeHandler<JSONObject> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, JSONObject obj, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, JSON.toJSONString(obj));
    }

    @Override
    public JSONObject getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return JSON.parseObject(resultSet.getString(s));
    }

    @Override
    public JSONObject getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return JSON.parseObject(resultSet.getString(i));
    }

    @Override
    public JSONObject getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return JSON.parseObject(callableStatement.getString(i));
    }
}
