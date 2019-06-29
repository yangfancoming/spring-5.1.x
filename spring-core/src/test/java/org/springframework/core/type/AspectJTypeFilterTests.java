

package org.springframework.core.type;

import org.junit.Test;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.stereotype.Component;

import static org.junit.Assert.*;

/**
 * @author Ramnivas Laddad
 */
public class AspectJTypeFilterTests {

	@Test
	public void namePatternMatches() throws Exception {
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClass",
				"org.springframework.core.type.AspectJTypeFilterTests.SomeClass");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClass",
				"*");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClass",
				"*..SomeClass");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClass",
				"org..SomeClass");
	}

	@Test
	public void namePatternNoMatches() throws Exception {
		assertNoMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClass",
				"org.springframework.core.type.AspectJTypeFilterTests.SomeClassX");
	}

	@Test
	public void subclassPatternMatches() throws Exception {
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassExtendingSomeClass",
				"org.springframework.core.type.AspectJTypeFilterTests.SomeClass+");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassExtendingSomeClass",
				"*+");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassExtendingSomeClass",
				"java.lang.Object+");

		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassImplementingSomeInterface",
				"org.springframework.core.type.AspectJTypeFilterTests.SomeInterface+");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassImplementingSomeInterface",
				"*+");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassImplementingSomeInterface",
				"java.lang.Object+");

		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassExtendingSomeClassExtendingSomeClassAndImplementingSomeInterface",
				"org.springframework.core.type.AspectJTypeFilterTests.SomeInterface+");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassExtendingSomeClassExtendingSomeClassAndImplementingSomeInterface",
				"org.springframework.core.type.AspectJTypeFilterTests.SomeClassExtendingSomeClass+");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassExtendingSomeClassExtendingSomeClassAndImplementingSomeInterface",
				"org.springframework.core.type.AspectJTypeFilterTests.SomeClass+");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassExtendingSomeClassExtendingSomeClassAndImplementingSomeInterface",
				"*+");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassExtendingSomeClassExtendingSomeClassAndImplementingSomeInterface",
				"java.lang.Object+");
	}

	@Test
	public void subclassPatternNoMatches() throws Exception {
		assertNoMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassExtendingSomeClass",
				"java.lang.String+");
	}

	@Test
	public void annotationPatternMatches() throws Exception {
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassAnnotatedWithComponent",
				"@org.springframework.stereotype.Component *..*");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassAnnotatedWithComponent",
				"@* *..*");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassAnnotatedWithComponent",
				"@*..* *..*");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassAnnotatedWithComponent",
				"@*..*Component *..*");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassAnnotatedWithComponent",
				"@org.springframework.stereotype.Component *..*Component");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassAnnotatedWithComponent",
				"@org.springframework.stereotype.Component *");
	}

	@Test
	public void annotationPatternNoMatches() throws Exception {
		assertNoMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassAnnotatedWithComponent",
				"@org.springframework.stereotype.Repository *..*");
	}

	@Test
	public void compositionPatternMatches() throws Exception {
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClass",
				"!*..SomeOtherClass");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassExtendingSomeClassExtendingSomeClassAndImplementingSomeInterface",
				"org.springframework.core.type.AspectJTypeFilterTests.SomeInterface+ " +
						"&& org.springframework.core.type.AspectJTypeFilterTests.SomeClass+ " +
						"&& org.springframework.core.type.AspectJTypeFilterTests.SomeClassExtendingSomeClass+");
		assertMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClassExtendingSomeClassExtendingSomeClassAndImplementingSomeInterface",
				"org.springframework.core.type.AspectJTypeFilterTests.SomeInterface+ " +
						"|| org.springframework.core.type.AspectJTypeFilterTests.SomeClass+ " +
						"|| org.springframework.core.type.AspectJTypeFilterTests.SomeClassExtendingSomeClass+");
	}

	@Test
	public void compositionPatternNoMatches() throws Exception {
		assertNoMatch("org.springframework.core.type.AspectJTypeFilterTests$SomeClass",
				"*..Bogus && org.springframework.core.type.AspectJTypeFilterTests.SomeClass");
	}

	private void assertMatch(String type, String typePattern) throws Exception {
		MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
		MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(type);

		AspectJTypeFilter filter = new AspectJTypeFilter(typePattern, getClass().getClassLoader());
		assertTrue(filter.match(metadataReader, metadataReaderFactory));
		ClassloadingAssertions.assertClassNotLoaded(type);
	}

	private void assertNoMatch(String type, String typePattern) throws Exception {
		MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
		MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(type);

		AspectJTypeFilter filter = new AspectJTypeFilter(typePattern, getClass().getClassLoader());
		assertFalse(filter.match(metadataReader, metadataReaderFactory));
		ClassloadingAssertions.assertClassNotLoaded(type);
	}


	// We must use a standalone set of types to ensure that no one else is loading them
	// and interfering with ClassloadingAssertions.assertClassNotLoaded()
	interface SomeInterface {
	}

	static class SomeClass {
	}

	static class SomeClassExtendingSomeClass extends SomeClass {
	}

	static class SomeClassImplementingSomeInterface implements SomeInterface {
	}

	static class SomeClassExtendingSomeClassExtendingSomeClassAndImplementingSomeInterface
			extends SomeClassExtendingSomeClass implements SomeInterface {
	}

	@Component
	static class SomeClassAnnotatedWithComponent {
	}

}
