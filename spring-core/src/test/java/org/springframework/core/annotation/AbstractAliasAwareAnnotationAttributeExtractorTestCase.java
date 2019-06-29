

package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.junit.Test;

import org.springframework.core.annotation.AnnotationUtilsTests.GroovyImplicitAliasesContextConfigClass;
import org.springframework.core.annotation.AnnotationUtilsTests.ImplicitAliasesContextConfig;
import org.springframework.core.annotation.AnnotationUtilsTests.Location1ImplicitAliasesContextConfigClass;
import org.springframework.core.annotation.AnnotationUtilsTests.Location2ImplicitAliasesContextConfigClass;
import org.springframework.core.annotation.AnnotationUtilsTests.Location3ImplicitAliasesContextConfigClass;
import org.springframework.core.annotation.AnnotationUtilsTests.ValueImplicitAliasesContextConfigClass;
import org.springframework.core.annotation.AnnotationUtilsTests.XmlImplicitAliasesContextConfigClass;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Abstract base class for tests involving concrete implementations of
 * {@link AbstractAliasAwareAnnotationAttributeExtractor}.
 *
 * @author Sam Brannen
 * @since 4.2.1
 */
public abstract class AbstractAliasAwareAnnotationAttributeExtractorTestCase {

	@Test
	public void getAttributeValueForImplicitAliases() throws Exception {
		assertGetAttributeValueForImplicitAliases(GroovyImplicitAliasesContextConfigClass.class, "groovyScript");
		assertGetAttributeValueForImplicitAliases(XmlImplicitAliasesContextConfigClass.class, "xmlFile");
		assertGetAttributeValueForImplicitAliases(ValueImplicitAliasesContextConfigClass.class, "value");
		assertGetAttributeValueForImplicitAliases(Location1ImplicitAliasesContextConfigClass.class, "location1");
		assertGetAttributeValueForImplicitAliases(Location2ImplicitAliasesContextConfigClass.class, "location2");
		assertGetAttributeValueForImplicitAliases(Location3ImplicitAliasesContextConfigClass.class, "location3");
	}

	private void assertGetAttributeValueForImplicitAliases(Class<?> clazz, String expected) throws Exception {
		Method xmlFile = ImplicitAliasesContextConfig.class.getDeclaredMethod("xmlFile");
		Method groovyScript = ImplicitAliasesContextConfig.class.getDeclaredMethod("groovyScript");
		Method value = ImplicitAliasesContextConfig.class.getDeclaredMethod("value");

		AnnotationAttributeExtractor<?> extractor = createExtractorFor(clazz, expected, ImplicitAliasesContextConfig.class);

		assertThat(extractor.getAttributeValue(value), is(expected));
		assertThat(extractor.getAttributeValue(groovyScript), is(expected));
		assertThat(extractor.getAttributeValue(xmlFile), is(expected));
	}

	protected abstract AnnotationAttributeExtractor<?> createExtractorFor(Class<?> clazz, String expected, Class<? extends Annotation> annotationType);

}
