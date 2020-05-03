

package org.springframework.transaction.annotation;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * Tests proving that regardless the proxy strategy used (JDK interface-based vs. CGLIB
 * subclass-based), discovery of advice-oriented annotations is consistent.
 *
 * For example, Spring's @Transactional may be declared at the interface or class level,
 * and whether interface or subclass proxies are used, the @Transactional annotation must
 * be discovered in a consistent fashion.

 */
@SuppressWarnings("resource")
public class ProxyAnnotationDiscoveryTests {

	@Test
	public void annotatedServiceWithoutInterface_PTC_true() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(PTCTrue.class, AnnotatedServiceWithoutInterface.class);
		ctx.refresh();
		AnnotatedServiceWithoutInterface s = ctx.getBean(AnnotatedServiceWithoutInterface.class);
		assertTrue("expected a subclass proxy", AopUtils.isCglibProxy(s));
		assertThat(s, instanceOf(AnnotatedServiceWithoutInterface.class));
	}

	@Test
	public void annotatedServiceWithoutInterface_PTC_false() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(PTCFalse.class, AnnotatedServiceWithoutInterface.class);
		ctx.refresh();
		AnnotatedServiceWithoutInterface s = ctx.getBean(AnnotatedServiceWithoutInterface.class);
		assertTrue("expected a subclass proxy", AopUtils.isCglibProxy(s));
		assertThat(s, instanceOf(AnnotatedServiceWithoutInterface.class));
	}

	@Test
	public void nonAnnotatedService_PTC_true() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(PTCTrue.class, AnnotatedServiceImpl.class);
		ctx.refresh();
		NonAnnotatedService s = ctx.getBean(NonAnnotatedService.class);
		assertTrue("expected a subclass proxy", AopUtils.isCglibProxy(s));
		assertThat(s, instanceOf(AnnotatedServiceImpl.class));
	}

	@Test
	public void nonAnnotatedService_PTC_false() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(PTCFalse.class, AnnotatedServiceImpl.class);
		ctx.refresh();
		NonAnnotatedService s = ctx.getBean(NonAnnotatedService.class);
		assertTrue("expected a jdk proxy", AopUtils.isJdkDynamicProxy(s));
		assertThat(s, not(instanceOf(AnnotatedServiceImpl.class)));
	}

	@Test
	public void annotatedService_PTC_true() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(PTCTrue.class, NonAnnotatedServiceImpl.class);
		ctx.refresh();
		AnnotatedService s = ctx.getBean(AnnotatedService.class);
		assertTrue("expected a subclass proxy", AopUtils.isCglibProxy(s));
		assertThat(s, instanceOf(NonAnnotatedServiceImpl.class));
	}

	@Test
	public void annotatedService_PTC_false() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(PTCFalse.class, NonAnnotatedServiceImpl.class);
		ctx.refresh();
		AnnotatedService s = ctx.getBean(AnnotatedService.class);
		assertTrue("expected a jdk proxy", AopUtils.isJdkDynamicProxy(s));
		assertThat(s, not(instanceOf(NonAnnotatedServiceImpl.class)));
	}
}

@Configuration
@EnableTransactionManagement(proxyTargetClass=false)
class PTCFalse { }

@Configuration
@EnableTransactionManagement(proxyTargetClass=true)
class PTCTrue { }

interface NonAnnotatedService {
	void m();
}

interface AnnotatedService {
	@Transactional void m();
}

class NonAnnotatedServiceImpl implements AnnotatedService {
	@Override
	public void m() { }
}

class AnnotatedServiceImpl implements NonAnnotatedService {
	@Override
	@Transactional public void m() { }
}

class AnnotatedServiceWithoutInterface {
	@Transactional public void m() { }
}
