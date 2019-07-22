

package org.springframework.transaction.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.tests.transaction.CallCountingTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class TransactionManagerConfiguration {

	@Bean
	@Qualifier("synch")
	public PlatformTransactionManager transactionManager1() {
		return new CallCountingTransactionManager();
	}

	@Bean
	@NoSynch
	public PlatformTransactionManager transactionManager2() {
		CallCountingTransactionManager tm = new CallCountingTransactionManager();
		tm.setTransactionSynchronization(CallCountingTransactionManager.SYNCHRONIZATION_NEVER);
		return tm;
	}

}
