

package org.springframework.cache.config;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ConfigurableApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Abstract cache annotation tests (containing several reusable methods).
 */
public abstract class AbstractCacheAnnotationTests {

	protected ConfigurableApplicationContext ctx;

	protected CacheableService<?> cs;

	protected CacheableService<?> ccs;

	protected CacheManager cm;


	/**
	 * @return a refreshed application context
	 */
	protected abstract ConfigurableApplicationContext getApplicationContext();


	@Before
	public void setup() {
		this.ctx = getApplicationContext();
		this.cs = ctx.getBean("service", CacheableService.class);
		this.ccs = ctx.getBean("classService", CacheableService.class);
		this.cm = ctx.getBean("cacheManager", CacheManager.class);

		Collection<String> cn = this.cm.getCacheNames();
		assertTrue(cn.contains("testCache"));
		assertTrue(cn.contains("secondary"));
		assertTrue(cn.contains("primary"));
	}

	@After
	public void close() {
		if (this.ctx != null) {
			this.ctx.close();
		}
	}


	public void testCacheable(CacheableService<?> service)  {
		Object o1 = new Object();

		Object r1 = service.cache(o1);
		Object r2 = service.cache(o1);
		Object r3 = service.cache(o1);

		assertSame(r1, r2);
		assertSame(r1, r3);
	}

	public void testCacheableNull(CacheableService<?> service)  {
		Object o1 = new Object();
		assertNull(this.cm.getCache("testCache").get(o1));

		Object r1 = service.cacheNull(o1);
		Object r2 = service.cacheNull(o1);
		Object r3 = service.cacheNull(o1);

		assertSame(r1, r2);
		assertSame(r1, r3);

		assertEquals(r3, this.cm.getCache("testCache").get(o1).get());
		assertNull("Cached value should be null", r3);
	}

	public void testCacheableSync(CacheableService<?> service)  {
		Object o1 = new Object();

		Object r1 = service.cacheSync(o1);
		Object r2 = service.cacheSync(o1);
		Object r3 = service.cacheSync(o1);

		assertSame(r1, r2);
		assertSame(r1, r3);
	}

	public void testCacheableSyncNull(CacheableService<?> service)  {
		Object o1 = new Object();
		assertNull(this.cm.getCache("testCache").get(o1));

		Object r1 = service.cacheSyncNull(o1);
		Object r2 = service.cacheSyncNull(o1);
		Object r3 = service.cacheSyncNull(o1);

		assertSame(r1, r2);
		assertSame(r1, r3);

		assertEquals(r3, this.cm.getCache("testCache").get(o1).get());
		assertNull("Cached value should be null", r3);
	}

	public void testEvict(CacheableService<?> service)  {
		Object o1 = new Object();

		Object r1 = service.cache(o1);
		Object r2 = service.cache(o1);

		assertSame(r1, r2);
		service.invalidate(o1);
		Object r3 = service.cache(o1);
		Object r4 = service.cache(o1);
		assertNotSame(r1, r3);
		assertSame(r3, r4);
	}

	public void testEvictEarly(CacheableService<?> service)  {
		Object o1 = new Object();

		Object r1 = service.cache(o1);
		Object r2 = service.cache(o1);

		assertSame(r1, r2);
		try {
			service.evictEarly(o1);
		}
		catch (RuntimeException ex) {
			// expected
		}

		Object r3 = service.cache(o1);
		Object r4 = service.cache(o1);
		assertNotSame(r1, r3);
		assertSame(r3, r4);
	}

	public void testEvictException(CacheableService<?> service)  {
		Object o1 = new Object();

		Object r1 = service.cache(o1);
		Object r2 = service.cache(o1);

		assertSame(r1, r2);
		try {
			service.evictWithException(o1);
		}
		catch (RuntimeException ex) {
			// expected
		}
		// exception occurred, eviction skipped, data should still be in the cache
		Object r3 = service.cache(o1);
		assertSame(r1, r3);
	}

	public void testEvictWKey(CacheableService<?> service)  {
		Object o1 = new Object();

		Object r1 = service.cache(o1);
		Object r2 = service.cache(o1);

		assertSame(r1, r2);
		service.evict(o1, null);
		Object r3 = service.cache(o1);
		Object r4 = service.cache(o1);
		assertNotSame(r1, r3);
		assertSame(r3, r4);
	}

	public void testEvictWKeyEarly(CacheableService<?> service)  {
		Object o1 = new Object();

		Object r1 = service.cache(o1);
		Object r2 = service.cache(o1);

		assertSame(r1, r2);

		try {
			service.invalidateEarly(o1, null);
		}
		catch (Exception ex) {
			// expected
		}
		Object r3 = service.cache(o1);
		Object r4 = service.cache(o1);
		assertNotSame(r1, r3);
		assertSame(r3, r4);
	}

	public void testEvictAll(CacheableService<?> service)  {
		Object o1 = new Object();

		Object r1 = service.cache(o1);
		Object r2 = service.cache(o1);

		Object o2 = new Object();
		Object r10 = service.cache(o2);

		assertSame(r1, r2);
		assertNotSame(r1, r10);
		service.evictAll(new Object());
		Cache cache = this.cm.getCache("testCache");
		assertNull(cache.get(o1));
		assertNull(cache.get(o2));

		Object r3 = service.cache(o1);
		Object r4 = service.cache(o1);
		assertNotSame(r1, r3);
		assertSame(r3, r4);
	}

	public void testConditionalExpression(CacheableService<?> service)  {
		Object r1 = service.conditional(4);
		Object r2 = service.conditional(4);

		assertNotSame(r1, r2);

		Object r3 = service.conditional(3);
		Object r4 = service.conditional(3);

		assertSame(r3, r4);
	}

	public void testConditionalExpressionSync(CacheableService<?> service)  {
		Object r1 = service.conditionalSync(4);
		Object r2 = service.conditionalSync(4);

		assertNotSame(r1, r2);

		Object r3 = service.conditionalSync(3);
		Object r4 = service.conditionalSync(3);

		assertSame(r3, r4);
	}

	public void testUnlessExpression(CacheableService<?> service)  {
		Cache cache = this.cm.getCache("testCache");
		cache.clear();
		service.unless(10);
		service.unless(11);
		assertThat(cache.get(10).get(), equalTo(10L));
		assertThat(cache.get(11), nullValue());
	}

	public void testKeyExpression(CacheableService<?> service)  {
		Object r1 = service.key(5, 1);
		Object r2 = service.key(5, 2);

		assertSame(r1, r2);

		Object r3 = service.key(1, 5);
		Object r4 = service.key(2, 5);

		assertNotSame(r3, r4);
	}

	public void testVarArgsKey(CacheableService<?> service)  {
		Object r1 = service.varArgsKey(1, 2, 3);
		Object r2 = service.varArgsKey(1, 2, 3);

		assertSame(r1, r2);

		Object r3 = service.varArgsKey(1, 2, 3);
		Object r4 = service.varArgsKey(1, 2);

		assertNotSame(r3, r4);
	}


	public void testNullValue(CacheableService<?> service)  {
		Object key = new Object();
		assertNull(service.nullValue(key));
		int nr = service.nullInvocations().intValue();
		assertNull(service.nullValue(key));
		assertEquals(nr, service.nullInvocations().intValue());
		assertNull(service.nullValue(new Object()));
		assertEquals(nr + 1, service.nullInvocations().intValue());
	}

	public void testMethodName(CacheableService<?> service, String keyName)  {
		Object key = new Object();
		Object r1 = service.name(key);
		assertSame(r1, service.name(key));
		Cache cache = this.cm.getCache("testCache");
		// assert the method name is used
		assertNotNull(cache.get(keyName));
	}

	public void testRootVars(CacheableService<?> service) {
		Object key = new Object();
		Object r1 = service.rootVars(key);
		assertSame(r1, service.rootVars(key));
		Cache cache = this.cm.getCache("testCache");
		// assert the method name is used
		String expectedKey = "rootVarsrootVars" + AopProxyUtils.ultimateTargetClass(service) + service;
		assertNotNull(cache.get(expectedKey));
	}

	public void testCheckedThrowable(CacheableService<?> service)  {
		String arg = UUID.randomUUID().toString();
		try {
			service.throwChecked(arg);
			fail("Excepted exception");
		}
		catch (Exception ex) {
			assertEquals("Wrong exception type", IOException.class, ex.getClass());
			assertEquals(arg, ex.getMessage());
		}
	}

	public void testUncheckedThrowable(CacheableService<?> service)  {
		try {
			service.throwUnchecked(1L);
			fail("Excepted exception");
		}
		catch (RuntimeException ex) {
			assertEquals("Wrong exception type", UnsupportedOperationException.class, ex.getClass());
			assertEquals("1", ex.getMessage());
		}
	}

	public void testCheckedThrowableSync(CacheableService<?> service)  {
		String arg = UUID.randomUUID().toString();
		try {
			service.throwCheckedSync(arg);
			fail("Excepted exception");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			assertEquals("Wrong exception type", IOException.class, ex.getClass());
			assertEquals(arg, ex.getMessage());
		}
	}

	public void testUncheckedThrowableSync(CacheableService<?> service)  {
		try {
			service.throwUncheckedSync(1L);
			fail("Excepted exception");
		}
		catch (RuntimeException ex) {
			assertEquals("Wrong exception type", UnsupportedOperationException.class, ex.getClass());
			assertEquals("1", ex.getMessage());
		}
	}

	public void testNullArg(CacheableService<?> service) {
		Object r1 = service.cache(null);
		assertSame(r1, service.cache(null));
	}

	public void testCacheUpdate(CacheableService<?> service) {
		Object o = new Object();
		Cache cache = this.cm.getCache("testCache");
		assertNull(cache.get(o));
		Object r1 = service.update(o);
		assertSame(r1, cache.get(o).get());

		o = new Object();
		assertNull(cache.get(o));
		Object r2 = service.update(o);
		assertSame(r2, cache.get(o).get());
	}

	public void testConditionalCacheUpdate(CacheableService<?> service) {
		Integer one = 1;
		Integer three = 3;

		Cache cache = this.cm.getCache("testCache");
		assertEquals(one, Integer.valueOf(service.conditionalUpdate(one).toString()));
		assertNull(cache.get(one));

		assertEquals(three, Integer.valueOf(service.conditionalUpdate(three).toString()));
		assertEquals(three, Integer.valueOf(cache.get(three).get().toString()));
	}

	public void testMultiCache(CacheableService<?> service) {
		Object o1 = new Object();
		Object o2 = new Object();

		Cache primary = this.cm.getCache("primary");
		Cache secondary = this.cm.getCache("secondary");

		assertNull(primary.get(o1));
		assertNull(secondary.get(o1));
		Object r1 = service.multiCache(o1);
		assertSame(r1, primary.get(o1).get());
		assertSame(r1, secondary.get(o1).get());

		Object r2 = service.multiCache(o1);
		Object r3 = service.multiCache(o1);

		assertSame(r1, r2);
		assertSame(r1, r3);

		assertNull(primary.get(o2));
		assertNull(secondary.get(o2));
		Object r4 = service.multiCache(o2);
		assertSame(r4, primary.get(o2).get());
		assertSame(r4, secondary.get(o2).get());
	}

	public void testMultiEvict(CacheableService<?> service) {
		Object o1 = new Object();
		Object o2 = o1.toString() + "A";


		Object r1 = service.multiCache(o1);
		Object r2 = service.multiCache(o1);

		Cache primary = this.cm.getCache("primary");
		Cache secondary = this.cm.getCache("secondary");

		primary.put(o2, o2);
		assertSame(r1, r2);
		assertSame(r1, primary.get(o1).get());
		assertSame(r1, secondary.get(o1).get());

		service.multiEvict(o1);
		assertNull(primary.get(o1));
		assertNull(secondary.get(o1));
		assertNull(primary.get(o2));

		Object r3 = service.multiCache(o1);
		Object r4 = service.multiCache(o1);
		assertNotSame(r1, r3);
		assertSame(r3, r4);

		assertSame(r3, primary.get(o1).get());
		assertSame(r4, secondary.get(o1).get());
	}

	public void testMultiPut(CacheableService<?> service) {
		Object o = 1;

		Cache primary = this.cm.getCache("primary");
		Cache secondary = this.cm.getCache("secondary");

		assertNull(primary.get(o));
		assertNull(secondary.get(o));
		Object r1 = service.multiUpdate(o);
		assertSame(r1, primary.get(o).get());
		assertSame(r1, secondary.get(o).get());

		o = 2;
		assertNull(primary.get(o));
		assertNull(secondary.get(o));
		Object r2 = service.multiUpdate(o);
		assertSame(r2, primary.get(o).get());
		assertSame(r2, secondary.get(o).get());
	}

	public void testPutRefersToResult(CacheableService<?> service)  {
		Long id = Long.MIN_VALUE;
		TestEntity entity = new TestEntity();
		Cache primary = this.cm.getCache("primary");
		assertNull(primary.get(id));
		assertNull(entity.getId());
		service.putRefersToResult(entity);
		assertSame(entity, primary.get(id).get());
	}

	public void testMultiCacheAndEvict(CacheableService<?> service) {
		String methodName = "multiCacheAndEvict";

		Cache primary = this.cm.getCache("primary");
		Cache secondary = this.cm.getCache("secondary");
		Object key = 1;

		secondary.put(key, key);

		assertNull(secondary.get(methodName));
		assertSame(key, secondary.get(key).get());

		Object r1 = service.multiCacheAndEvict(key);
		assertSame(r1, service.multiCacheAndEvict(key));

		// assert the method name is used
		assertSame(r1, primary.get(methodName).get());
		assertNull(secondary.get(methodName));
		assertNull(secondary.get(key));
	}

	public void testMultiConditionalCacheAndEvict(CacheableService<?> service) {
		Cache primary = this.cm.getCache("primary");
		Cache secondary = this.cm.getCache("secondary");
		Object key = 1;

		secondary.put(key, key);

		assertNull(primary.get(key));
		assertSame(key, secondary.get(key).get());

		Object r1 = service.multiConditionalCacheAndEvict(key);
		Object r3 = service.multiConditionalCacheAndEvict(key);

		assertTrue(!r1.equals(r3));
		assertNull(primary.get(key));

		Object key2 = 3;
		Object r2 = service.multiConditionalCacheAndEvict(key2);
		assertSame(r2, service.multiConditionalCacheAndEvict(key2));

		// assert the method name is used
		assertSame(r2, primary.get(key2).get());
		assertNull(secondary.get(key2));
	}

	@Test
	public void testCacheable()  {
		testCacheable(this.cs);
	}

	@Test
	public void testCacheableNull()  {
		testCacheableNull(this.cs);
	}

	@Test
	public void testCacheableSync()  {
		testCacheableSync(this.cs);
	}

	@Test
	public void testCacheableSyncNull()  {
		testCacheableSyncNull(this.cs);
	}

	@Test
	public void testInvalidate()  {
		testEvict(this.cs);
	}

	@Test
	public void testEarlyInvalidate()  {
		testEvictEarly(this.cs);
	}

	@Test
	public void testEvictWithException()  {
		testEvictException(this.cs);
	}

	@Test
	public void testEvictAll()  {
		testEvictAll(this.cs);
	}

	@Test
	public void testInvalidateWithKey()  {
		testEvictWKey(this.cs);
	}

	@Test
	public void testEarlyInvalidateWithKey()  {
		testEvictWKeyEarly(this.cs);
	}

	@Test
	public void testConditionalExpression()  {
		testConditionalExpression(this.cs);
	}

	@Test
	public void testConditionalExpressionSync()  {
		testConditionalExpressionSync(this.cs);
	}

	@Test
	public void testUnlessExpression()  {
		testUnlessExpression(this.cs);
	}

	@Test
	public void testClassCacheUnlessExpression()  {
		testUnlessExpression(this.cs);
	}

	@Test
	public void testKeyExpression()  {
		testKeyExpression(this.cs);
	}

	@Test
	public void testVarArgsKey()  {
		testVarArgsKey(this.cs);
	}

	@Test
	public void testClassCacheCacheable()  {
		testCacheable(this.ccs);
	}

	@Test
	public void testClassCacheInvalidate()  {
		testEvict(this.ccs);
	}

	@Test
	public void testClassEarlyInvalidate()  {
		testEvictEarly(this.ccs);
	}

	@Test
	public void testClassEvictAll()  {
		testEvictAll(this.ccs);
	}

	@Test
	public void testClassEvictWithException()  {
		testEvictException(this.ccs);
	}

	@Test
	public void testClassCacheInvalidateWKey()  {
		testEvictWKey(this.ccs);
	}

	@Test
	public void testClassEarlyInvalidateWithKey()  {
		testEvictWKeyEarly(this.ccs);
	}

	@Test
	public void testNullValue()  {
		testNullValue(this.cs);
	}

	@Test
	public void testClassNullValue()  {
		Object key = new Object();
		assertNull(this.ccs.nullValue(key));
		int nr = this.ccs.nullInvocations().intValue();
		assertNull(this.ccs.nullValue(key));
		assertEquals(nr, this.ccs.nullInvocations().intValue());
		assertNull(this.ccs.nullValue(new Object()));
		// the check method is also cached
		assertEquals(nr, this.ccs.nullInvocations().intValue());
		assertEquals(nr + 1, AnnotatedClassCacheableService.nullInvocations.intValue());
	}

	@Test
	public void testMethodName()  {
		testMethodName(this.cs, "name");
	}

	@Test
	public void testClassMethodName()  {
		testMethodName(this.ccs, "nametestCache");
	}

	@Test
	public void testRootVars()  {
		testRootVars(this.cs);
	}

	@Test
	public void testClassRootVars()  {
		testRootVars(this.ccs);
	}

	@Test
	public void testCustomKeyGenerator() {
		Object param = new Object();
		Object r1 = this.cs.customKeyGenerator(param);
		assertSame(r1, this.cs.customKeyGenerator(param));
		Cache cache = this.cm.getCache("testCache");
		// Checks that the custom keyGenerator was used
		Object expectedKey = SomeCustomKeyGenerator.generateKey("customKeyGenerator", param);
		assertNotNull(cache.get(expectedKey));
	}

	@Test
	public void testUnknownCustomKeyGenerator() {
		try {
			Object param = new Object();
			this.cs.unknownCustomKeyGenerator(param);
			fail("should have failed with NoSuchBeanDefinitionException");
		}
		catch (NoSuchBeanDefinitionException ex) {
			// expected
		}
	}

	@Test
	public void testCustomCacheManager() {
		CacheManager customCm = this.ctx.getBean("customCacheManager", CacheManager.class);
		Object key = new Object();
		Object r1 = this.cs.customCacheManager(key);
		assertSame(r1, this.cs.customCacheManager(key));

		Cache cache = customCm.getCache("testCache");
		assertNotNull(cache.get(key));
	}

	@Test
	public void testUnknownCustomCacheManager() {
		try {
			Object param = new Object();
			this.cs.unknownCustomCacheManager(param);
			fail("should have failed with NoSuchBeanDefinitionException");
		}
		catch (NoSuchBeanDefinitionException ex) {
			// expected
		}
	}

	@Test
	public void testNullArg()  {
		testNullArg(this.cs);
	}

	@Test
	public void testClassNullArg()  {
		testNullArg(this.ccs);
	}

	@Test
	public void testCheckedException()  {
		testCheckedThrowable(this.cs);
	}

	@Test
	public void testClassCheckedException()  {
		testCheckedThrowable(this.ccs);
	}

	@Test
	public void testCheckedExceptionSync()  {
		testCheckedThrowableSync(this.cs);
	}

	@Test
	public void testClassCheckedExceptionSync()  {
		testCheckedThrowableSync(this.ccs);
	}

	@Test
	public void testUncheckedException()  {
		testUncheckedThrowable(this.cs);
	}

	@Test
	public void testClassUncheckedException()  {
		testUncheckedThrowable(this.ccs);
	}

	@Test
	public void testUncheckedExceptionSync()  {
		testUncheckedThrowableSync(this.cs);
	}

	@Test
	public void testClassUncheckedExceptionSync()  {
		testUncheckedThrowableSync(this.ccs);
	}

	@Test
	public void testUpdate() {
		testCacheUpdate(this.cs);
	}

	@Test
	public void testClassUpdate() {
		testCacheUpdate(this.ccs);
	}

	@Test
	public void testConditionalUpdate() {
		testConditionalCacheUpdate(this.cs);
	}

	@Test
	public void testClassConditionalUpdate() {
		testConditionalCacheUpdate(this.ccs);
	}

	@Test
	public void testMultiCache() {
		testMultiCache(this.cs);
	}

	@Test
	public void testClassMultiCache() {
		testMultiCache(this.ccs);
	}

	@Test
	public void testMultiEvict() {
		testMultiEvict(this.cs);
	}

	@Test
	public void testClassMultiEvict() {
		testMultiEvict(this.ccs);
	}

	@Test
	public void testMultiPut() {
		testMultiPut(this.cs);
	}

	@Test
	public void testClassMultiPut() {
		testMultiPut(this.ccs);
	}

	@Test
	public void testPutRefersToResult()  {
		testPutRefersToResult(this.cs);
	}

	@Test
	public void testClassPutRefersToResult()  {
		testPutRefersToResult(this.ccs);
	}

	@Test
	public void testMultiCacheAndEvict() {
		testMultiCacheAndEvict(this.cs);
	}

	@Test
	public void testClassMultiCacheAndEvict() {
		testMultiCacheAndEvict(this.ccs);
	}

	@Test
	public void testMultiConditionalCacheAndEvict() {
		testMultiConditionalCacheAndEvict(this.cs);
	}

	@Test
	public void testClassMultiConditionalCacheAndEvict() {
		testMultiConditionalCacheAndEvict(this.ccs);
	}

}
