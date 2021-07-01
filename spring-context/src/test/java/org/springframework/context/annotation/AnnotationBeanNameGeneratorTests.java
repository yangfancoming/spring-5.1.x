

package org.springframework.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import example.scannable.DefaultNamedComponent;
import org.junit.Test;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static org.junit.Assert.*;


public class AnnotationBeanNameGeneratorTests {

	private AnnotationBeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

	// 测试  基于@Component("walden")生成bean名称
	@Test
	public void generateBeanNameWithNamedComponent() {
		AnnotatedBeanDefinition bd = new AnnotatedGenericBeanDefinition(ComponentWithName.class);
		String beanName = beanNameGenerator.generateBeanName(bd, null);
		assertNotNull("The generated beanName must *never* be null.", beanName);
		assertTrue("The generated beanName must *never* be blank.", StringUtils.hasText(beanName));
		assertEquals("walden", beanName);
	}

	// 测试  基于	String value() default "thoreau1";  生成bean名称
	@Test
	public void generateBeanNameWithDefaultNamedComponent() {
		AnnotatedBeanDefinition bd = new AnnotatedGenericBeanDefinition(DefaultNamedComponent.class);
		String beanName = beanNameGenerator.generateBeanName(bd, null);
		assertNotNull("The generated beanName must *never* be null.", beanName);
		assertTrue("The generated beanName must *never* be blank.", StringUtils.hasText(beanName));
		assertEquals("thoreau", beanName);
	}

	// 测试 AnnotationBeanNameGenerator 类的 generateBeanName 方法的 buildDefaultBeanName 分支
	@Test
	public void generateBeanNameWithNamedComponentWhereTheNameIsBlank() {
		AnnotatedBeanDefinition bd = new AnnotatedGenericBeanDefinition(ComponentWithBlankName.class);
		String beanName = beanNameGenerator.generateBeanName(bd, null);
		assertNotNull("The generated beanName must *never* be null.", beanName);
		assertTrue("The generated beanName must *never* be blank.", StringUtils.hasText(beanName));
		String expectedGeneratedBeanName = beanNameGenerator.buildDefaultBeanName(bd);// annotationBeanNameGeneratorTests.ComponentWithBlankName
		assertEquals(expectedGeneratedBeanName, beanName);
	}

	@Test
	public void generateBeanNameWithAnonymousComponentYieldsGeneratedBeanName() {
		AnnotatedBeanDefinition bd = new AnnotatedGenericBeanDefinition(AnonymousComponent.class);
		String beanName = beanNameGenerator.generateBeanName(bd, null);
		assertNotNull("The generated beanName must *never* be null.", beanName);
		assertTrue("The generated beanName must *never* be blank.", StringUtils.hasText(beanName));
		String expectedGeneratedBeanName = beanNameGenerator.buildDefaultBeanName(bd);// annotationBeanNameGeneratorTests.AnonymousComponent
		assertEquals(expectedGeneratedBeanName, beanName);
	}

	// 测试 @Service("henry") 注解生成的bean名称
	@Test
	public void generateBeanNameFromMetaComponentWithStringValue() {
		AnnotatedBeanDefinition bd = new AnnotatedGenericBeanDefinition(ComponentFromStringMeta.class);
		String beanName = beanNameGenerator.generateBeanName(bd, null);
		assertEquals("henry", beanName);
	}

	// 测试 @NonStringMetaComponent(123) 注解 生成的bean名称
	@Test
	public void generateBeanNameFromMetaComponentWithNonStringValue() {
		AnnotatedBeanDefinition bd = new AnnotatedGenericBeanDefinition(ComponentFromNonStringMeta.class);
		String beanName = beanNameGenerator.generateBeanName(bd, null);
		assertEquals("annotationBeanNameGeneratorTests.ComponentFromNonStringMeta", beanName);
	}

	/**
	 * @since 4.0.1
	 * @see https://jira.spring.io/browse/SPR-11360
	 */
	@Test
	public void generateBeanNameFromComposedControllerAnnotationWithoutName() {

		AnnotatedBeanDefinition bd = new AnnotatedGenericBeanDefinition(ComposedControllerAnnotationWithoutName.class);
		String beanName = beanNameGenerator.generateBeanName(bd, null);
		String expectedGeneratedBeanName = beanNameGenerator.buildDefaultBeanName(bd);
		assertEquals(expectedGeneratedBeanName, beanName);
	}

	/**
	 * @since 4.0.1
	 * @see https://jira.spring.io/browse/SPR-11360
	 */
	@Test
	public void generateBeanNameFromComposedControllerAnnotationWithBlankName() {
		AnnotatedBeanDefinition bd = new AnnotatedGenericBeanDefinition(ComposedControllerAnnotationWithBlankName.class);
		String beanName = beanNameGenerator.generateBeanName(bd, null);
		String expectedGeneratedBeanName = beanNameGenerator.buildDefaultBeanName(bd);
		assertEquals(expectedGeneratedBeanName, beanName);
	}

	/**
	 * @since 4.0.1
	 * @see https://jira.spring.io/browse/SPR-11360
	 */
	@Test
	public void generateBeanNameFromComposedControllerAnnotationWithStringValue() {
		AnnotatedBeanDefinition bd = new AnnotatedGenericBeanDefinition(ComposedControllerAnnotationWithStringValue.class);
		String beanName = beanNameGenerator.generateBeanName(bd, null);
		assertEquals("restController", beanName);
	}

	@Component("walden")
	private static class ComponentWithName {}

	@Component(" ")
	private static class ComponentWithBlankName {}

	@Component
	private static class AnonymousComponent {}

	@Service("henry")
	private static class ComponentFromStringMeta {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Component
	public @interface NonStringMetaComponent {
		long value();
	}

	@NonStringMetaComponent(123)
	private static class ComponentFromNonStringMeta {}
	
	/**
	 * @see org.springframework.web.bind.annotation.RestController
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Controller
	public static @interface TestRestController {
		String value() default "";
	}

	@TestRestController
	public static class ComposedControllerAnnotationWithoutName {}

	@TestRestController(" ")
	public static class ComposedControllerAnnotationWithBlankName {}

	@TestRestController("restController")
	public static class ComposedControllerAnnotationWithStringValue {}
}
