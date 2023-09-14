package org.dromara.common.mybatis.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * <p>
 * list转换string
 * </p>
 *
 * @author zsh
 * @since 2020/11/25
 */
public class ListToStringHandler extends BaseTypeHandler<List<Object>> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<Object> list, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, JSON.toJSONString(list));
    }

    @Override
    public List getNullableResult(ResultSet resultSet, String s) throws SQLException {
        JSONArray jsonArray = JSONArray.parseArray( resultSet.getString(s));
        return jsonArray;
    }

    @Override
    public List getNullableResult(ResultSet resultSet, int i) throws SQLException {
        JSONArray jsonArray = JSONArray.parseArray( resultSet.getString(i));
        return jsonArray;
    }

    @Override
    public List getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        JSONArray jsonArray = JSONArray.parseArray( callableStatement.getString(i));
        return jsonArray;
    }
}
