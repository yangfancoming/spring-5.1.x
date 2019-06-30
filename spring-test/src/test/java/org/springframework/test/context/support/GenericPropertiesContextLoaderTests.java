

package org.springframework.test.context.support;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.test.context.MergedContextConfiguration;

import static org.hamcrest.CoreMatchers.*;

/**
 * Unit tests for {@link GenericPropertiesContextLoader}.
 *
 * @author Sam Brannen
 * @since 4.0.4
 */
public class GenericPropertiesContextLoaderTests {

	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	@Rule
	public ExpectedException expectedException = ExpectedException.none();


	@Test
	public void configMustNotContainAnnotatedClasses() throws Exception {
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage(containsString("does not support annotated classes"));

		GenericPropertiesContextLoader loader = new GenericPropertiesContextLoader();
		MergedContextConfiguration mergedConfig = new MergedContextConfiguration(getClass(), EMPTY_STRING_ARRAY,
			new Class<?>[] { getClass() }, EMPTY_STRING_ARRAY, loader);
		loader.loadContext(mergedConfig);
	}

}
