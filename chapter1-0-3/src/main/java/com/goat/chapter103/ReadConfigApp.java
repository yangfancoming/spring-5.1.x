package com.goat.chapter103;

import com.goat.chapter103.util.ConfigUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Administrator on 2020/3/23.
 *
 * @ Description: java原生方式读取配置文件
 * @ author  山羊来了
 * @ date 2020/3/23---20:16
 */
public class ReadConfigApp {

	// java原生 方式读取配置文件
	@Test
	public void test(){
		Properties properties = ConfigUtil.getProperties("config.properties");
		Assert.assertEquals("{salt=123, q=goat, to=zh, from=en}",properties.toString());
	}

	// Spring 方式读取配置文件
	@Test
	public void testFillProperties() throws IOException {
		Properties ret = new Properties();
		PropertiesLoaderUtils.fillProperties(ret,new ClassPathResource("config.properties"));
		Assert.assertEquals("{salt=123, q=goat, to=zh, from=en}",ret.toString());

	}
}
