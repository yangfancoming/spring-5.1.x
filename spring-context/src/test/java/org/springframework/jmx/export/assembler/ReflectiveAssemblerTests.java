

package org.springframework.jmx.export.assembler;

/**
 * @author Rob Harrop
 */
public class ReflectiveAssemblerTests extends AbstractJmxAssemblerTests {

	protected static final String OBJECT_NAME = "bean:name=testBean1";


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
	protected MBeanInfoAssembler getAssembler() {
		return new SimpleReflectiveMBeanInfoAssembler();
	}

	@Override
	protected String getApplicationContextPath() {
		return "org/springframework/jmx/export/assembler/reflectiveAssembler.xml";
	}

}
