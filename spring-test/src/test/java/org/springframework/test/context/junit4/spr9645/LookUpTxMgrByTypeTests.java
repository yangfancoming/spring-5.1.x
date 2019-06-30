

package org.springframework.test.context.junit4.spr9645;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.tests.transaction.CallCountingTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class LookUpTxMgrByTypeTests {

	private static final CallCountingTransactionManager txManager = new CallCountingTransactionManager();

	@Configuration
	static class Config {

		@Bean
		public PlatformTransactionManager txManager() {
			return txManager;
		}
	}

	@BeforeTransaction
	public void beforeTransaction() {
		txManager.clear();
	}

	@Test
	public void transactionalTest() {
		assertEquals(1, txManager.begun);
		assertEquals(1, txManager.inflight);
		assertEquals(0, txManager.commits);
		assertEquals(0, txManager.rollbacks);
	}

	@AfterTransaction
	public void afterTransaction() {
		assertEquals(1, txManager.begun);
		assertEquals(0, txManager.inflight);
		assertEquals(0, txManager.commits);
		assertEquals(1, txManager.rollbacks);
	}

}
