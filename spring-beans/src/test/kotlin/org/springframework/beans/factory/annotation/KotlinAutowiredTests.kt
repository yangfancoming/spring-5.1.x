

package org.springframework.beans.factory.annotation

import org.junit.Test

import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.tests.sample.beans.TestBean

import org.junit.Assert.*
import org.springframework.beans.factory.BeanCreationException
import org.springframework.tests.sample.beans.Colour

/**
 * Tests for Kotlin support with [Autowired].
 *
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 */
class KotlinAutowiredTests {

	@Test
	fun `Autowiring with target`() {
		val bf = DefaultListableBeanFactory()
		val bpp = AutowiredAnnotationBeanPostProcessor()
		bpp.setBeanFactory(bf)
		bf.addBeanPostProcessor(bpp)
		val bd = RootBeanDefinition(KotlinBean::class.java)
		bd.scope = RootBeanDefinition.SCOPE_PROTOTYPE
		bf.registerBeanDefinition("annotatedBean", bd)
		val tb = TestBean()
		bf.registerSingleton("testBean", tb)

		val kb = bf.getBean("annotatedBean", KotlinBean::class.java)
		assertSame(tb, kb.injectedFromConstructor)
		assertSame(tb, kb.injectedFromMethod)
		assertSame(tb, kb.injectedField)
	}

	@Test
	fun `Autowiring without target`() {
		val bf = DefaultListableBeanFactory()
		val bpp = AutowiredAnnotationBeanPostProcessor()
		bpp.setBeanFactory(bf)
		bf.addBeanPostProcessor(bpp)
		val bd = RootBeanDefinition(KotlinBean::class.java)
		bd.scope = RootBeanDefinition.SCOPE_PROTOTYPE
		bf.registerBeanDefinition("annotatedBean", bd)

		val kb = bf.getBean("annotatedBean", KotlinBean::class.java)
		assertNull(kb.injectedFromConstructor)
		assertNull(kb.injectedFromMethod)
		assertNull(kb.injectedField)
	}
	
	@Test  // SPR-15847
	fun `Autowiring by primary constructor with mandatory and optional parameters`() {
		val bf = DefaultListableBeanFactory()
		val bpp = AutowiredAnnotationBeanPostProcessor()
		bpp.setBeanFactory(bf)
		bf.addBeanPostProcessor(bpp)
		val bd = RootBeanDefinition(KotlinBeanWithMandatoryAndOptionalParameters::class.java)
		bd.scope = RootBeanDefinition.SCOPE_PROTOTYPE
		bf.registerBeanDefinition("bean", bd)
		val tb = TestBean()
		bf.registerSingleton("testBean", tb)

		val kb = bf.getBean("bean", KotlinBeanWithMandatoryAndOptionalParameters::class.java)
		assertSame(tb, kb.injectedFromConstructor)
		assertEquals("foo", kb.optional)
		assertEquals("bar", kb.initializedField)
	}

	@Test
	fun `Autowiring by primary constructor with optional parameters`() {
		val bf = DefaultListableBeanFactory()
		val bpp = AutowiredAnnotationBeanPostProcessor()
		bpp.setBeanFactory(bf)
		bf.addBeanPostProcessor(bpp)
		val bd = RootBeanDefinition(KotlinBeanWithOptionalParameters::class.java)
		bd.scope = RootBeanDefinition.SCOPE_PROTOTYPE
		bf.registerBeanDefinition("bean", bd)

		val kb = bf.getBean("bean", KotlinBeanWithOptionalParameters::class.java)
		assertNotNull(kb.optional1)
		assertEquals("foo", kb.optional2)
		assertEquals("bar", kb.initializedField)
	}

	@Test  // SPR-15847
	fun `Autowiring by annotated primary constructor with optional parameter`() {
		val bf = DefaultListableBeanFactory()
		val bpp = AutowiredAnnotationBeanPostProcessor()
		bpp.setBeanFactory(bf)
		bf.addBeanPostProcessor(bpp)
		val bd = RootBeanDefinition(KotlinBeanWithOptionalParameterAndExplicitConstructor::class.java)
		bd.scope = RootBeanDefinition.SCOPE_PROTOTYPE
		bf.registerBeanDefinition("bean", bd)
		val tb = TestBean()
		bf.registerSingleton("testBean", tb)

		val kb = bf.getBean("bean", KotlinBeanWithOptionalParameterAndExplicitConstructor::class.java)
		assertSame(tb, kb.injectedFromConstructor)
		assertEquals("foo", kb.optional)
	}

	@Test  // SPR-15847
	fun `Autowiring by annotated secondary constructor with optional parameter`() {
		val bf = DefaultListableBeanFactory()
		val bpp = AutowiredAnnotationBeanPostProcessor()
		bpp.setBeanFactory(bf)
		bf.addBeanPostProcessor(bpp)
		val bd = RootBeanDefinition(KotlinBeanWithAutowiredSecondaryConstructor::class.java)
		bd.scope = RootBeanDefinition.SCOPE_PROTOTYPE
		bf.registerBeanDefinition("bean", bd)
		val tb = TestBean()
		bf.registerSingleton("testBean", tb)
		val colour = Colour.BLUE
		bf.registerSingleton("colour", colour)

		val kb = bf.getBean("bean", KotlinBeanWithAutowiredSecondaryConstructor::class.java)
		assertSame(tb, kb.injectedFromConstructor)
		assertEquals("bar", kb.optional)
		assertSame(colour, kb.injectedFromSecondaryConstructor)
	}

	@Test  // SPR-16012
	fun `Fallback on the default constructor when no autowirable primary constructor is defined`() {
		val bf = DefaultListableBeanFactory()
		val bpp = AutowiredAnnotationBeanPostProcessor()
		bpp.setBeanFactory(bf)
		bf.addBeanPostProcessor(bpp)
		val bd = RootBeanDefinition(KotlinBeanWithPrimaryAndDefaultConstructors::class.java)
		bd.scope = RootBeanDefinition.SCOPE_PROTOTYPE
		bf.registerBeanDefinition("bean", bd)

		val kb = bf.getBean("bean", KotlinBeanWithPrimaryAndDefaultConstructors::class.java)
		assertNotNull(kb.testBean)
	}

	@Test  // SPR-16012
	fun `Instantiation via primary constructor when a default is defined`() {
		val bf = DefaultListableBeanFactory()
		val bpp = AutowiredAnnotationBeanPostProcessor()
		bpp.setBeanFactory(bf)
		bf.addBeanPostProcessor(bpp)
		val bd = RootBeanDefinition(KotlinBeanWithPrimaryAndDefaultConstructors::class.java)
		bd.scope = RootBeanDefinition.SCOPE_PROTOTYPE
		bf.registerBeanDefinition("bean", bd)
		val tb = TestBean()
		bf.registerSingleton("testBean", tb)

		val kb = bf.getBean("bean", KotlinBeanWithPrimaryAndDefaultConstructors::class.java)
		assertEquals(tb, kb.testBean)
	}

	@Test  // SPR-16289
	fun `Instantiation via secondary constructor when a default primary is defined`() {
		val bf = DefaultListableBeanFactory()
		val bpp = AutowiredAnnotationBeanPostProcessor()
		bpp.setBeanFactory(bf)
		bf.addBeanPostProcessor(bpp)
		val bd = RootBeanDefinition(KotlinBeanWithPrimaryAndSecondaryConstructors::class.java)
		bd.scope = RootBeanDefinition.SCOPE_PROTOTYPE
		bf.registerBeanDefinition("bean", bd)

		bf.getBean(KotlinBeanWithPrimaryAndSecondaryConstructors::class.java, "foo")
		bf.getBean(KotlinBeanWithPrimaryAndSecondaryConstructors::class.java)
	}

	@Test(expected = BeanCreationException::class)  // SPR-16022
	fun `No autowiring with primary and secondary non annotated constructors`() {
		val bf = DefaultListableBeanFactory()
		val bpp = AutowiredAnnotationBeanPostProcessor()
		bpp.setBeanFactory(bf)
		bf.addBeanPostProcessor(bpp)
		val bd = RootBeanDefinition(KotlinBeanWithSecondaryConstructor::class.java)
		bd.scope = RootBeanDefinition.SCOPE_PROTOTYPE
		bf.registerBeanDefinition("bean", bd)
		val tb = TestBean()
		bf.registerSingleton("testBean", tb)
		val colour = Colour.BLUE
		bf.registerSingleton("colour", colour)

		bf.getBean("bean", KotlinBeanWithSecondaryConstructor::class.java)
	}


	class KotlinBean(val injectedFromConstructor: TestBean?) {
		
		var injectedFromMethod: TestBean? = null

		@Autowired
		var injectedField: TestBean? = null

		@Autowired
		fun injectedMethod(p1: TestBean?) {
			injectedFromMethod = p1
		}
	}

	class KotlinBeanWithMandatoryAndOptionalParameters(
			val injectedFromConstructor: TestBean,
			val optional: String = "foo"
	) {
		var initializedField: String? = null

		init {
			initializedField = "bar"
		}
	}

	class KotlinBeanWithOptionalParameters(
			val optional1: TestBean = TestBean(),
			val optional2: String = "foo"
	) {
		var initializedField: String? = null

		init {
			initializedField = "bar"
		}
	}

	class KotlinBeanWithOptionalParameterAndExplicitConstructor @Autowired constructor(
			val optional: String = "foo",
			val injectedFromConstructor: TestBean
	)

	class KotlinBeanWithAutowiredSecondaryConstructor(
			val optional: String = "foo",
			val injectedFromConstructor: TestBean
	) {
		@Autowired constructor(injectedFromSecondaryConstructor: Colour, injectedFromConstructor: TestBean,
							   optional: String = "bar") : this(optional, injectedFromConstructor) {
			this.injectedFromSecondaryConstructor = injectedFromSecondaryConstructor
		}

		var injectedFromSecondaryConstructor: Colour? = null
	}

	@Suppress("unused")
	class KotlinBeanWithPrimaryAndDefaultConstructors(val testBean: TestBean) {
		constructor() : this(TestBean())
	}

	@Suppress("unused", "UNUSED_PARAMETER")
	class KotlinBeanWithPrimaryAndSecondaryConstructors() {
		constructor(p: String) : this()
	}

	class KotlinBeanWithSecondaryConstructor(
			val optional: String = "foo",
			val injectedFromConstructor: TestBean
	) {
		constructor(injectedFromSecondaryConstructor: Colour, injectedFromConstructor: TestBean,
					optional: String = "bar") : this(optional, injectedFromConstructor) {
			this.injectedFromSecondaryConstructor = injectedFromSecondaryConstructor
		}

		var injectedFromSecondaryConstructor: Colour? = null
	}

}
