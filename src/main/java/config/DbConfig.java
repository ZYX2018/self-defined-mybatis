package config;

import dao.model.Student;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * mysql数据源配置
 * @author MOON
 * 2020-09-25
 */
public class DbConfig {

    private final Properties properties=new Properties();

    private PreparedStatement preparedStatement=null;

    private ResultSet resultSet=null;

    private BasicDataSource source;


    @Test
    public  void  test(){
        loadProperties("Data.properties");
        configSource();
        Map<String,Object> map=new HashMap<>();
        map.put("name","张三丰");
        System.out.println(selectList(Student.class,map));
    }

    /**
     * 配置数据源
     */
    private void configSource(){
        source=new BasicDataSource();
        source.setDriverClassName(properties.getProperty("db.mysql.driver"));
        source.setUrl(properties.getProperty("db.mysql.url"));
        source.setUsername(properties.getProperty("db.mysql.userName"));
        source.setPassword(properties.getProperty("db.mysql.password"));
    }

    /**
     *
     * @return 获得链接
     * @throws SQLException 数据库链接异常
     */
    private Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    /**
     *   业务方法
     * @param calssz po的类型
     * @param <T> result的返回类型
     * @return 返回集合
     */
    private <T> List<T> selectList(Class<T> calssz , Object params){
        List<T> list =new ArrayList<>();
        try {
            Connection connection =getConnection();
            //
            String sql=properties.getProperty("query.sql.List."+calssz.getSimpleName());
            preparedStatement=connection.prepareStatement(sql);

            if(params instanceof Integer){
                preparedStatement.setObject(1,params);
            }else if(params instanceof String){
                preparedStatement.setObject(1,params);
            }else if(params instanceof Double){
                preparedStatement.setObject(1,params);
            }else  if(params instanceof Map){
                Map map= (Map) params;
                Iterator<Map.Entry> iterator=map.entrySet().iterator();
                for (int i=1;iterator.hasNext();i++){
                    preparedStatement.setObject(i,iterator.next().getValue());
                }
            }
            resultSet=preparedStatement.executeQuery();
            //
            while (resultSet.next()){
                T t= calssz.newInstance();
                ResultSetMetaData metaData=resultSet.getMetaData();
                int count=metaData.getColumnCount();
                for (int i=1;i<=count;i++){
                    String columName=metaData.getColumnName(i);
                    Field field= calssz.getDeclaredField(columName);
                    field.setAccessible(true);
                    field.set(t,resultSet.getObject(i));
                }
                list.add(t);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return list;
    }


    public void loadProperties(String name) {

        InputStream inputStream=null;
        try {
            inputStream=DbConfig.class.getClassLoader().getResourceAsStream(name);
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (inputStream!=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
