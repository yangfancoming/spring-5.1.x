

package org.springframework.jmx.export.assembler;

import java.util.Properties;
import javax.management.MBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfo;

import org.junit.Test;

import static org.junit.Assert.*;


public class MethodExclusionMBeanInfoAssemblerNotMappedTests extends AbstractJmxAssemblerTests {

	protected static final String OBJECT_NAME = "bean:name=testBean4";

	@Test
	public void testGetAgeIsReadOnly() throws Exception {
		ModelMBeanInfo info = getMBeanInfoFromAssembler();
		ModelMBeanAttributeInfo attr = info.getAttribute(AGE_ATTRIBUTE);
		assertTrue("Age is not readable", attr.isReadable());
		assertTrue("Age is not writable", attr.isWritable());
	}

	@Test
	public void testNickNameIsExposed() throws Exception {
		ModelMBeanInfo inf = (ModelMBeanInfo) getMBeanInfo();
		MBeanAttributeInfo attr = inf.getAttribute("NickName");
		assertNotNull("Nick Name should not be null", attr);
		assertTrue("Nick Name should be writable", attr.isWritable());
		assertTrue("Nick Name should be readable", attr.isReadable());
	}

	@Override
	protected String getObjectName() {
		return OBJECT_NAME;
	}

	@Override
	protected int getExpectedOperationCount() {
		return 11;
	}

	@Override
	protected int getExpectedAttributeCount() {
		return 4;
	}

	@Override
	protected String getApplicationContextPath() {
		return "org/springframework/jmx/export/assembler/methodExclusionAssemblerNotMapped.xml";
	}

	@Override
	protected MBeanInfoAssembler getAssembler() throws Exception {
		MethodExclusionMBeanInfoAssembler assembler = new MethodExclusionMBeanInfoAssembler();
		Properties props = new Properties();
		props.setProperty("bean:name=testBean5", "setAge,isSuperman,setSuperman,dontExposeMe");
		assembler.setIgnoredMethodMappings(props);
		return assembler;
	}

}
