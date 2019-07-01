

@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package org.springframework.core.env

/**
 * Extension for [PropertyResolver.getProperty] providing Array like getter returning a
 * nullable [String].
 *
 * ```kotlin
 * val name = env["name"] ?: "Seb"
 * ```
 *
 * @author Sebastien Deleuze
 * @since 5.0
 */
operator fun PropertyResolver.get(key: String) : String? = getProperty(key)


/**
 * Extension for [PropertyResolver.getProperty] providing a `getProperty<Foo>(...)`
 * variant returning a nullable [String].
 *
 * @author Sebastien Deleuze
 * @since 5.1
 */
inline fun <reified T: Any?> PropertyResolver.getProperty(key: String) : T? =
		getProperty(key, T::class.java)

/**
 * Extension for [PropertyResolver.getRequiredProperty] providing a
 * `getRequiredProperty<Foo>(...)` variant.
 *
 * @author Sebastien Deleuze
 * @since 5.1
 */
inline fun <reified T: Any> PropertyResolver.getRequiredProperty(key: String) : T =
		getRequiredProperty(key, T::class.java)
