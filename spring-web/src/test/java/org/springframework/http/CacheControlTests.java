

package org.springframework.http;

import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;


public class CacheControlTests {

	@Test
	public void emptyCacheControl()  {
		CacheControl cc = CacheControl.empty();
		assertThat(cc.getHeaderValue(), Matchers.nullValue());
	}

	@Test
	public void maxAge()  {
		CacheControl cc = CacheControl.maxAge(1, TimeUnit.HOURS);
		assertThat(cc.getHeaderValue(), Matchers.equalTo("max-age=3600"));
	}

	@Test
	public void maxAgeAndDirectives()  {
		CacheControl cc = CacheControl.maxAge(3600, TimeUnit.SECONDS).cachePublic().noTransform();
		assertThat(cc.getHeaderValue(), Matchers.equalTo("max-age=3600, no-transform, public"));
	}

	@Test
	public void maxAgeAndSMaxAge()  {
		CacheControl cc = CacheControl.maxAge(1, TimeUnit.HOURS).sMaxAge(30, TimeUnit.MINUTES);
		// 这里 cc.getHeaderValue() ，也就是该构建器最终生成的头部的值会是 "max-age=3600, s-maxage=1800"
		assertThat(cc.getHeaderValue(), Matchers.equalTo("max-age=3600, s-maxage=1800"));
		System.out.println(cc.getHeaderValue());
	}

	@Test
	public void noCachePrivate()  {
		CacheControl cc = CacheControl.noCache().cachePrivate();
		assertThat(cc.getHeaderValue(), Matchers.equalTo("no-cache, private"));
	}

	@Test
	public void noStore()  {
		CacheControl cc = CacheControl.noStore();
		assertThat(cc.getHeaderValue(), Matchers.equalTo("no-store"));
	}

	@Test
	public void staleIfError()  {
		CacheControl cc = CacheControl.maxAge(1, TimeUnit.HOURS).staleIfError(2, TimeUnit.HOURS);
		assertThat(cc.getHeaderValue(), Matchers.equalTo("max-age=3600, stale-if-error=7200"));
	}

	@Test
	public void staleWhileRevalidate()  {
		CacheControl cc = CacheControl.maxAge(1, TimeUnit.HOURS).staleWhileRevalidate(2, TimeUnit.HOURS);
		assertThat(cc.getHeaderValue(), Matchers.equalTo("max-age=3600, stale-while-revalidate=7200"));
	}

}
