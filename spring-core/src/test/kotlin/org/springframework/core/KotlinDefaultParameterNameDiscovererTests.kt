

package org.springframework.core

import org.junit.Assert.assertEquals
import org.junit.Test

class KotlinDefaultParameterNameDiscovererTests {

	private val parameterNameDiscoverer = DefaultParameterNameDiscoverer()

	enum class MyEnum {
		ONE, TWO
	}

	@Test  // SPR-16931
	fun getParameterNamesOnEnum() {
		val constructor = MyEnum::class.java.declaredConstructors[0]
		val actualParams = parameterNameDiscoverer.getParameterNames(constructor)
		assertEquals(2, actualParams!!.size)
	}
}
