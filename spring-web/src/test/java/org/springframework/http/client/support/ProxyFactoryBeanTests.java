

package org.springframework.http.client.support;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class ProxyFactoryBeanTests {

	ProxyFactoryBean factoryBean;

	@Before
	public void setUp() {
		factoryBean = new ProxyFactoryBean();
	}

	@Test(expected = IllegalArgumentException.class)
	public void noType() {
		factoryBean.setType(null);
		factoryBean.afterPropertiesSet();
	}

	@Test(expected = IllegalArgumentException.class)
	public void noHostname() {
		factoryBean.setHostname("");
		factoryBean.afterPropertiesSet();
	}

	@Test(expected = IllegalArgumentException.class)
	public void noPort() {
		factoryBean.setHostname("example.com");
		factoryBean.afterPropertiesSet();
	}

	@Test
	public void normal() {
		Proxy.Type type = Proxy.Type.HTTP;
		factoryBean.setType(type);
		String hostname = "example.com";
		factoryBean.setHostname(hostname);
		int port = 8080;
		factoryBean.setPort(port);
		factoryBean.afterPropertiesSet();

		Proxy result = factoryBean.getObject();

		assertEquals(type, result.type());
		InetSocketAddress address = (InetSocketAddress) result.address();
		assertEquals(hostname, address.getHostName());
		assertEquals(port, address.getPort());
	}

}
