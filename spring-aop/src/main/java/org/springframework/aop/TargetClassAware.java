

package org.springframework.aop;

import org.springframework.lang.Nullable;

/**
 * Minimal interface for exposing the target class behind a proxy.
 * Implemented by AOP proxy objects and proxy factories
 * (via {@link org.springframework.aop.framework.Advised}) as well as by {@link TargetSource TargetSources}.
 * @since 2.0.3
 * @see org.springframework.aop.support.AopUtils#getTargetClass(Object)
 * 所有的Aop代理对象或者代理工厂（proxy factory)都要实现的接口，该接口用于暴露出被代理目标对象类型
 */
public interface TargetClassAware {

	/**
	 * Return the target class behind the implementing object
	 * (typically a proxy configuration or an actual proxy).
	 * @return the target Class, or {@code null} if not known
	 */
	@Nullable
	Class<?> getTargetClass();

}
