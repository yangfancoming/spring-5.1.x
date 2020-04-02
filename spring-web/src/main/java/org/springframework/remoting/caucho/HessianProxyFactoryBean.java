

package org.springframework.remoting.caucho;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;

/**
 * {@link FactoryBean} for Hessian proxies. Exposes the proxied service
 * for use as a bean reference, using the specified service interface.
 *
 * Hessian is a slim, binary RPC protocol.
 * For information on Hessian, see the
 * <a href="http://hessian.caucho.com">Hessian website</a>
 * <b>Note: As of Spring 4.0, this proxy factory requires Hessian 4.0 or above.</b>
 *
 * The service URL must be an HTTP URL exposing a Hessian service.
 * For details, see the {@link HessianClientInterceptor} javadoc.
 *

 * @since 13.05.2003
 * @see #setServiceInterface
 * @see #setServiceUrl
 * @see HessianClientInterceptor
 * @see HessianServiceExporter
 * @see org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean
 * @see org.springframework.remoting.rmi.RmiProxyFactoryBean
 */
public class HessianProxyFactoryBean extends HessianClientInterceptor implements FactoryBean<Object> {

	@Nullable
	private Object serviceProxy;


	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		this.serviceProxy = new ProxyFactory(getServiceInterface(), this).getProxy(getBeanClassLoader());
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
