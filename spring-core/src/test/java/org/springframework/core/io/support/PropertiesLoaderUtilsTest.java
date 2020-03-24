package org.springframework.core.io.support;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Administrator on 2020/3/23.
 * @ Description: PropertiesLoaderUtilsTest 工具类测试用例
 * @ author  山羊来了
 * @ date 2020/3/23---17:56
 */
public class PropertiesLoaderUtilsTest {

	private final static String PROPERTIES = "org/springframework/core/io/support/test.properties";
	private final static String XML = "org/springframework/core/io/support/test.xml";

	@Test
	public void testLoadPropertiesResource() throws Exception {
		Properties ret = PropertiesLoaderUtils.loadProperties(new ClassPathResource(PROPERTIES));
		assertEquals("value", ret.getProperty("key"));
		assertEquals("中文", ret.getProperty("key2"));
	}

	@Test
	public void testFillProperties() throws Exception {
		Properties ret = new Properties();
		PropertiesLoaderUtils.fillProperties(ret,new ClassPathResource(PROPERTIES));
		assertEquals("{key=value, key2=中文}", ret.toString());
	}

	@Test
	public void testLoadPropertiesResourceXml() throws Exception {
		Properties ret = PropertiesLoaderUtils.loadProperties(new ClassPathResource(XML));
		assertEquals("value", ret.getProperty("key"));
		assertEquals("中文", ret.getProperty("key2"));
	}

	@Test
	public void testLoadPropertiesEncodedResource() throws Exception {
//		Properties ret = PropertiesLoaderUtils.loadProperties(new EncodedResource(new ClassPathResource(PROPERTIES),"UTF-8"));
		Properties ret = PropertiesLoaderUtils.loadProperties(new EncodedResource(new ClassPathResource(PROPERTIES), StandardCharsets.UTF_8));
		assertEquals("value", ret.getProperty("key"));
		assertEquals("中文", ret.getProperty("key2"));
	}

	/**
	 * 	loadProperties方法，从当前classpath下加载properties文件，如果使用loadAllProperties，可以从当前classpath下加载所有的相同名称的properties文件，并执行合并。
	 * 	在 src/main/resources 和 src/test/resources 目录下都放入test.properties文件 分别写入内容 key3=value3  key=value
	 * 	运行结果发现 loadAllProperties 将两个test.properties配置文件的内容合并了
	*/
	@Test
	public void testLoadAllPropertiesString() throws Exception {
		Properties ret = PropertiesLoaderUtils.loadAllProperties("test.properties");
		assertEquals("value3", ret.getProperty("key3"));
		assertEquals("value", ret.getProperty("key"));
		// 遇到 key2 重复key 以main为准
		assertEquals("value2main", ret.getProperty("key2"));
		// 只能加载 test目录下的 test.properties 配置文件 (加载不到main目录下的)
		Properties ret2 = PropertiesLoaderUtils.loadProperties(new EncodedResource(new ClassPathResource("test.properties"), StandardCharsets.UTF_8));
		System.out.println(ret2);
	}
}
