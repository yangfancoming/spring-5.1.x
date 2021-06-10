
package org.springframework.web.servlet.handler;

import org.junit.Test;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.*;

/**
 * Test fixture for {@link MappedInterceptor} tests.
 */
public class MappedInterceptorTests {

	private LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();

	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Test
	public void noPatterns() {
		MappedInterceptor mappedInterceptor = new MappedInterceptor(null, null, interceptor);
		assertTrue(mappedInterceptor.matches("/foo", pathMatcher));
		assertTrue(mappedInterceptor.matches("/any", null));
	}

	@Test
	public void includePattern() {
		MappedInterceptor mappedInterceptor = new MappedInterceptor(new String[] { "/foo/*" }, interceptor);
		assertTrue(mappedInterceptor.matches("/foo/bar", pathMatcher));
		assertFalse(mappedInterceptor.matches("/bar/foo", pathMatcher));
	}

	@Test
	public void includePatternWithMatrixVariables() {
		MappedInterceptor mappedInterceptor = new MappedInterceptor(new String[] { "/foo*/*" }, interceptor);
		assertTrue(mappedInterceptor.matches("/foo;q=1/bar;s=2", pathMatcher));
	}

	@Test
	public void excludePattern() {
		MappedInterceptor mappedInterceptor = new MappedInterceptor(null, new String[] { "/admin/**" }, interceptor);
		assertTrue(mappedInterceptor.matches("/foo", pathMatcher));
		assertFalse(mappedInterceptor.matches("/admin/foo", pathMatcher));
	}

	// 包含和排除同时存在  排除优先级永远高于包含
	@Test
	public void includeAndExcludePatterns() {
		MappedInterceptor mappedInterceptor = new MappedInterceptor(new String[] { "/**" }, new String[] { "/admin/**" }, interceptor);
		assertTrue(mappedInterceptor.matches("/foo", pathMatcher));
		assertFalse(mappedInterceptor.matches("/admin/foo", pathMatcher));
	}

	// 测试 自定义路径匹配器
	@Test
	public void customPathMatcher() {
		MappedInterceptor mappedInterceptor = new MappedInterceptor(new String[] { "/foo/[0-9]*" }, interceptor);
		mappedInterceptor.setPathMatcher(new TestPathMatcher());
		assertTrue(mappedInterceptor.matches("/foo/123", pathMatcher));
		assertFalse(mappedInterceptor.matches("/foo/bar", pathMatcher));
	}

	@Test
	public void preHandle() throws Exception {
		HandlerInterceptor interceptor = mock(HandlerInterceptor.class);
		MappedInterceptor mappedInterceptor = new MappedInterceptor(new String[] { "/**" }, interceptor);
		mappedInterceptor.preHandle(mock(HttpServletRequest.class), mock(HttpServletResponse.class), null);
		then(interceptor).should().preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class), any());
	}

	@Test
	public void postHandle() throws Exception {
		HandlerInterceptor interceptor = mock(HandlerInterceptor.class);
		MappedInterceptor mappedInterceptor = new MappedInterceptor(new String[] { "/**" }, interceptor);
		mappedInterceptor.postHandle(mock(HttpServletRequest.class), mock(HttpServletResponse.class),null, mock(ModelAndView.class));
		then(interceptor).should().postHandle(any(), any(), any(), any());
	}

	@Test
	public void afterCompletion() throws Exception {
		HandlerInterceptor interceptor = mock(HandlerInterceptor.class);
		MappedInterceptor mappedInterceptor = new MappedInterceptor(new String[] { "/**" }, interceptor);
		mappedInterceptor.afterCompletion(mock(HttpServletRequest.class), mock(HttpServletResponse.class),null, mock(Exception.class));
		then(interceptor).should().afterCompletion(any(), any(), any(), any());
	}

	public static class TestPathMatcher implements PathMatcher {
		@Override
		public boolean isPattern(String path) {
			return false;
		}
		@Override
		public boolean match(String pattern, String path) {
			return path.matches(pattern);
		}
		@Override
		public boolean matchStart(String pattern, String path) {
			return false;
		}
		@Override
		public String extractPathWithinPattern(String pattern, String path) {
			return null;
		}
		@Override
		public Map<String, String> extractUriTemplateVariables(String pattern, String path) {
			return null;
		}
		@Override
		public Comparator<String> getPatternComparator(String path) {
			return null;
		}
		@Override
		public String combine(String pattern1, String pattern2) {
			return null;
		}
	}
}
