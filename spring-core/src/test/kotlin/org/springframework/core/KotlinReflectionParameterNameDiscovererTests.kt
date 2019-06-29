
package org.springframework.core

import org.junit.Test

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.springframework.util.ReflectionUtils

/**
 * Tests for KotlinReflectionParameterNameDiscoverer
 */
class KotlinReflectionParameterNameDiscovererTests {

	private val parameterNameDiscoverer = KotlinReflectionParameterNameDiscoverer()

	@Test
	fun getParameterNamesOnInterface() {
		val method = ReflectionUtils.findMethod(MessageService::class.java,"sendMessage", String::class.java)!!
		val actualParams = parameterNameDiscoverer.getParameterNames(method)
		assertThat(actualParams, `is`(arrayOf("message")))
	}

	@Test
	fun getParameterNamesOnClass() {
		val method = ReflectionUtils.findMethod(MessageServiceImpl::class.java,"sendMessage", String::class.java)!!
		val actualParams = parameterNameDiscoverer.getParameterNames(method)
		assertThat(actualParams, `is`(arrayOf("message")))
	}

	@Test
	fun getParameterNamesOnExtensionMethod() {
		val method = ReflectionUtils.findMethod(UtilityClass::class.java, "identity", String::class.java)!!
		val actualParams = parameterNameDiscoverer.getParameterNames(method)!!
		assertThat(actualParams, `is`(arrayOf("\$receiver")))
	}

	interface MessageService {
		fun sendMessage(message: String)
	}

	class MessageServiceImpl {
		fun sendMessage(message: String) = message
	}

	class UtilityClass {
		fun String.identity() = this
	}
}
