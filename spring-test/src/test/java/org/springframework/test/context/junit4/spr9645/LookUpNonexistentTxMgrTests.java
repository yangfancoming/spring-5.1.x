

package org.springframework.test.context.junit4.spr9645;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.tests.transaction.CallCountingTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import static org.junit.Assert.*;

/**
 * Integration tests that verify the behavior requested in
 * <a href="https://jira.spring.io/browse/SPR-9645">SPR-9645</a>.
 *
 * @author Sam Brannen
 * @since 3.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class LookUpNonexistentTxMgrTests {

	private static final CallCountingTransactionManager txManager = new CallCountingTransactionManager();

	@Configuration
	static class Config {

		@Bean
		public PlatformTransactionManager transactionManager() {
			return txManager;
		}
	}

	@Test
	public void nonTransactionalTest() {
		assertEquals(0, txManager.begun);
		assertEquals(0, txManager.inflight);
		assertEquals(0, txManager.commits);
		assertEquals(0, txManager.rollbacks);
	}
}
