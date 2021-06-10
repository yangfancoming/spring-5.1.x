

package org.springframework.web.servlet.handler;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BeanNameUrlHandlerMappingTests {

	public static final String CONF = "/org/springframework/web/servlet/handler/map1.xml";

	private ConfigurableWebApplicationContext wac;

	/**
	 * map1.xml 配置文件内容
	 * 	<bean id="handlerMapping" class="org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping"/>
	 * 	<bean id="godCtrl" name="/ /mypath/welcome.html /mypath/show.html /mypath/bookseats.html /mypath/reservation.html /mypath/payment.html /mypath/confirmation.html /mypath/test*" class="java.lang.Object"/>
	*/
	@Before
	public void setUp() throws Exception {
		MockServletContext sc = new MockServletContext("");
		wac = new XmlWebApplicationContext();
		wac.setServletContext(sc);
		wac.setConfigLocations(new String[] {CONF});
		wac.refresh();
	}

	// 请求uri 没有对应控制器的情况
	@Test
	public void requestsWithoutHandlers() throws Exception {
		HandlerMapping hm = (HandlerMapping) wac.getBean("handlerMapping");
		MockHttpServletRequest req = new MockHttpServletRequest("GET", "/mypath/nonsense.html");
		req.setContextPath("/myapp");
		Object h = hm.getHandler(req);
		assertTrue("Handler is null", h == null);

		req = new MockHttpServletRequest("GET", "/foo/bar/baz.html");
		h = hm.getHandler(req);
		assertTrue("Handler is null", h == null);
	}

	@Test
	public void requestsWithSubPaths() throws Exception {
		HandlerMapping hm = (HandlerMapping) wac.getBean("handlerMapping");
		doTestRequestsWithSubPaths(hm);
	}

	@Test
	public void requestsWithSubPathsInParentContext() throws Exception {
		BeanNameUrlHandlerMapping hm = new BeanNameUrlHandlerMapping();
		hm.setDetectHandlersInAncestorContexts(true);
		hm.setApplicationContext(new StaticApplicationContext(wac));
		doTestRequestsWithSubPaths(hm);
	}

	private void doTestRequestsWithSubPaths(HandlerMapping hm) throws Exception {
		Object bean = wac.getBean("godCtrl");

		MockHttpServletRequest req = new MockHttpServletRequest("GET", "/mypath/welcome.html");
		HandlerExecutionChain hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest("GET", "/myapp/mypath/welcome.html");
		req.setContextPath("/myapp");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest("GET", "/myapp/mypath/welcome.html");
		req.setContextPath("/myapp");
		req.setServletPath("/mypath/welcome.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest("GET", "/myapp/myservlet/mypath/welcome.html");
		req.setContextPath("/myapp");
		req.setServletPath("/myservlet");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest("GET", "/myapp/myapp/mypath/welcome.html");
		req.setContextPath("/myapp");
		req.setServletPath("/myapp");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest("GET", "/mypath/show.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest("GET", "/mypath/bookseats.html");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);
	}

	@Test
	public void requestsWithFullPaths() throws Exception {
		BeanNameUrlHandlerMapping hm = new BeanNameUrlHandlerMapping();
		hm.setAlwaysUseFullPath(true);
		hm.setApplicationContext(wac);
		Object bean = wac.getBean("godCtrl");

		MockHttpServletRequest req = new MockHttpServletRequest("GET", "/mypath/welcome.html");
		HandlerExecutionChain hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest("GET", "/myapp/mypath/welcome.html");
		req.setContextPath("/myapp");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest("GET", "/mypath/welcome.html");
		req.setContextPath("");
		req.setServletPath("/mypath");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest("GET", "/Myapp/mypath/welcome.html");
		req.setContextPath("/myapp");
		req.setServletPath("/mypath");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest("GET", "/");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);
	}

	@Test
	public void asteriskMatches() throws Exception {
		HandlerMapping hm = (HandlerMapping) wac.getBean("handlerMapping");
		Object bean = wac.getBean("godCtrl");

		MockHttpServletRequest req = new MockHttpServletRequest("GET", "/mypath/test.html");
		HandlerExecutionChain hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest("GET", "/mypath/testarossa");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest("GET", "/mypath/tes");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec == null);
	}

	@Test
	public void overlappingMappings() throws Exception {
		BeanNameUrlHandlerMapping hm = (BeanNameUrlHandlerMapping) wac.getBean("handlerMapping");
		Object anotherHandler = new Object();
		hm.registerHandler("/mypath/testaross*", anotherHandler);
		Object bean = wac.getBean("godCtrl");

		MockHttpServletRequest req = new MockHttpServletRequest("GET", "/mypath/test.html");
		HandlerExecutionChain hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == bean);

		req = new MockHttpServletRequest("GET", "/mypath/testarossa");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec != null && hec.getHandler() == anotherHandler);

		req = new MockHttpServletRequest("GET", "/mypath/tes");
		hec = hm.getHandler(req);
		assertTrue("Handler is correct bean", hec == null);
	}

	@Test
	public void doubleMappings() {
		BeanNameUrlHandlerMapping hm = (BeanNameUrlHandlerMapping) wac.getBean("handlerMapping");
		try {
			hm.registerHandler("/mypath/welcome.html", new Object());
			fail("Should have thrown IllegalStateException");
		}catch (IllegalStateException ex) {
			// expected
		}
	}
}
