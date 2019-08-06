
package org.springframework.beans.factory.support.security.support;

import org.springframework.beans.factory.InitializingBean;


public class InitBean implements InitializingBean {

	@Override
	public void afterPropertiesSet()  {
		System.getProperties();
	}
}
