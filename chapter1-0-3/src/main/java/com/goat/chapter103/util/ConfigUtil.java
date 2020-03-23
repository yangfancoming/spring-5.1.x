package com.goat.chapter103.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by Administrator on 2020/3/20.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/20---14:54
 */
public class ConfigUtil {

    /**
     * 读取配置文件
     * @param file  "config.properties"
     */
    public static Properties getProperties(String file){
        InputStream is = ConfigUtil.class.getClassLoader().getResourceAsStream(file);
        BufferedReader br= new BufferedReader(new InputStreamReader(is));
        Properties props = new Properties();
        try {
            props.load(br);
            return props;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
