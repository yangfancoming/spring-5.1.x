

package org.springframework.transaction.config;

import java.io.Serializable;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.tests.transaction.CallCountingTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.SerializationTestUtils;

import static org.junit.Assert.*;

/**
 * @author Rob Harrop

 */
public class AnnotationDrivenTests {

	@Test
	public void withProxyTargetClass() throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("annotationDrivenProxyTargetClassTests.xml", getClass());
		doTestWithMultipleTransactionManagers(context);
	}

	@Test
	public void withConfigurationClass() throws Exception {
		ApplicationContext parent = new AnnotationConfigApplicationContext(TransactionManagerConfiguration.class);
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"annotationDrivenConfigurationClassTests.xml"}, getClass(), parent);
		doTestWithMultipleTransactionManagers(context);
	}

	@Test
	public void withAnnotatedTransactionManagers() throws Exception {
		AnnotationConfigApplicationContext parent = new AnnotationConfigApplicationContext();
		parent.registerBeanDefinition("transactionManager1", new RootBeanDefinition(SynchTransactionManager.class));
		parent.registerBeanDefinition("transactionManager2", new RootBeanDefinition(NoSynchTransactionManager.class));
		parent.refresh();
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"annotationDrivenConfigurationClassTests.xml"}, getClass(), parent);
		doTestWithMultipleTransactionManagers(context);
	}

	private void doTestWithMultipleTransactionManagers(ApplicationContext context) {
		CallCountingTransactionManager tm1 = context.getBean("transactionManager1", CallCountingTransactionManager.class);
		CallCountingTransactionManager tm2 = context.getBean("transactionManager2", CallCountingTransactionManager.class);
		TransactionalService service = context.getBean("service", TransactionalService.class);
		assertTrue(AopUtils.isCglibProxy(service));
		service.setSomething("someName");
		assertEquals(1, tm1.commits);
		assertEquals(0, tm2.commits);
		service.doSomething();
		assertEquals(1, tm1.commits);
		assertEquals(1, tm2.commits);
		service.setSomething("someName");
		assertEquals(2, tm1.commits);
		assertEquals(1, tm2.commits);
		service.doSomething();
		assertEquals(2, tm1.commits);
		assertEquals(2, tm2.commits);
	}

	@Test
	@SuppressWarnings("resource")
	public void serializableWithPreviousUsage() throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("annotationDrivenProxyTargetClassTests.xml", getClass());
		TransactionalService service = context.getBean("service", TransactionalService.class);
		service.setSomething("someName");
		service = (TransactionalService) SerializationTestUtils.serializeAndDeserialize(service);
		service.setSomething("someName");
	}

	@Test
	@SuppressWarnings("resource")
	public void serializableWithoutPreviousUsage() throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("annotationDrivenProxyTargetClassTests.xml", getClass());
		TransactionalService service = context.getBean("service", TransactionalService.class);
		service = (TransactionalService) SerializationTestUtils.serializeAndDeserialize(service);
		service.setSomething("someName");
	}


	@SuppressWarnings("serial")
	public static class TransactionCheckingInterceptor implements MethodInterceptor, Serializable {

		@Override
		public Object invoke(MethodInvocation methodInvocation) throws Throwable {
			if (methodInvocation.getMethod().getName().equals("setSomething")) {
				assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
				assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
			}
			else {
				assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
				assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
			}
			return methodInvocation.proceed();
		}
	}

}
