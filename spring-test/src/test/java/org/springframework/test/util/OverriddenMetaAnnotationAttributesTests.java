

package org.springframework.test.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.*;
import static org.springframework.test.util.MetaAnnotationUtils.*;

/**
 * Unit tests for {@link MetaAnnotationUtils} that verify support for overridden
 * meta-annotation attributes.
 *
 * See <a href="https://jira.spring.io/browse/SPR-10181">SPR-10181</a>.
 *
 * @author Sam Brannen
 * @since 4.0
 * @see MetaAnnotationUtilsTests
 */
public class OverriddenMetaAnnotationAttributesTests {

	@Test
	public void contextConfigurationValue() throws Exception {
		Class<MetaValueConfigTestCase> declaringClass = MetaValueConfigTestCase.class;
		AnnotationDescriptor<ContextConfiguration> descriptor = findAnnotationDescriptor(declaringClass,
			ContextConfiguration.class);
		assertNotNull(descriptor);
		assertEquals(declaringClass, descriptor.getRootDeclaringClass());
		assertEquals(MetaValueConfig.class, descriptor.getComposedAnnotationType());
		assertEquals(ContextConfiguration.class, descriptor.getAnnotationType());
		assertNotNull(descriptor.getComposedAnnotation());
		assertEquals(MetaValueConfig.class, descriptor.getComposedAnnotationType());

		// direct access to annotation value:
		assertArrayEquals(new String[] { "foo.xml" }, descriptor.getAnnotation().value());
	}

	@Test
	public void overriddenContextConfigurationValue() throws Exception {
		Class<?> declaringClass = OverriddenMetaValueConfigTestCase.class;
		AnnotationDescriptor<ContextConfiguration> descriptor = findAnnotationDescriptor(declaringClass,
			ContextConfiguration.class);
		assertNotNull(descriptor);
		assertEquals(declaringClass, descriptor.getRootDeclaringClass());
		assertEquals(MetaValueConfig.class, descriptor.getComposedAnnotationType());
		assertEquals(ContextConfiguration.class, descriptor.getAnnotationType());
		assertNotNull(descriptor.getComposedAnnotation());
		assertEquals(MetaValueConfig.class, descriptor.getComposedAnnotationType());

		// direct access to annotation value:
		assertArrayEquals(new String[] { "foo.xml" }, descriptor.getAnnotation().value());

		// overridden attribute:
		AnnotationAttributes attributes = descriptor.getAnnotationAttributes();

		// NOTE: we would like to be able to override the 'value' attribute; however,
		// Spring currently does not allow overrides for the 'value' attribute.
		// See SPR-11393 for related discussions.
		assertArrayEquals(new String[] { "foo.xml" }, attributes.getStringArray("value"));
	}

	@Test
	public void contextConfigurationLocationsAndInheritLocations() throws Exception {
		Class<MetaLocationsConfigTestCase> declaringClass = MetaLocationsConfigTestCase.class;
		AnnotationDescriptor<ContextConfiguration> descriptor = findAnnotationDescriptor(declaringClass,
			ContextConfiguration.class);
		assertNotNull(descriptor);
		assertEquals(declaringClass, descriptor.getRootDeclaringClass());
		assertEquals(MetaLocationsConfig.class, descriptor.getComposedAnnotationType());
		assertEquals(ContextConfiguration.class, descriptor.getAnnotationType());
		assertNotNull(descriptor.getComposedAnnotation());
		assertEquals(MetaLocationsConfig.class, descriptor.getComposedAnnotationType());

		// direct access to annotation attributes:
		assertArrayEquals(new String[] { "foo.xml" }, descriptor.getAnnotation().locations());
		assertFalse(descriptor.getAnnotation().inheritLocations());
	}

	@Test
	public void overriddenContextConfigurationLocationsAndInheritLocations() throws Exception {
		Class<?> declaringClass = OverriddenMetaLocationsConfigTestCase.class;
		AnnotationDescriptor<ContextConfiguration> descriptor = findAnnotationDescriptor(declaringClass,
			ContextConfiguration.class);
		assertNotNull(descriptor);
		assertEquals(declaringClass, descriptor.getRootDeclaringClass());
		assertEquals(MetaLocationsConfig.class, descriptor.getComposedAnnotationType());
		assertEquals(ContextConfiguration.class, descriptor.getAnnotationType());
		assertNotNull(descriptor.getComposedAnnotation());
		assertEquals(MetaLocationsConfig.class, descriptor.getComposedAnnotationType());

		// direct access to annotation attributes:
		assertArrayEquals(new String[] { "foo.xml" }, descriptor.getAnnotation().locations());
		assertFalse(descriptor.getAnnotation().inheritLocations());

		// overridden attributes:
		AnnotationAttributes attributes = descriptor.getAnnotationAttributes();
		assertArrayEquals(new String[] { "bar.xml" }, attributes.getStringArray("locations"));
		assertTrue(attributes.getBoolean("inheritLocations"));
	}


	// -------------------------------------------------------------------------

	@ContextConfiguration("foo.xml")
	@Retention(RetentionPolicy.RUNTIME)
	static @interface MetaValueConfig {

		String[] value() default {};
	}

	@MetaValueConfig
	public static class MetaValueConfigTestCase {
	}

	@MetaValueConfig("bar.xml")
	public static class OverriddenMetaValueConfigTestCase {
	}

	@ContextConfiguration(locations = "foo.xml", inheritLocations = false)
	@Retention(RetentionPolicy.RUNTIME)
	static @interface MetaLocationsConfig {

		String[] locations() default {};

		boolean inheritLocations();
	}

	@MetaLocationsConfig(inheritLocations = true)
	static class MetaLocationsConfigTestCase {
	}

	@MetaLocationsConfig(locations = "bar.xml", inheritLocations = true)
	static class OverriddenMetaLocationsConfigTestCase {
	}

}
