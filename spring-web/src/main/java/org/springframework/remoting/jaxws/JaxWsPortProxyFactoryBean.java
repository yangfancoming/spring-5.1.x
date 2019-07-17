

package org.springframework.remoting.jaxws;

import javax.xml.ws.BindingProvider;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link org.springframework.beans.factory.FactoryBean} for a specific port of a
 * JAX-WS service. Exposes a proxy for the port, to be used for bean references.
 * Inherits configuration properties from {@link JaxWsPortClientInterceptor}.
 *

 * @since 2.5
 * @see #setServiceInterface
 * @see LocalJaxWsServiceFactoryBean
 */
public class JaxWsPortProxyFactoryBean extends JaxWsPortClientInterceptor implements FactoryBean<Object> {

	@Nullable
	private Object serviceProxy;


	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();

		Class<?> ifc = getServiceInterface();
		Assert.notNull(ifc, "Property 'serviceInterface' is required");

		// Build a proxy that also exposes the JAX-WS BindingProvider interface.
		ProxyFactory pf = new ProxyFactory();
		pf.addInterface(ifc);
		pf.addInterface(BindingProvider.class);
		pf.addAdvice(this);
		this.serviceProxy = pf.getProxy(getBeanClassLoader());
	}


	@Override
	@Nullable
	public Object getObject() {
		return this.serviceProxy;
	}

	@Override
	public Class<?> getObjectType() {
		return getServiceInterface();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
