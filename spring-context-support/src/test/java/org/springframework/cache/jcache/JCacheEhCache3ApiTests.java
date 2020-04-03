

package org.springframework.cache.jcache;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

/**
 * Just here to be run against EHCache 3, whereas the original JCacheEhCacheAnnotationTests
 * runs against EhCache 2.x with the EhCache-JCache add-on.
 *
 * @author Stephane Nicoll
 */
public class JCacheEhCache3ApiTests extends JCacheEhCacheApiTests {

	@Override
	protected CachingProvider getCachingProvider() {
		return Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
	}

}
