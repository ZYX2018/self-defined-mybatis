package config;

import org.apache.commons.dbcp.BasicDataSource;
import utils.LoadPropertiesUtil;

/**
 * @author moon
 *
 */
public class DataSourceConfig {

    private static  BasicDataSource source;


    /**
     * 配置数据源
     */
    private static synchronized void  configDataSource(){
        source = new BasicDataSource();
        source.setDriverClassName(LoadPropertiesUtil.getProperties("iMybatis.dataSource.driver"));
        source.setUrl(LoadPropertiesUtil.getProperties("iMybatis.dataSource.url"));
        source.setUsername(LoadPropertiesUtil.getProperties("iMybatis.dataSource.userName"));
        source.setPassword(LoadPropertiesUtil.getProperties("iMybatis.dataSource.password"));
    }

    public static BasicDataSource getSource(){
        configDataSource();
        return source;
    }


}
