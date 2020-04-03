

package org.springframework.orm.jpa.hibernate.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public abstract class TestBean {
	private BeanSource source;

	private String name;

	@Autowired
	private ApplicationContext applicationContext;

	public BeanSource getSource() {
		return source;
	}

	public void setSource(BeanSource source) {
		this.source = source;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
