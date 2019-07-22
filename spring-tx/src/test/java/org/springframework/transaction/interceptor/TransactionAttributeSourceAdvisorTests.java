

package org.springframework.transaction.interceptor;

import java.util.Properties;

import org.junit.Test;

import org.springframework.util.SerializationTestUtils;


public class TransactionAttributeSourceAdvisorTests {

	@Test
	public void serializability() throws Exception {
		TransactionInterceptor ti = new TransactionInterceptor();
		ti.setTransactionAttributes(new Properties());
		TransactionAttributeSourceAdvisor tas = new TransactionAttributeSourceAdvisor(ti);
		SerializationTestUtils.serializeAndDeserialize(tas);
	}

}
