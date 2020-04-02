

package org.springframework.aop.framework;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import org.springframework.aop.SpringProxy;

/**
 * Default {@link AopProxyFactory} implementation, creating either a CGLIB proxy or a JDK dynamic proxy.
 * Creates a CGLIB proxy if one the following is true for a given
 * {@link AdvisedSupport} instance:
 * <li>the {@code optimize} flag is set
 * <li>the {@code proxyTargetClass} flag is set
 * <li>no proxy interfaces have been specified
 * In general, specify {@code proxyTargetClass} to enforce a CGLIB proxy,
 * or specify one or more interfaces to use a JDK dynamic proxy.
 * @since 12.03.2004
 * @see AdvisedSupport#setOptimize
 * @see AdvisedSupport#setProxyTargetClass
 * @see AdvisedSupport#setInterfaces
 *
 * aop代理 aop动态代理 根源  aop根源
 */
@SuppressWarnings("serial")
public class DefaultAopProxyFactory implements AopProxyFactory, Serializable {

	@Override
	public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
		// 判断当前类是否需要进行运行时优化，或者是指定了使用Cglib代理的方式，再或者是目标类没有用户提供的
		// 相关接口，则使用Cglib代理实现代理逻辑的织入
		if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {
			Class<?> targetClass = config.getTargetClass();
			if (targetClass == null) {
				throw new AopConfigException("TargetSource cannot determine target class: Either an interface or a target is required for proxy creation.");
			}
			// 如果被代理的类是一个接口，或者被代理的类是使用Jdk代理生成的类，此时还是使用Jdk代理
			if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
				return new JdkDynamicAopProxy(config);
			}
			// 返回Cglib代理织入类对象
			return new ObjenesisCglibAopProxy(config); // 使用 cglib 代理
		}else {
			// 返回Jdk代理织入类对象
			return new JdkDynamicAopProxy(config);  // 使用 jdk 代理
		}
	}

	/**
	 * Determine whether the supplied {@link AdvisedSupport} has only the
	 * {@link org.springframework.aop.SpringProxy} interface specified
	 * (or no proxy interfaces specified at all).
	 */
	private boolean hasNoUserSuppliedProxyInterfaces(AdvisedSupport config) {
		Class<?>[] ifcs = config.getProxiedInterfaces();
		return (ifcs.length == 0 || (ifcs.length == 1 && SpringProxy.class.isAssignableFrom(ifcs[0])));
	}

}
