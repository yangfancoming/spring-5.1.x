

package org.springframework.beans.factory.xml;

import org.junit.Test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.tests.sample.beans.TestBean;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * With Spring 3.1, bean id attributes (and all other id attributes across the
 * core schemas) are no longer typed as xsd:id, but as xsd:string.  This allows
 * for using the same bean id within nested <beans> elements.
 *
 * Duplicate ids *within the same level of nesting* will still be treated as an
 * error through the ProblemReporter, as this could never be an intended/valid
 * situation.

 * @since 3.1
 * @see org.springframework.beans.factory.xml.XmlBeanFactoryTests#testWithDuplicateName
 * @see org.springframework.beans.factory.xml.XmlBeanFactoryTests#testWithDuplicateNameInAlias
 */
public class DuplicateBeanIdTests {

	@Test
	public void duplicateBeanIdsWithinSameNestingLevelRaisesError() {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(bf);
		try {
			reader.loadBeanDefinitions(new ClassPathResource("DuplicateBeanIdTests-sameLevel-context.xml", this.getClass()));
			fail("expected parsing exception due to duplicate ids in same nesting level");
		}
		catch (Exception ex) {
			// expected
		}
	}

	@Test
	public void duplicateBeanIdsAcrossNestingLevels() {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(bf);
		reader.loadBeanDefinitions(new ClassPathResource("DuplicateBeanIdTests-multiLevel-context.xml", this.getClass()));
		TestBean testBean = bf.getBean(TestBean.class); // there should be only one
		assertThat(testBean.getName(), equalTo("nested"));
	}
}
