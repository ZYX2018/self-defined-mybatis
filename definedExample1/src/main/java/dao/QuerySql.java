package dao;

import config.DataSourceConfig;
import lombok.extern.java.Log;
import org.junit.Test;
import utils.LoadPropertiesUtil;
import utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Log
public class QuerySql {



    @Test
    public void test() throws ClassNotFoundException, SQLException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String sql = LoadPropertiesUtil.getProperties("iMybatis.preparedSql.List.Student");
        String clazz = LoadPropertiesUtil.getProperties("iMybatis.resultType");
        log.info("sql:"+sql+",clazz:"+clazz);
        List<String> params = new LinkedList<>();
        params.add("张三丰");
        List res = selectAsList(sql,QuerySql.class.getClassLoader().loadClass(clazz),params);
        log.info(res.toString());
    }

    public <T> T select(String preparedSql,Class<T> clazz , List params) throws SQLException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<T> resultList = selectAsList(preparedSql,clazz,params);
        return resultList.size()>0?resultList.get(0):null;
    }

    public <T> List<T> selectAsList(String preparedSql,Class<T> clazz , List params) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
       List<T> res = new LinkedList<>();
        ResultSet resultSet = listResult(preparedSql,params);
        //解析结果
        while (resultSet.next()){
            T t= clazz.getDeclaredConstructor().newInstance();
            ResultSetMetaData metaData=resultSet.getMetaData();
            int count=metaData.getColumnCount();
            for (int i=1;i<=count;i++){
                String columName=metaData.getColumnName(i);
                String replacePrefix = LoadPropertiesUtil.getProperties("iMybatis.replacePrefix");
                if (!StringUtils.isEmpty(replacePrefix)){
                    columName = columName.replaceFirst(replacePrefix,"");
                }
                //是否开启驼峰转换
                boolean humpConversion = Boolean.parseBoolean(LoadPropertiesUtil.getProperties("iMybatis.enableHumpConversion"));
                if (humpConversion){
                    columName = columName.toLowerCase();
                    columName = StringUtils.underlineToHump(columName);
                }
                Field field= clazz.getDeclaredField(columName);
                if (field!=null){
                    field.setAccessible(true);
                    Object columValue = resultSet.getObject(i);
                    field.set(t,columValue);
                }
            }
            res.add(t);
        }
        return res;
    }

    private  ResultSet listResult(String preparedSql ,  List params) throws SQLException {
        //获得链接
        Connection connection =  DataSourceConfig.getSource().getConnection();
        PreparedStatement statement = connection.prepareStatement(preparedSql);
        AtomicInteger index = new AtomicInteger(1);
        params.forEach(param->{
            try {
                statement.setObject(index.getAndIncrement(),param);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ResultSet   resultSet=statement.executeQuery();
        return resultSet;
    }


}
