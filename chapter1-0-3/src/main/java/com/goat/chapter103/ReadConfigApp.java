package com.goat.chapter103;

import com.goat.chapter103.util.ConfigUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * Created by Administrator on 2020/3/23.
 *
 * @ Description: java原生方式读取配置文件
 * @ author  山羊来了
 * @ date 2020/3/23---20:16
 */
public class ReadConfigApp {

	@Test
	public void test(){
		Properties properties = ConfigUtil.getProperties("config.properties");
		Assert.assertEquals("{salt=123, q=goat, to=zh, from=en}",properties.toString());
	}
}
