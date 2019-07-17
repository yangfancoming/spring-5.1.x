

package org.springframework.context.support;

import org.springframework.beans.factory.BeanCreationNotAllowedException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * @author Alef Arendsen

 */
public class Service implements ApplicationContextAware, MessageSourceAware, DisposableBean {

	private ApplicationContext applicationContext;

	private MessageSource messageSource;

	private Resource[] resources;

	private boolean properlyDestroyed = false;


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		if (this.messageSource != null) {
			throw new IllegalArgumentException("MessageSource should not be set twice");
		}
		this.messageSource = messageSource;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setResources(Resource[] resources) {
		this.resources = resources;
	}

	public Resource[] getResources() {
		return resources;
	}


	@Override
	public void destroy() {
		this.properlyDestroyed = true;
		Thread thread = new Thread() {
			@Override
			public void run() {
				Assert.state(applicationContext.getBean("messageSource") instanceof StaticMessageSource,
						"Invalid MessageSource bean");
				try {
					applicationContext.getBean("service2");
					// Should have thrown BeanCreationNotAllowedException
					properlyDestroyed = false;
				}
				catch (BeanCreationNotAllowedException ex) {
					// expected
				}
			}
		};
		thread.start();
		try {
			thread.join();
		}
		catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	public boolean isProperlyDestroyed() {
		return properlyDestroyed;
	}

}
