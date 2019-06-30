

package org.springframework.cache.config;

import org.junit.Test;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.support.GenericXmlApplicationContext;

import static org.junit.Assert.*;

/**
 * AOP advice specific parsing tests.
 *
 * @author Stephane Nicoll
 */
public class CacheAdviceParserTests {

	@Test
	public void keyAndKeyGeneratorCannotBeSetTogether() {
		try {
			new GenericXmlApplicationContext("/org/springframework/cache/config/cache-advice-invalid.xml");
			fail("Should have failed to load context, one advise define both a key and a key generator");
		}
		catch (BeanDefinitionStoreException ex) {
			// TODO better exception handling
		}
	}

}
