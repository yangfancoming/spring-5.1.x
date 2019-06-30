

package org.springframework.test.util;

import org.junit.Test;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.util.AopTestUtils.*;

/**
 * Unit tests for {@link AopTestUtils}.
 *
 * @author Sam Brannen
 * @since 4.2
 */
public class AopTestUtilsTests {

	private final FooImpl foo = new FooImpl();


	@Test(expected = IllegalArgumentException.class)
	public void getTargetObjectForNull() {
		getTargetObject(null);
	}

	@Test
	public void getTargetObjectForNonProxiedObject() {
		Foo target = getTargetObject(foo);
		assertSame(foo, target);
	}

	@Test
	public void getTargetObjectWrappedInSingleJdkDynamicProxy() {
		Foo target = getTargetObject(jdkProxy(foo));
		assertSame(foo, target);
	}

	@Test
	public void getTargetObjectWrappedInSingleCglibProxy() {
		Foo target = getTargetObject(cglibProxy(foo));
		assertSame(foo, target);
	}

	@Test
	public void getTargetObjectWrappedInDoubleJdkDynamicProxy() {
		Foo target = getTargetObject(jdkProxy(jdkProxy(foo)));
		assertNotSame(foo, target);
	}

	@Test
	public void getTargetObjectWrappedInDoubleCglibProxy() {
		Foo target = getTargetObject(cglibProxy(cglibProxy(foo)));
		assertNotSame(foo, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getUltimateTargetObjectForNull() {
		getUltimateTargetObject(null);
	}

	@Test
	public void getUltimateTargetObjectForNonProxiedObject() {
		Foo target = getUltimateTargetObject(foo);
		assertSame(foo, target);
	}

	@Test
	public void getUltimateTargetObjectWrappedInSingleJdkDynamicProxy() {
		Foo target = getUltimateTargetObject(jdkProxy(foo));
		assertSame(foo, target);
	}

	@Test
	public void getUltimateTargetObjectWrappedInSingleCglibProxy() {
		Foo target = getUltimateTargetObject(cglibProxy(foo));
		assertSame(foo, target);
	}

	@Test
	public void getUltimateTargetObjectWrappedInDoubleJdkDynamicProxy() {
		Foo target = getUltimateTargetObject(jdkProxy(jdkProxy(foo)));
		assertSame(foo, target);
	}

	@Test
	public void getUltimateTargetObjectWrappedInDoubleCglibProxy() {
		Foo target = getUltimateTargetObject(cglibProxy(cglibProxy(foo)));
		assertSame(foo, target);
	}

	@Test
	public void getUltimateTargetObjectWrappedInCglibProxyWrappedInJdkDynamicProxy() {
		Foo target = getUltimateTargetObject(jdkProxy(cglibProxy(foo)));
		assertSame(foo, target);
	}

	@Test
	public void getUltimateTargetObjectWrappedInCglibProxyWrappedInDoubleJdkDynamicProxy() {
		Foo target = getUltimateTargetObject(jdkProxy(jdkProxy(cglibProxy(foo))));
		assertSame(foo, target);
	}

	private Foo jdkProxy(Foo foo) {
		ProxyFactory pf = new ProxyFactory();
		pf.setTarget(foo);
		pf.addInterface(Foo.class);
		Foo proxy = (Foo) pf.getProxy();
		assertTrue("Proxy is a JDK dynamic proxy", AopUtils.isJdkDynamicProxy(proxy));
		assertThat(proxy, instanceOf(Foo.class));
		return proxy;
	}

	private Foo cglibProxy(Foo foo) {
		ProxyFactory pf = new ProxyFactory();
		pf.setTarget(foo);
		pf.setProxyTargetClass(true);
		Foo proxy = (Foo) pf.getProxy();
		assertTrue("Proxy is a CGLIB proxy", AopUtils.isCglibProxy(proxy));
		assertThat(proxy, instanceOf(FooImpl.class));
		return proxy;
	}


	static interface Foo {
	}

	static class FooImpl implements Foo {
	}

}
