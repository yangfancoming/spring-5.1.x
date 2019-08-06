
package org.springframework.beans.factory.support.security.support;

import org.springframework.beans.factory.DisposableBean;


public class DestroyBean implements DisposableBean {

	@Override
	public void destroy() throws Exception {
		System.setProperty("security.destroy", "true");
	}
}
