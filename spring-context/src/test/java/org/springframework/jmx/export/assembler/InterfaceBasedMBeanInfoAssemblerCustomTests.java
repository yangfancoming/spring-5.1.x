

package org.springframework.jmx.export.assembler;

import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfo;

import org.junit.Test;

import static org.junit.Assert.*;


public class InterfaceBasedMBeanInfoAssemblerCustomTests extends AbstractJmxAssemblerTests {

	protected static final String OBJECT_NAME = "bean:name=testBean5";


	@Override
	protected String getObjectName() {
		return OBJECT_NAME;
	}

	@Override
	protected int getExpectedOperationCount() {
		return 5;
	}

	@Override
	protected int getExpectedAttributeCount() {
		return 2;
	}

	@Override
	protected MBeanInfoAssembler getAssembler() {
		InterfaceBasedMBeanInfoAssembler assembler = new InterfaceBasedMBeanInfoAssembler();
		assembler.setManagedInterfaces(new Class<?>[] {ICustomJmxBean.class});
		return assembler;
	}

	@Test
	public void testGetAgeIsReadOnly() throws Exception {
		ModelMBeanInfo info = getMBeanInfoFromAssembler();
		ModelMBeanAttributeInfo attr = info.getAttribute(AGE_ATTRIBUTE);

		assertTrue(attr.isReadable());
		assertFalse(attr.isWritable());
	}

	@Override
	protected String getApplicationContextPath() {
		return "org/springframework/jmx/export/assembler/interfaceAssemblerCustom.xml";
	}

}
