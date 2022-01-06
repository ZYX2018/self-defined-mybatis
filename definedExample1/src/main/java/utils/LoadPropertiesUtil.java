package utils;


import config.DataSourceConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoadPropertiesUtil {
    private static final Properties properties = new Properties();

    static {
        InputStream inputStream = null;
        try {
            inputStream = DataSourceConfig.class.getClassLoader().getResourceAsStream("application.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 加载配置文件
     */
    public static String getProperties(String key){
        return  properties.getProperty(key);
    }



}
