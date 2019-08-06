
package org.springframework.beans.factory.support.security.support;


public class CustomCallbackBean {

	public void init() {
		System.getProperties();
	}

	public void destroy() {
		System.setProperty("security.destroy", "true");
	}
}
