package org.springframework.beans.factory.config;
import java.util.Properties;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.CollectionUtils;
import static org.junit.Assert.*;
import static org.springframework.tests.TestResourceUtils.*;

/**
 * Unit tests for {@link PropertiesFactoryBean}.
 * @since 01.11.2003
 */
public class PropertiesFactoryBeanTests {

	private static final Class<?> CLASS = PropertiesFactoryBeanTests.class;
	// 		PropertiesFactoryBeanTests-test.properties
	private static final Resource TEST_PROPS = qualifiedResource(CLASS, "test.properties");
	// 		PropertiesFactoryBeanTests-test.properties.xml
	private static final Resource TEST_PROPS_XML = qualifiedResource(CLASS, "test.properties.xml");

	PropertiesFactoryBean pfb = new PropertiesFactoryBean();
	
	/**
	 * 测试 properties 文件 使用 setLocation 方式
	 * @see PropertiesLoaderSupport#loadProperties(java.util.Properties)
	 * @see PropertiesLoaderUtils#fillProperties(java.util.Properties, org.springframework.core.io.support.EncodedResource, org.springframework.util.PropertiesPersister)
	 * @see PropertiesLoaderSupport#mergeProperties() 函数中的第2个if
	*/
	@Test
	public void testWithPropertiesFile() throws Exception {
		pfb.setLocation(TEST_PROPS);
		// 将 setLocation 的文件内容加载到 PropertiesFactoryBean 的 singletonInstance（Properties） 属性中
		pfb.afterPropertiesSet();
		Properties props = pfb.getObject();
		assertEquals("99", props.getProperty("tb.array[0].age"));
	}

	/**
	 * 测试 xml 文件 使用 setLocation 方式
	 * @see PropertiesLoaderSupport#mergeProperties() 函数中的第3个if
	 */
	@Test
	public void testWithPropertiesXmlFile() throws Exception {
		pfb.setLocation(TEST_PROPS_XML);
		pfb.afterPropertiesSet();
		Properties props = pfb.getObject();
		assertEquals("99", props.getProperty("tb.array[0].age"));
	}

	/**
	 *  使用 setProperties 方式
	 * @see PropertiesLoaderSupport#mergeProperties() 函数中的第2个if
	 * @see CollectionUtils#mergePropertiesIntoMap(java.util.Properties, java.util.Map)
	*/
	@Test
	public void testWithLocalProperties() throws Exception {
		Properties localProps = new Properties();
		localProps.setProperty("key2", "value2");
		pfb.setProperties(localProps);
		pfb.afterPropertiesSet();
		Properties props = pfb.getObject();
		assertEquals("value2", props.getProperty("key2"));
	}

	/**
	 * 测试 文件和Properties方式同时使用，文件的优先级最高，因为第3个if 是读取的文件内容
	 * tb.array[0].age=99
	 * tb.list[1].name=test
	*/
	@Test
	public void testWithPropertiesFileAndLocalProperties() throws Exception {
		pfb.setLocation(TEST_PROPS);
		Properties localProps = new Properties();
		localProps.setProperty("key2", "value2");
		localProps.setProperty("tb.array[0].age", "0");
		pfb.setProperties(localProps);
		pfb.afterPropertiesSet();
		Properties props = pfb.getObject();
		// 验证 文件内容 覆盖掉了 本地 Properties 的内容
		assertEquals("99", props.getProperty("tb.array[0].age"));
		assertEquals("value2", props.getProperty("key2"));
	}

	/**
	 * 测试 文件和 setPropertiesArray 方式同时使用，文件的优先级最高，因为第3个if 是读取的文件内容
	 * tb.array[0].age=99
	 * tb.list[1].name=test
	 */
	@Test
	public void testWithPropertiesFileAndMultipleLocalProperties() throws Exception {
		pfb.setLocation(TEST_PROPS);
		Properties props1 = new Properties();
		props1.setProperty("key2", "value2");
		props1.setProperty("tb.array[0].age", "0"); // 0 将被文件中的99 覆盖
		Properties props2 = new Properties();
		props2.setProperty("spring", "framework");
		props2.setProperty("Don", "Mattingly");
		Properties props3 = new Properties();
		props3.setProperty("spider", "man");
		props3.setProperty("bat", "man");
		pfb.setPropertiesArray(new Properties[] {props1, props2, props3});
		// 触发
		pfb.afterPropertiesSet();

		Properties props = pfb.getObject();
		assertEquals("99", props.getProperty("tb.array[0].age"));
		assertEquals("value2", props.getProperty("key2"));
		assertEquals("framework", props.getProperty("spring"));
		assertEquals("Mattingly", props.getProperty("Don"));
		assertEquals("man", props.getProperty("spider"));
		assertEquals("man", props.getProperty("bat"));
		// 文件中的另一个键值对 会被添加到最终的props中
		assertEquals("test", props.getProperty("tb.list[1].name"));
	}

	/**
	 * 测试 如果开启 本地重写，那么本地Properties 优先级最高，会覆盖文件中的属性！
	*/
	@Test
	public void testWithPropertiesFileAndLocalPropertiesAndLocalOverride() throws Exception {
		pfb.setLocation(TEST_PROPS);
		Properties localProps = new Properties();
		localProps.setProperty("key2", "value2");
		localProps.setProperty("tb.array[0].age", "0");
		pfb.setProperties(localProps);
		// 如果开启 本地重写，那么本地Properties 优先级最高，会覆盖文件中的属性！
		pfb.setLocalOverride(true);
		pfb.afterPropertiesSet();
		Properties props = pfb.getObject();
		assertEquals("0", props.getProperty("tb.array[0].age"));
		assertEquals("value2", props.getProperty("key2"));
	}

	/**
	 * 测试  原型  （默认为单例）
	 * tb.array[0].age=99
	 * tb.list[1].name=test
	 */
	@Test
	public void testWithPrototype() throws Exception {
		pfb.setSingleton(false);
		pfb.setLocation(TEST_PROPS);
		Properties localProps = new Properties();
		localProps.setProperty("key2", "value2");
		pfb.setProperties(localProps);
		pfb.afterPropertiesSet();
		Properties props = pfb.getObject();
		assertEquals("99", props.getProperty("tb.array[0].age"));
		assertEquals("value2", props.getProperty("key2"));
		Properties newProps = pfb.getObject();
		// 验证 设置非单例后  2个对象地址 不同
		assertTrue(props != newProps);
		assertEquals("99", newProps.getProperty("tb.array[0].age"));
		assertEquals("value2", newProps.getProperty("key2"));
	}
}
