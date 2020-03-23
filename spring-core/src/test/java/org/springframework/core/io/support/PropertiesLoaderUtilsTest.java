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

	@Test
	public void testLoadAllPropertiesString() throws Exception {
		Properties ret = PropertiesLoaderUtils.loadAllProperties("test.properties");
		assertEquals("value", ret.getProperty("key3"));
		assertEquals("value", ret.getProperty("key"));
	}
}
