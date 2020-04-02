

package org.springframework.web.util;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link UrlPathHelper}.
 */
public class UrlPathHelperTests {

	private static final String WEBSPHERE_URI_ATTRIBUTE = "com.ibm.websphere.servlet.uri_non_decoded";

	private static final UrlPathHelper helper = new UrlPathHelper();

	private final MockHttpServletRequest request = new MockHttpServletRequest();

	@Test
	public void getContextPath() {
		request.setContextPath("/petclinic");
		String contextPath = helper.getContextPath(request);
		System.out.println(contextPath);
	}

	@Test
	public void getRequestUri() {
		request.setRequestURI("/welcome.html");
		assertEquals( "/welcome.html", helper.getRequestUri(request));

		request.setRequestURI("/foo%20bar");
		assertEquals( "/foo bar", helper.getRequestUri(request));

		request.setRequestURI("/foo+bar");
		assertEquals( "/foo+bar", helper.getRequestUri(request));
	}

	// 测试 删除 分号内容
	@Test
	public void getRequestRemoveSemicolonContent() {
		helper.setRemoveSemicolonContent(true);

		request.setRequestURI("/foo;f=F;o=O;o=O/bar;b=B;a=A;r=R");
		assertEquals("/foo/bar", helper.getRequestUri(request));

		request.setRequestURI("/foo;a=b;jsessionid=c0o7fszeb1;c=d");
		assertEquals( "/foo", helper.getRequestUri(request));

		// SPR-13455
		request.setServletPath("/foo/1");
		request.setRequestURI("/foo/;test/1");
		assertEquals("/foo/1", helper.getRequestUri(request));
	}

	// 测试 不删除 分号内容   (不论是否删除分号 jsessionid 都会被删除)
	@Test
	public void getRequestKeepSemicolonContent() {
		helper.setRemoveSemicolonContent(false);
		request.setRequestURI("/foo;a=b;c=d");
		assertEquals("/foo;a=b;c=d", helper.getRequestUri(request));

		request.setRequestURI("/foo;jsessionid=c0o7fszeb1");
		assertEquals( "/foo", helper.getRequestUri(request));

		request.setRequestURI("/foo;a=b;jsessionid=c0o7fszeb1;c=d");
		assertEquals( "/foo;a=b;c=d", helper.getRequestUri(request));
		// SPR-10398
		request.setRequestURI("/foo;a=b;JSESSIONID=c0o7fszeb1;c=d");
		assertEquals( "/foo;a=b;c=d", helper.getRequestUri(request));
	}
	@Test
	public void getPathWithinApplication() {
		request.setContextPath("/petclinic");
		request.setRequestURI("/petclinic/welcome.html");
		assertEquals("/welcome.html", helper.getPathWithinApplication(request));
	}

	@Test
	public void getPathWithinApplicationForRootWithNoLeadingSlash() {
		request.setContextPath("/petclinic");
		request.setRequestURI("/petclinic");
		assertEquals( "/", helper.getPathWithinApplication(request));
	}

	@Test
	public void getPathWithinApplicationForSlashContextPath() {
		request.setContextPath("/");
		request.setRequestURI("/welcome.html");
		assertEquals( "/welcome.html", helper.getPathWithinApplication(request));
	}

	@Test
	public void getPathWithinServlet() {
		request.setContextPath("/petclinic");
		request.setServletPath("/main");
		request.setRequestURI("/petclinic/main/welcome.html");
		assertEquals( "/welcome.html", helper.getPathWithinServletMapping(request));
	}

	@Test
	public void alwaysUseFullPath() {
		helper.setAlwaysUseFullPath(true);
		request.setContextPath("/petclinic");
		request.setServletPath("/main");
		request.setRequestURI("/petclinic/main/welcome.html");
		assertEquals( "/main/welcome.html", helper.getLookupPathForRequest(request));
	}

	// SPR-11101
	@Test
	public void getPathWithinServletWithoutUrlDecoding() {
		request.setContextPath("/SPR-11101");
		request.setServletPath("/test_url_decoding/a/b");
		request.setRequestURI("/test_url_decoding/a%2Fb");
		helper.setUrlDecode(false);
		String actual = helper.getPathWithinServletMapping(request);
		assertEquals("/test_url_decoding/a%2Fb", actual);
	}


	@Test
	public void getLookupPathWithSemicolonContent() {
		helper.setRemoveSemicolonContent(false);
		request.setContextPath("/petclinic");
		request.setServletPath("/main");
		request.setRequestURI("/petclinic;a=b/main;b=c/welcome.html;c=d");
		assertEquals("/welcome.html;c=d", helper.getLookupPathForRequest(request));
	}

	@Test
	public void getLookupPathWithSemicolonContentAndNullPathInfo() {
		helper.setRemoveSemicolonContent(false);
		request.setContextPath("/petclinic");
		request.setServletPath("/welcome.html");
		request.setRequestURI("/petclinic;a=b/welcome.html;c=d");
		assertEquals("/welcome.html;c=d", helper.getLookupPathForRequest(request));
	}

	// suite of tests root requests for default servlets (SRV 11.2) on Websphere vs Tomcat and other containers
	// see: https://jira.springframework.org/browse/SPR-7064
	// / mapping (default servlet)
	@Test
	public void tomcatDefaultServletRoot() {
		request.setContextPath("/test");
		request.setPathInfo(null);
		request.setServletPath("/");
		request.setRequestURI("/test/");
		assertEquals("/", helper.getLookupPathForRequest(request));
	}

	@Test
	public void tomcatDefaultServletFile() {
		request.setContextPath("/test");
		request.setPathInfo(null);
		request.setServletPath("/foo");
		request.setRequestURI("/test/foo");
		assertEquals("/foo", helper.getLookupPathForRequest(request));
	}

	@Test
	public void tomcatDefaultServletFolder() {
		request.setContextPath("/test");
		request.setPathInfo(null);
		request.setServletPath("/foo/");
		request.setRequestURI("/test/foo/");
		assertEquals("/foo/", helper.getLookupPathForRequest(request));
	}

	//SPR-12372 & SPR-13455
	@Test
	public void removeDuplicateSlashesInPath() {
		request.setContextPath("/SPR-12372");
		request.setPathInfo(null);
		request.setServletPath("/foo/bar/");
		request.setRequestURI("/SPR-12372/foo//bar/");
		assertEquals("/foo/bar/", helper.getLookupPathForRequest(request));
		request.setServletPath("/foo/bar/");
		request.setRequestURI("/SPR-12372/foo/bar//");
		assertEquals("/foo/bar/", helper.getLookupPathForRequest(request));
		// "normal" case
		request.setServletPath("/foo/bar//");
		request.setRequestURI("/SPR-12372/foo/bar//");
		assertEquals("/foo/bar//", helper.getLookupPathForRequest(request));
	}

	@Test
	public void wasDefaultServletRoot() {
		request.setContextPath("/test");
		request.setPathInfo("/");
		request.setServletPath("");
		request.setRequestURI("/test/");
		request.setAttribute(WEBSPHERE_URI_ATTRIBUTE, "/test/");
		assertEquals("/", helper.getLookupPathForRequest(request));
	}

	@Test
	public void wasDefaultServletRootWithCompliantSetting() {
		request.setAttribute(WEBSPHERE_URI_ATTRIBUTE, "/test/");
		tomcatDefaultServletRoot();
	}

	@Test
	public void wasDefaultServletFile() {
		request.setContextPath("/test");
		request.setPathInfo("/foo");
		request.setServletPath("");
		request.setRequestURI("/test/foo");
		request.setAttribute(WEBSPHERE_URI_ATTRIBUTE, "/test/foo");
		assertEquals("/foo", helper.getLookupPathForRequest(request));
	}

	@Test
	public void wasDefaultServletFileWithCompliantSetting() {
		request.setAttribute(WEBSPHERE_URI_ATTRIBUTE, "/test/foo");
		tomcatDefaultServletFile();
	}

	@Test
	public void wasDefaultServletFolder() {
		request.setContextPath("/test");
		request.setPathInfo("/foo/");
		request.setServletPath("");
		request.setRequestURI("/test/foo/");
		request.setAttribute(WEBSPHERE_URI_ATTRIBUTE, "/test/foo/");
		assertEquals("/foo/", helper.getLookupPathForRequest(request));
	}

	@Test
	public void wasDefaultServletFolderWithCompliantSetting() {
		UrlPathHelper.websphereComplianceFlag = true;
		try {
			request.setAttribute(WEBSPHERE_URI_ATTRIBUTE, "/test/foo/");
			tomcatDefaultServletFolder();
		}finally {
			UrlPathHelper.websphereComplianceFlag = false;
		}
	}


	// /foo/* mapping
	@Test
	public void tomcatCasualServletRoot() {
		request.setContextPath("/test");
		request.setPathInfo("/");
		request.setServletPath("/foo");
		request.setRequestURI("/test/foo/");
		assertEquals("/", helper.getLookupPathForRequest(request));
	}

	// test the root mapping for /foo/* w/o a trailing slash - <host>/<context>/foo
	@Test @Ignore
	public void tomcatCasualServletRootWithMissingSlash() {
		request.setContextPath("/test");
		request.setPathInfo(null);
		request.setServletPath("/foo");
		request.setRequestURI("/test/foo");
		assertEquals("/", helper.getLookupPathForRequest(request));
	}

	@Test
	public void tomcatCasualServletFile() {
		request.setContextPath("/test");
		request.setPathInfo("/foo");
		request.setServletPath("/foo");
		request.setRequestURI("/test/foo/foo");
		assertEquals("/foo", helper.getLookupPathForRequest(request));
	}

	@Test
	public void tomcatCasualServletFolder() {
		request.setContextPath("/test");
		request.setPathInfo("/foo/");
		request.setServletPath("/foo");
		request.setRequestURI("/test/foo/foo/");
		assertEquals("/foo/", helper.getLookupPathForRequest(request));
	}

	@Test
	public void wasCasualServletRoot() {
		request.setContextPath("/test");
		request.setPathInfo(null);
		request.setServletPath("/foo/");
		request.setRequestURI("/test/foo/");
		request.setAttribute(WEBSPHERE_URI_ATTRIBUTE, "/test/foo/");
		assertEquals("/", helper.getLookupPathForRequest(request));
	}

	@Test
	public void wasCasualServletRootWithCompliantSetting() {
		request.setAttribute(WEBSPHERE_URI_ATTRIBUTE, "/test/foo/");
		tomcatCasualServletRoot();
	}

	// test the root mapping for /foo/* w/o a trailing slash - <host>/<context>/foo
	@Ignore
	@Test
	public void wasCasualServletRootWithMissingSlash() {
		request.setContextPath("/test");
		request.setPathInfo(null);
		request.setServletPath("/foo");
		request.setRequestURI("/test/foo");
		request.setAttribute(WEBSPHERE_URI_ATTRIBUTE, "/test/foo");
		assertEquals("/", helper.getLookupPathForRequest(request));
	}

	@Ignore
	@Test
	public void wasCasualServletRootWithMissingSlashWithCompliantSetting() {
		request.setAttribute(WEBSPHERE_URI_ATTRIBUTE, "/test/foo");
		tomcatCasualServletRootWithMissingSlash();
	}

	@Test
	public void wasCasualServletFile() {
		request.setContextPath("/test");
		request.setPathInfo("/foo");
		request.setServletPath("/foo");
		request.setRequestURI("/test/foo/foo");
		request.setAttribute(WEBSPHERE_URI_ATTRIBUTE, "/test/foo/foo");
		assertEquals("/foo", helper.getLookupPathForRequest(request));
	}

	@Test
	public void wasCasualServletFileWithCompliantSetting() {
		request.setAttribute(WEBSPHERE_URI_ATTRIBUTE, "/test/foo/foo");
		tomcatCasualServletFile();
	}

	@Test
	public void wasCasualServletFolder() {
		request.setContextPath("/test");
		request.setPathInfo("/foo/");
		request.setServletPath("/foo");
		request.setRequestURI("/test/foo/foo/");
		request.setAttribute(WEBSPHERE_URI_ATTRIBUTE, "/test/foo/foo/");

		assertEquals("/foo/", helper.getLookupPathForRequest(request));
	}

	@Test
	public void wasCasualServletFolderWithCompliantSetting() {
		request.setAttribute(WEBSPHERE_URI_ATTRIBUTE, "/test/foo/foo/");
		tomcatCasualServletFolder();
	}

	@Test
	public void getOriginatingRequestUri() {
		request.setAttribute(WebUtils.FORWARD_REQUEST_URI_ATTRIBUTE, "/path");
		request.setRequestURI("/forwarded");
		assertEquals("/path", helper.getOriginatingRequestUri(request));
	}

	@Test
	public void getOriginatingRequestUriWebsphere() {
		request.setAttribute(WEBSPHERE_URI_ATTRIBUTE, "/path");
		request.setRequestURI("/forwarded");
		assertEquals("/path", helper.getOriginatingRequestUri(request));
	}

	@Test
	public void getOriginatingRequestUriDefault() {
		request.setRequestURI("/forwarded");
		assertEquals("/forwarded", helper.getOriginatingRequestUri(request));
	}

	@Test
	public void getOriginatingQueryString() {
		request.setQueryString("forward=on");
		request.setAttribute(WebUtils.FORWARD_REQUEST_URI_ATTRIBUTE, "/path");
		request.setAttribute(WebUtils.FORWARD_QUERY_STRING_ATTRIBUTE, "original=on");
		assertEquals("original=on", this.helper.getOriginatingQueryString(request));
	}

	@Test
	public void getOriginatingQueryStringNotPresent() {
		request.setQueryString("forward=true");
		assertEquals("forward=true", this.helper.getOriginatingQueryString(request));
	}

	@Test
	public void getOriginatingQueryStringIsNull() {
		request.setQueryString("forward=true");
		request.setAttribute(WebUtils.FORWARD_REQUEST_URI_ATTRIBUTE, "/path");
		assertNull(this.helper.getOriginatingQueryString(request));
	}

}
