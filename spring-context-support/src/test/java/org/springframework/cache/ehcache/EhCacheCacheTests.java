

package org.springframework.cache.ehcache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.cache.AbstractCacheTests;
import org.springframework.tests.Assume;
import org.springframework.tests.TestGroup;

import static org.junit.Assert.*;

/**
 * @author Costin Leau
 * @author Stephane Nicoll

 */
public class EhCacheCacheTests extends AbstractCacheTests<EhCacheCache> {

	private CacheManager cacheManager;

	private Ehcache nativeCache;

	private EhCacheCache cache;


	@Before
	public void setup() {
		cacheManager = new CacheManager(new Configuration().name("EhCacheCacheTests")
				.defaultCache(new CacheConfiguration("default", 100)));
		nativeCache = new net.sf.ehcache.Cache(new CacheConfiguration(CACHE_NAME, 100));
		cacheManager.addCache(nativeCache);

		cache = new EhCacheCache(nativeCache);
	}

	@After
	public void shutdown() {
		cacheManager.shutdown();
	}


	@Override
	protected EhCacheCache getCache() {
		return cache;
	}

	@Override
	protected Ehcache getNativeCache() {
		return nativeCache;
	}


	@Test
	public void testExpiredElements() throws Exception {
		Assume.group(TestGroup.LONG_RUNNING);

		String key = "brancusi";
		String value = "constantin";
		Element brancusi = new Element(key, value);
		// ttl = 10s
		brancusi.setTimeToLive(3);
		nativeCache.put(brancusi);

		assertEquals(value, cache.get(key).get());
		// wait for the entry to expire
		Thread.sleep(5 * 1000);
		assertNull(cache.get(key));
	}

}
