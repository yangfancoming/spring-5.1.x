

package org.springframework.remoting.jaxws;

import javax.xml.ws.Service;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

/**
 * {@link org.springframework.beans.factory.FactoryBean} for locally
 * defined JAX-WS Service references.
 * Uses {@link LocalJaxWsServiceFactory}'s facilities underneath.
 *
 * Alternatively, JAX-WS Service references can be looked up
 * in the JNDI environment of the Java EE container.
 *

 * @since 2.5
 * @see javax.xml.ws.Service
 * @see org.springframework.jndi.JndiObjectFactoryBean
 * @see JaxWsPortProxyFactoryBean
 */
public class LocalJaxWsServiceFactoryBean extends LocalJaxWsServiceFactory
		implements FactoryBean<Service>, InitializingBean {

	@Nullable
	private Service service;


	@Override
	public void afterPropertiesSet() {
		this.service = createJaxWsService();
	}

	@Override
	@Nullable
	public Service getObject() {
		return this.service;
	}

	@Override
	public Class<? extends Service> getObjectType() {
		return (this.service != null ? this.service.getClass() : Service.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
