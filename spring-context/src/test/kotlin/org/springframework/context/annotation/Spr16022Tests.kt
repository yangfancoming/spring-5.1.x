
package org.springframework.context.annotation

import org.junit.Assert.assertEquals
import org.junit.Test
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.getBean
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * @author Sebastien Deleuze
 */
class Spr16022Tests {

	@Test
	fun `Register beans with multiple constructors with AnnotationConfigApplicationContext`() {
		assert(AnnotationConfigApplicationContext(Config::class.java))
	}

	@Test
	fun `Register beans with multiple constructors with ClassPathXmlApplicationContext`() {
		assert(ClassPathXmlApplicationContext(CONTEXT))
	}

	private fun assert(context: BeanFactory) {
		val bean1 = context.getBean<MultipleConstructorsTestBean>("bean1")
		assertEquals(0, bean1.foo)
		val bean2 = context.getBean<MultipleConstructorsTestBean>("bean2")
		assertEquals(1, bean2.foo)
		val bean3 = context.getBean<MultipleConstructorsTestBean>("bean3")
		assertEquals(3, bean3.foo)

	}

	@Suppress("unused")
	class MultipleConstructorsTestBean(val foo: Int) {
		constructor(bar: String) : this(bar.length)
		constructor(foo: Int, bar: String) : this(foo + bar.length)
	}

	@Configuration @ImportResource(CONTEXT)
	open class Config
}

private const val CONTEXT = "org/springframework/context/annotation/multipleConstructors.xml"