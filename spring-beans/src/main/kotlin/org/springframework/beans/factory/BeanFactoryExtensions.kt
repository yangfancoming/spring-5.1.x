

package org.springframework.beans.factory

import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.ResolvableType

/**
 * Extension for [BeanFactory.getBean] providing a `getBean<Foo>()` variant.
 *
 * @author Sebastien Deleuze
 * @since 5.0
 */
inline fun <reified T : Any> BeanFactory.getBean(): T = getBean(T::class.java)

/**
 * Extension for [BeanFactory.getBean] providing a `getBean<Foo>("foo")` variant.
 *
 * @see BeanFactory.getBean(String, Class<T>)
 * @author Sebastien Deleuze
 * @since 5.0
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified T : Any> BeanFactory.getBean(name: String): T =
		getBean(name, T::class.java)

/**
 * Extension for [BeanFactory.getBean] providing a `getBean<Foo>(arg1, arg2)` variant.
 *
 * @see BeanFactory.getBean(Class<T>, Object...)
 * @author Sebastien Deleuze
 * @since 5.0
 */
inline fun <reified T : Any> BeanFactory.getBean(vararg args:Any): T =
		getBean(T::class.java, *args)

/**
 * Extension for [BeanFactory.getBeanProvider] providing a `getBeanProvider<Foo>()` variant.
 * This extension is not subject to type erasure and retains actual generic type arguments.
 *
 * @see BeanFactory.getBeanProvider(ResolvableType)
 * @author Sebastien Deleuze
 * @since 5.1
 */
inline fun <reified T : Any> BeanFactory.getBeanProvider(): ObjectProvider<T> =
		getBeanProvider(ResolvableType.forType((object : ParameterizedTypeReference<T>() {}).type))

