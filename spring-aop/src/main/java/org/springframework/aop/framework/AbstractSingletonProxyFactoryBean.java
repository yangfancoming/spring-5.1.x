

package org.springframework.aop.framework;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/**
 * Convenient superclass for {@link FactoryBean} types that produce singleton-scoped proxy objects.
 * Manages pre- and post-interceptors (references, rather than
 * interceptor names, as in {@link ProxyFactoryBean}) and provides consistent interface management.
 * @since 2.0
 */
@SuppressWarnings("serial")
public abstract class AbstractSingletonProxyFactoryBean extends ProxyConfig implements FactoryBean<Object>, BeanClassLoaderAware, InitializingBean {

	@Nullable
	private Object target;

	@Nullable
	private Class<?>[] proxyInterfaces;

	@Nullable
	private Object[] preInterceptors;

	@Nullable
	private Object[] postInterceptors;

	/** Default is global AdvisorAdapterRegistry. */
	private AdvisorAdapterRegistry advisorAdapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();

	@Nullable
	private transient ClassLoader proxyClassLoader;

	@Nullable
	private Object proxy;

	/**
	 * Set the target object, that is, the bean to be wrapped with a transactional proxy.
	 * The target may be any object, in which case a SingletonTargetSource will
	 * be created. If it is a TargetSource, no wrapper TargetSource is created:
	 * This enables the use of a pooling or prototype TargetSource etc.
	 * @see org.springframework.aop.TargetSource
	 * @see org.springframework.aop.target.SingletonTargetSource
	 * @see org.springframework.aop.target.LazyInitTargetSource
	 * @see org.springframework.aop.target.PrototypeTargetSource
	 * @see org.springframework.aop.target.CommonsPool2TargetSource
	 */
	public void setTarget(Object target) {
		this.target = target;
	}

	/**
	 * Specify the set of interfaces being proxied.
	 * If not specified (the default), the AOP infrastructure works
	 * out which interfaces need proxying by analyzing the target,
	 * proxying all the interfaces that the target object implements.
	 */
	public void setProxyInterfaces(Class<?>[] proxyInterfaces) {
		this.proxyInterfaces = proxyInterfaces;
	}

	/**
	 * Set additional interceptors (or advisors) to be applied before the
	 * implicit transaction interceptor, e.g. a PerformanceMonitorInterceptor.
	 * You may specify any AOP Alliance MethodInterceptors or other
	 * Spring AOP Advices, as well as Spring AOP Advisors.
	 * @see org.springframework.aop.interceptor.PerformanceMonitorInterceptor
	 */
	public void setPreInterceptors(Object[] preInterceptors) {
		this.preInterceptors = preInterceptors;
	}

	/**
	 * Set additional interceptors (or advisors) to be applied after the
	 * implicit transaction interceptor.
	 * You may specify any AOP Alliance MethodInterceptors or other
	 * Spring AOP Advices, as well as Spring AOP Advisors.
	 */
	public void setPostInterceptors(Object[] postInterceptors) {
		this.postInterceptors = postInterceptors;
	}

	/**
	 * Specify the AdvisorAdapterRegistry to use.
	 * Default is the global AdvisorAdapterRegistry.
	 * @see org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry
	 */
	public void setAdvisorAdapterRegistry(AdvisorAdapterRegistry advisorAdapterRegistry) {
		this.advisorAdapterRegistry = advisorAdapterRegistry;
	}

	/**
	 * Set the ClassLoader to generate the proxy class in.
	 * Default is the bean ClassLoader, i.e. the ClassLoader used by the
	 * containing BeanFactory for loading all bean classes. This can be
	 * overridden here for specific proxies.
	 */
	public void setProxyClassLoader(ClassLoader classLoader) {
		this.proxyClassLoader = classLoader;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		if (this.proxyClassLoader == null) {
			this.proxyClassLoader = classLoader;
		}
	}


	@Override
	public void afterPropertiesSet() {
		if (target == null) throw new IllegalArgumentException("Property 'target' is required");
		if (target instanceof String) {
			throw new IllegalArgumentException("'target' needs to be a bean reference, not a bean name as value");
		}
		if (proxyClassLoader == null) {
			proxyClassLoader = ClassUtils.getDefaultClassLoader();
		}
		ProxyFactory proxyFactory = new ProxyFactory();
		if (preInterceptors != null) {
			for (Object interceptor : preInterceptors) {
				proxyFactory.addAdvisor(advisorAdapterRegistry.wrap(interceptor));
			}
		}
		// Add the main interceptor (typically an Advisor).
		proxyFactory.addAdvisor(advisorAdapterRegistry.wrap(createMainInterceptor()));
		if (postInterceptors != null) {
			for (Object interceptor : postInterceptors) {
				proxyFactory.addAdvisor(advisorAdapterRegistry.wrap(interceptor));
			}
		}
		proxyFactory.copyFrom(this);

		TargetSource targetSource = createTargetSource(target);
		proxyFactory.setTargetSource(targetSource);

		if (proxyInterfaces != null) {
			proxyFactory.setInterfaces(proxyInterfaces);
		}else if (!isProxyTargetClass()) {
			// Rely on AOP infrastructure to tell us what interfaces to proxy.
			Class<?> targetClass = targetSource.getTargetClass();
			if (targetClass != null) {
				proxyFactory.setInterfaces(ClassUtils.getAllInterfacesForClass(targetClass, proxyClassLoader));
			}
		}
		postProcessProxyFactory(proxyFactory);
		proxy = proxyFactory.getProxy(proxyClassLoader);
	}

	/**
	 * Determine a TargetSource for the given target (or TargetSource).
	 * @param target target. If this is an implementation of TargetSource it is
	 * used as our TargetSource; otherwise it is wrapped in a SingletonTargetSource.
	 * @return a TargetSource for this object
	 */
	protected TargetSource createTargetSource(Object target) {
		if (target instanceof TargetSource) {
			return (TargetSource) target;
		}else {
			return new SingletonTargetSource(target);
		}
	}

	/**
	 * A hook for subclasses to post-process the {@link ProxyFactory}
	 * before creating the proxy instance with it.
	 * @param proxyFactory the AOP ProxyFactory about to be used
	 * @since 4.2
	 */
	protected void postProcessProxyFactory(ProxyFactory proxyFactory) {
	}

	@Override
	public Object getObject() {
		if (proxy == null) {
			throw new FactoryBeanNotInitializedException();
		}
		return proxy;
	}

	@Override
	@Nullable
	public Class<?> getObjectType() {
		if (proxy != null) {
			return proxy.getClass();
		}
		if (proxyInterfaces != null && proxyInterfaces.length == 1) {
			return proxyInterfaces[0];
		}
		if (target instanceof TargetSource) {
			return ((TargetSource) target).getTargetClass();
		}
		if (target != null) {
			return target.getClass();
		}
		return null;
	}

	@Override
	public final boolean isSingleton() {
		return true;
	}

	/**
	 * Create the "main" interceptor for this proxy factory bean.
	 * Typically an Advisor, but can also be any type of Advice.
	 * Pre-interceptors will be applied before, post-interceptors
	 * will be applied after this interceptor.
	 */
	protected abstract Object createMainInterceptor();

}
