

package org.springframework.test.context.junit4.hybrid;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.SmartContextLoader;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Integration tests for hybrid {@link SmartContextLoader} implementations that
 * support path-based and class-based resources simultaneously, as is done in
 * Spring Boot.
 *
 * @author Sam Brannen
 * @since 4.0.4
 * @see HybridContextLoader
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = HybridContextLoader.class)
public class HybridContextLoaderTests {

	@Configuration
	static class Config {

		@Bean
		public String fooFromJava() {
			return "Java";
		}

		@Bean
		public String enigma() {
			return "enigma from Java";
		}
	}


	@Autowired
	private String fooFromXml;

	@Autowired
	private String fooFromJava;

	@Autowired
	private String enigma;


	@Test
	public void verifyContentsOfHybridApplicationContext() {
		assertEquals("XML", fooFromXml);
		assertEquals("Java", fooFromJava);

		// Note: the XML bean definition for "enigma" always wins since
		// ConfigurationClassBeanDefinitionReader.isOverriddenByExistingDefinition()
		// lets XML bean definitions override those "discovered" later via an
		// @Bean method.
		assertEquals("enigma from XML", enigma);
	}

}
