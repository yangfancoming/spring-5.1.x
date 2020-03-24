

package org.springframework.aop;

/**
 * Marker interface implemented by all AOP proxies. Used to detect whether or not objects are Spring-generated proxies.
 * @since 2.0.1
 * @see org.springframework.aop.support.AopUtils#isAopProxy(Object)
 */
public interface SpringProxy {

}
