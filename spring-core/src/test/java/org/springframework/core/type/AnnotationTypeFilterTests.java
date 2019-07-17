

package org.springframework.core.type;

import java.lang.annotation.Inherited;

import org.junit.Test;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import static org.junit.Assert.*;

/**
 * @author Ramnivas Laddad

 * @author Oliver Gierke
 */
public class AnnotationTypeFilterTests {

	@Test
	public void testDirectAnnotationMatch() throws Exception {
		MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
		String classUnderTest = "org.springframework.core.type.AnnotationTypeFilterTests$SomeComponent";
		MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(classUnderTest);

		AnnotationTypeFilter filter = new AnnotationTypeFilter(InheritedAnnotation.class);
		assertTrue(filter.match(metadataReader, metadataReaderFactory));
		ClassloadingAssertions.assertClassNotLoaded(classUnderTest);
	}

	@Test
	public void testInheritedAnnotationFromInterfaceDoesNotMatch() throws Exception {
		MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
		String classUnderTest = "org.springframework.core.type.AnnotationTypeFilterTests$SomeClassWithSomeComponentInterface";
		MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(classUnderTest);

		AnnotationTypeFilter filter = new AnnotationTypeFilter(InheritedAnnotation.class);
		// Must fail as annotation on interfaces should not be considered a match
		assertFalse(filter.match(metadataReader, metadataReaderFactory));
		ClassloadingAssertions.assertClassNotLoaded(classUnderTest);
	}

	@Test
	public void testInheritedAnnotationFromBaseClassDoesMatch() throws Exception {
		MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
		String classUnderTest = "org.springframework.core.type.AnnotationTypeFilterTests$SomeSubclassOfSomeComponent";
		MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(classUnderTest);

		AnnotationTypeFilter filter = new AnnotationTypeFilter(InheritedAnnotation.class);
		assertTrue(filter.match(metadataReader, metadataReaderFactory));
		ClassloadingAssertions.assertClassNotLoaded(classUnderTest);
	}

	@Test
	public void testNonInheritedAnnotationDoesNotMatch() throws Exception {
		MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
		String classUnderTest = "org.springframework.core.type.AnnotationTypeFilterTests$SomeSubclassOfSomeClassMarkedWithNonInheritedAnnotation";
		MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(classUnderTest);

		AnnotationTypeFilter filter = new AnnotationTypeFilter(NonInheritedAnnotation.class);
		// Must fail as annotation isn't inherited
		assertFalse(filter.match(metadataReader, metadataReaderFactory));
		ClassloadingAssertions.assertClassNotLoaded(classUnderTest);
	}

	@Test
	public void testNonAnnotatedClassDoesntMatch() throws Exception {
		MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
		String classUnderTest = "org.springframework.core.type.AnnotationTypeFilterTests$SomeNonCandidateClass";
		MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(classUnderTest);

		AnnotationTypeFilter filter = new AnnotationTypeFilter(Component.class);
		assertFalse(filter.match(metadataReader, metadataReaderFactory));
		ClassloadingAssertions.assertClassNotLoaded(classUnderTest);
	}

	@Test
	public void testMatchesInterfacesIfConfigured() throws Exception {
		MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
		String classUnderTest = "org.springframework.core.type.AnnotationTypeFilterTests$SomeClassWithSomeComponentInterface";
		MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(classUnderTest);

		AnnotationTypeFilter filter = new AnnotationTypeFilter(InheritedAnnotation.class, false, true);
		assertTrue(filter.match(metadataReader, metadataReaderFactory));
		ClassloadingAssertions.assertClassNotLoaded(classUnderTest);
	}


	// We must use a standalone set of types to ensure that no one else is loading them
	// and interfering with ClassloadingAssertions.assertClassNotLoaded()

	@Inherited
	private @interface InheritedAnnotation {
	}


	@InheritedAnnotation
	private static class SomeComponent {
	}


	@InheritedAnnotation
	private interface SomeComponentInterface {
	}


	@SuppressWarnings("unused")
	private static class SomeClassWithSomeComponentInterface implements Cloneable, SomeComponentInterface {
	}


	@SuppressWarnings("unused")
	private static class SomeSubclassOfSomeComponent extends SomeComponent {
	}


	private @interface NonInheritedAnnotation {
	}


	@NonInheritedAnnotation
	private static class SomeClassMarkedWithNonInheritedAnnotation {
	}


	@SuppressWarnings("unused")
	private static class SomeSubclassOfSomeClassMarkedWithNonInheritedAnnotation extends SomeClassMarkedWithNonInheritedAnnotation {
	}


	@SuppressWarnings("unused")
	private static class SomeNonCandidateClass {
	}

}
