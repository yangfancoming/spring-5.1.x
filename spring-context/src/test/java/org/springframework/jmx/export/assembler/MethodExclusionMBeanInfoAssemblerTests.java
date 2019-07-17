

package org.springframework.jmx.export.assembler;

import java.lang.reflect.Method;
import java.util.Properties;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfo;

import org.junit.Test;

import org.springframework.jmx.JmxTestBean;

import static org.junit.Assert.*;


public class MethodExclusionMBeanInfoAssemblerTests extends AbstractJmxAssemblerTests {

	private static final String OBJECT_NAME = "bean:name=testBean5";


	@Override
	protected String getObjectName() {
		return OBJECT_NAME;
	}

	@Override
	protected int getExpectedOperationCount() {
		return 9;
	}

	@Override
	protected int getExpectedAttributeCount() {
		return 4;
	}

	@Override
	protected String getApplicationContextPath() {
		return "org/springframework/jmx/export/assembler/methodExclusionAssembler.xml";
	}

	@Override
	protected MBeanInfoAssembler getAssembler() {
		MethodExclusionMBeanInfoAssembler assembler = new MethodExclusionMBeanInfoAssembler();
		assembler.setIgnoredMethods(new String[] {"dontExposeMe", "setSuperman"});
		return assembler;
	}

	@Test
	public void testSupermanIsReadOnly() throws Exception {
		ModelMBeanInfo info = getMBeanInfoFromAssembler();
		ModelMBeanAttributeInfo attr = info.getAttribute("Superman");

		assertTrue(attr.isReadable());
		assertFalse(attr.isWritable());
	}

	/*
	 * https://opensource.atlassian.com/projects/spring/browse/SPR-2754
	 */
	@Test
	public void testIsNotIgnoredDoesntIgnoreUnspecifiedBeanMethods() throws Exception {
		final String beanKey = "myTestBean";
		MethodExclusionMBeanInfoAssembler assembler = new MethodExclusionMBeanInfoAssembler();
		Properties ignored = new Properties();
		ignored.setProperty(beanKey, "dontExposeMe,setSuperman");
		assembler.setIgnoredMethodMappings(ignored);
		Method method = JmxTestBean.class.getMethod("dontExposeMe");
		assertFalse(assembler.isNotIgnored(method, beanKey));
		// this bean does not have any ignored methods on it, so must obviously not be ignored...
		assertTrue(assembler.isNotIgnored(method, "someOtherBeanKey"));
	}

}
