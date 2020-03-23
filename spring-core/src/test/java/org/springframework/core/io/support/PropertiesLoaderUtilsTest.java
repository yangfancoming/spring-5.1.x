package org.springframework.core.io.support;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Administrator on 2020/3/23.
 * @ Description: PropertiesLoaderUtilsTest 工具类测试用例
 * @ author  山羊来了
 * @ date 2020/3/23---17:56
 */
public class PropertiesLoaderUtilsTest {


	@Test
	public void testLoadPropertiesResource() throws Exception {
		Properties ret = PropertiesLoaderUtils.loadProperties(new ClassPathResource("org/springframework/core/io/support/test.properties"));
		assertEquals("value", ret.getProperty("key"));
		assertEquals("中文", ret.getProperty("key2"));
	}

	@Test
	public void testFillProperties() throws Exception {
		Properties ret = new Properties();
		PropertiesLoaderUtils.fillProperties(ret,new ClassPathResource("org/springframework/core/io/support/test.properties"));
		System.out.println(ret);
		assertEquals("{key=value, key2=中文}", ret.toString());
	}
}
