

package org.springframework.beans.factory;

/**
 * A marker superinterface indicating that a bean is eligible to be notified by the Spring container of a particular framework object through a callback-style method.
 * 一个标记超接口，指示bean可以通过回调样式方法由特定框架对象的Spring容器通知。
 * The actual method signature is determined by individual subinterfaces but should typically consist of just one void-returning method that accepts a single argument.
 * 实际方法签名由各个子接口确定，但通常只包含一个接受单个参数的返回void的方法。
 *
 * <p>Note that merely implementing {@link Aware} provides no default functionality.
 * Rather, processing must be done explicitly, for example in a
 * {@link org.springframework.beans.factory.config.BeanPostProcessor}.
 * Refer to {@link org.springframework.context.support.ApplicationContextAwareProcessor}
 * for an example of processing specific {@code *Aware} interface callbacks.


 * @since 3.1
 */
public interface Aware {

}
