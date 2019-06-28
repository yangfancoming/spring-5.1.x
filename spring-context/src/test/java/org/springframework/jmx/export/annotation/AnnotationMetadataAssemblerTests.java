

package org.springframework.jmx.export.annotation;

import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;

import org.junit.Test;

import org.springframework.jmx.IJmxTestBean;
import org.springframework.jmx.export.assembler.AbstractMetadataAssemblerTests;
import org.springframework.jmx.export.metadata.JmxAttributeSource;

import static org.junit.Assert.*;

/**
 * @author Rob Harrop
 * @author Chris Beams
 */
public class AnnotationMetadataAssemblerTests extends AbstractMetadataAssemblerTests {

	private static final String OBJECT_NAME = "bean:name=testBean4";


	@Test
	public void testAttributeFromInterface() throws Exception {
		ModelMBeanInfo inf = getMBeanInfoFromAssembler();
		ModelMBeanAttributeInfo attr = inf.getAttribute("Colour");
		assertTrue("The name attribute should be writable", attr.isWritable());
		assertTrue("The name attribute should be readable", attr.isReadable());
	}

	@Test
	public void testOperationFromInterface() throws Exception {
		ModelMBeanInfo inf = getMBeanInfoFromAssembler();
		ModelMBeanOperationInfo op = inf.getOperation("fromInterface");
		assertNotNull(op);
	}

	@Test
	public void testOperationOnGetter() throws Exception {
		ModelMBeanInfo inf = getMBeanInfoFromAssembler();
		ModelMBeanOperationInfo op = inf.getOperation("getExpensiveToCalculate");
		assertNotNull(op);
	}

	@Test
	public void testRegistrationOnInterface() throws Exception {
		Object bean = getContext().getBean("testInterfaceBean");
		ModelMBeanInfo inf = getAssembler().getMBeanInfo(bean, "bean:name=interfaceTestBean");
		assertNotNull(inf);
		assertEquals("My Managed Bean", inf.getDescription());

		ModelMBeanOperationInfo op = inf.getOperation("foo");
		assertNotNull("foo operation not exposed", op);
		assertEquals("invoke foo", op.getDescription());

		assertNull("doNotExpose operation should not be exposed", inf.getOperation("doNotExpose"));

		ModelMBeanAttributeInfo attr = inf.getAttribute("Bar");
		assertNotNull("bar attribute not exposed", attr);
		assertEquals("Bar description", attr.getDescription());

		ModelMBeanAttributeInfo attr2 = inf.getAttribute("CacheEntries");
		assertNotNull("cacheEntries attribute not exposed", attr2);
		assertEquals("Metric Type should be COUNTER", "COUNTER",
				attr2.getDescriptor().getFieldValue("metricType"));
	}


	@Override
	protected JmxAttributeSource getAttributeSource() {
		return new AnnotationJmxAttributeSource();
	}

	@Override
	protected String getObjectName() {
		return OBJECT_NAME;
	}

	@Override
	protected IJmxTestBean createJmxTestBean() {
		return new AnnotationTestSubBean();
	}

	@Override
	protected String getApplicationContextPath() {
		return "org/springframework/jmx/export/annotation/annotations.xml";
	}

	@Override
	protected int getExpectedAttributeCount() {
		return super.getExpectedAttributeCount() + 1;
	}

	@Override
	protected int getExpectedOperationCount() {
		return super.getExpectedOperationCount() + 4;
	}
}
