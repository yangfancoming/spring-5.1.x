

package org.springframework.core.type;

import org.junit.Test;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;

import static org.junit.Assert.*;

/**
 * @author Ramnivas Laddad

 */
public class AssignableTypeFilterTests {

	@Test
	public void directMatch() throws Exception {
		MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
		String classUnderTest = "org.springframework.core.type.AssignableTypeFilterTests$TestNonInheritingClass";
		MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(classUnderTest);

		AssignableTypeFilter matchingFilter = new AssignableTypeFilter(TestNonInheritingClass.class);
		AssignableTypeFilter notMatchingFilter = new AssignableTypeFilter(TestInterface.class);
		assertFalse(notMatchingFilter.match(metadataReader, metadataReaderFactory));
		assertTrue(matchingFilter.match(metadataReader, metadataReaderFactory));
	}

	@Test
	public void interfaceMatch() throws Exception {
		MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
		String classUnderTest = "org.springframework.core.type.AssignableTypeFilterTests$TestInterfaceImpl";
		MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(classUnderTest);

		AssignableTypeFilter filter = new AssignableTypeFilter(TestInterface.class);
		assertTrue(filter.match(metadataReader, metadataReaderFactory));
		ClassloadingAssertions.assertClassNotLoaded(classUnderTest);
	}

	@Test
	public void superClassMatch() throws Exception {
		MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
		String classUnderTest = "org.springframework.core.type.AssignableTypeFilterTests$SomeDaoLikeImpl";
		MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(classUnderTest);

		AssignableTypeFilter filter = new AssignableTypeFilter(SimpleJdbcDaoSupport.class);
		assertTrue(filter.match(metadataReader, metadataReaderFactory));
		ClassloadingAssertions.assertClassNotLoaded(classUnderTest);
	}

	@Test
	public void interfaceThroughSuperClassMatch() throws Exception {
		MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
		String classUnderTest = "org.springframework.core.type.AssignableTypeFilterTests$SomeDaoLikeImpl";
		MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(classUnderTest);

		AssignableTypeFilter filter = new AssignableTypeFilter(JdbcDaoSupport.class);
		assertTrue(filter.match(metadataReader, metadataReaderFactory));
		ClassloadingAssertions.assertClassNotLoaded(classUnderTest);
	}


	// We must use a standalone set of types to ensure that no one else is loading them
	// and interfere with ClassloadingAssertions.assertClassNotLoaded()
	private static class TestNonInheritingClass {
	}

	private interface TestInterface {
	}

	@SuppressWarnings("unused")
	private static class TestInterfaceImpl implements TestInterface {
	}

	private interface SomeDaoLikeInterface {
	}

	@SuppressWarnings("unused")
	private static class SomeDaoLikeImpl extends SimpleJdbcDaoSupport implements SomeDaoLikeInterface {
	}

	private interface JdbcDaoSupport {
	}

	private static class SimpleJdbcDaoSupport implements JdbcDaoSupport {
	}

}
