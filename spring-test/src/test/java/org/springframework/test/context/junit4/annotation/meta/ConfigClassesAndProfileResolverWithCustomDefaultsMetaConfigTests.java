

package org.springframework.test.context.junit4.annotation.meta;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Integration tests for meta-annotation attribute override support, relying on
 * default attribute values defined in {@link ConfigClassesAndProfileResolverWithCustomDefaultsMetaConfig}.
 *
 * @author Sam Brannen
 * @since 4.0.3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ConfigClassesAndProfileResolverWithCustomDefaultsMetaConfig
public class ConfigClassesAndProfileResolverWithCustomDefaultsMetaConfigTests {

	@Autowired
	private String foo;


	@Test
	public void foo() {
		assertEquals("Resolver Foo", foo);
	}
}
