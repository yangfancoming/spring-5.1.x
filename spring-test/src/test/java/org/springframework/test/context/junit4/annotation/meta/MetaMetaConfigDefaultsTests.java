

package org.springframework.test.context.junit4.annotation.meta;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Integration tests for meta-meta-annotation support, relying on default attribute
 * values defined in {@link ConfigClassesAndProfilesWithCustomDefaultsMetaConfig} and
 * overrides in {@link MetaMetaConfig}.
 *
 * @author Sam Brannen
 * @since 4.0.3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@MetaMetaConfig
public class MetaMetaConfigDefaultsTests {

	@Autowired
	private String foo;


	@Test
	public void foo() {
		assertEquals("Production Foo", foo);
	}
}
