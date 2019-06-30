

package org.springframework.beans.factory.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.support.StaticApplicationContext;
import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.springframework.util.ClassUtils.*;

public class QualifierAnnotationTests {

	// org.springframework.beans.factory.xml.QualifierAnnotationTests 
	private static final String CLASSNAME = QualifierAnnotationTests.class.getName();
	
	// classpath:org/springframework/beans/factory/xml/QualifierAnnotationTests-context.xml
	private static final String CONFIG_LOCATION = format("classpath:%s-context.xml", convertClassNameToResourcePath(CLASSNAME));

	StaticApplicationContext context = new StaticApplicationContext();

	@Before
	public void test(){
		BeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
		reader.loadBeanDefinitions(CONFIG_LOCATION); // 加载 QualifierAnnotationTests-context.xml 文件
	}

	// testNonQualifiedFieldFails 不合格字段失败的测试
	@Test
	public void testNonQualifiedFieldFails() {
		context.registerSingleton("testBean", NonQualifiedTestBean.class);// 注册为单例模式  并将属性键值对传入，value可以为引用类型
		try {
			context.refresh();
			fail("Should have thrown a BeanCreationException");
		}
		catch (BeanCreationException e) {
			assertTrue(e.getMessage().contains("found 6"));
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void testQualifiedByValue() {
		context.registerSingleton("testBean", QualifiedByValueTestBean.class);
		context.refresh();
		QualifiedByValueTestBean testBean = (QualifiedByValueTestBean) context.getBean("testBean");
		Person person = testBean.getLarry();
		assertEquals("Larry", person.getName());
		System.out.println(person.getName());
	}

	@Test
	public void testQualifiedByBeanName() {
		context.registerSingleton("testBean", QualifiedByBeanNameTestBean.class);
		context.refresh();
		QualifiedByBeanNameTestBean testBean = (QualifiedByBeanNameTestBean) context.getBean("testBean");
		Person person = testBean.getLarry();
		assertEquals("LarryBean", person.getName());
		assertTrue(testBean.myProps != null && testBean.myProps.isEmpty());
		System.out.println(person.getName());
		System.out.println(testBean.myProps);
	}

	@Test
	public void testQualifiedByParentValue() {
		StaticApplicationContext parent = new StaticApplicationContext();
		GenericBeanDefinition parentLarry = new GenericBeanDefinition();
		parentLarry.setBeanClass(Person.class);
		parentLarry.getPropertyValues().add("name", "ParentLarry");
		parentLarry.addQualifier(new AutowireCandidateQualifier(Qualifier.class, "parentLarry"));
		parent.registerBeanDefinition("someLarry", parentLarry);
		GenericBeanDefinition otherLarry = new GenericBeanDefinition();
		otherLarry.setBeanClass(Person.class);
		otherLarry.getPropertyValues().add("name", "OtherLarry");
		otherLarry.addQualifier(new AutowireCandidateQualifier(Qualifier.class, "otherLarry"));
		parent.registerBeanDefinition("someOtherLarry", otherLarry);
		parent.refresh();
		StaticApplicationContext context = new StaticApplicationContext(parent);
		context.registerSingleton("testBean", QualifiedByParentValueTestBean.class);
		context.refresh();
		QualifiedByParentValueTestBean testBean = (QualifiedByParentValueTestBean) context.getBean("testBean");
		Person person = testBean.getLarry();
		assertEquals("ParentLarry", person.getName());
	}



	@Test
	public void testQualifiedByFieldName() {
		context.registerSingleton("testBean", QualifiedByFieldNameTestBean.class);
		context.refresh();
		QualifiedByFieldNameTestBean testBean = (QualifiedByFieldNameTestBean) context.getBean("testBean");
		Person person = testBean.getLarry();
		assertEquals("LarryBean", person.getName());
	}

	@Test
	public void testQualifiedByParameterName() {
		

		context.registerSingleton("testBean", QualifiedByParameterNameTestBean.class);
		context.refresh();
		QualifiedByParameterNameTestBean testBean = (QualifiedByParameterNameTestBean) context.getBean("testBean");
		Person person = testBean.getLarry();
		assertEquals("LarryBean", person.getName());
	}

	@Test
	public void testQualifiedByAlias() {
		

		context.registerSingleton("testBean", QualifiedByAliasTestBean.class);
		context.refresh();
		QualifiedByAliasTestBean testBean = (QualifiedByAliasTestBean) context.getBean("testBean");
		Person person = testBean.getStooge();
		assertEquals("LarryBean", person.getName());
	}

	@Test
	public void testQualifiedByAnnotation() {
		

		context.registerSingleton("testBean", QualifiedByAnnotationTestBean.class);
		context.refresh();
		QualifiedByAnnotationTestBean testBean = (QualifiedByAnnotationTestBean) context.getBean("testBean");
		Person person = testBean.getLarry();
		assertEquals("LarrySpecial", person.getName());
	}

	@Test
	public void testQualifiedByCustomValue() {
		

		context.registerSingleton("testBean", QualifiedByCustomValueTestBean.class);
		context.refresh();
		QualifiedByCustomValueTestBean testBean = (QualifiedByCustomValueTestBean) context.getBean("testBean");
		Person person = testBean.getCurly();
		assertEquals("Curly", person.getName());
	}

	@Test
	public void testQualifiedByAnnotationValue() {
		

		context.registerSingleton("testBean", QualifiedByAnnotationValueTestBean.class);
		context.refresh();
		QualifiedByAnnotationValueTestBean testBean = (QualifiedByAnnotationValueTestBean) context.getBean("testBean");
		Person person = testBean.getLarry();
		assertEquals("LarrySpecial", person.getName());
	}

	@Test
	public void testQualifiedByAttributesFailsWithoutCustomQualifierRegistered() {
		

		context.registerSingleton("testBean", QualifiedByAttributesTestBean.class);
		try {
			context.refresh();
			fail("should have thrown a BeanCreationException");
		}
		catch (BeanCreationException e) {
			assertTrue(e.getMessage().contains("found 6"));
		}
	}

	@Test
	public void testQualifiedByAttributesWithCustomQualifierRegistered() {
		

		QualifierAnnotationAutowireCandidateResolver resolver = (QualifierAnnotationAutowireCandidateResolver)
				context.getDefaultListableBeanFactory().getAutowireCandidateResolver();
		resolver.addQualifierType(MultipleAttributeQualifier.class);
		context.registerSingleton("testBean", MultiQualifierClient.class);
		context.refresh();

		MultiQualifierClient testBean = (MultiQualifierClient) context.getBean("testBean");

		assertNotNull( testBean.factoryTheta);
		assertNotNull( testBean.implTheta);
	}


	@SuppressWarnings("unused")
	private static class NonQualifiedTestBean {
		@Autowired
		private Person anonymous;
		public Person getAnonymous() {
			return anonymous;
		}
	}


	private static class QualifiedByValueTestBean {
		@Autowired
		@Qualifier("larry") // sos  @Qualifier 注解区分大小写
		private Person larry;
		public Person getLarry() {
			return larry;
		}
	}

	private static class QualifiedByBeanNameTestBean {
		@Autowired
		@Qualifier("larryBean")
		private Person larry;

		@Autowired
		@Qualifier("testProperties")
		public Properties myProps;

		public Person getLarry() {
			return larry;
		}
	}

	private static class QualifiedByParentValueTestBean {

		@Autowired
		@Qualifier("parentLarry")
		private Person larry;

		public Person getLarry() {
			return larry;
		}
	}

	private static class QualifiedByFieldNameTestBean {

		@Autowired
		private Person larryBean;

		public Person getLarry() {
			return larryBean;
		}
	}


	private static class QualifiedByParameterNameTestBean {

		private Person larryBean;

		@Autowired
		public void setLarryBean(Person larryBean) {
			this.larryBean = larryBean;
		}

		public Person getLarry() {
			return larryBean;
		}
	}


	private static class QualifiedByAliasTestBean {

		@Autowired @Qualifier("stooge")
		private Person stooge;

		public Person getStooge() {
			return stooge;
		}
	}


	private static class QualifiedByAnnotationTestBean {

		@Autowired @Qualifier("special")
		private Person larry;

		public Person getLarry() {
			return larry;
		}
	}


	private static class QualifiedByCustomValueTestBean {

		@Autowired @SimpleValueQualifier("curly")
		private Person curly;

		public Person getCurly() {
			return curly;
		}
	}


	private static class QualifiedByAnnotationValueTestBean {

		@Autowired @SimpleValueQualifier("special")
		private Person larry;

		public Person getLarry() {
			return larry;
		}
	}


	@SuppressWarnings("unused")
	private static class QualifiedByAttributesTestBean {

		@Autowired @MultipleAttributeQualifier(name="moe", age=42)
		private Person moeSenior;

		@Autowired @MultipleAttributeQualifier(name="moe", age=15)
		private Person moeJunior;

		public Person getMoeSenior() {
			return moeSenior;
		}

		public Person getMoeJunior() {
			return moeJunior;
		}
	}


	@SuppressWarnings("unused")
	private static class Person {
		private String name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}


	@Qualifier("special")
	@SimpleValueQualifier("special")
	private static class SpecialPerson extends Person {
	}


	@Target({ElementType.FIELD, ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	@Qualifier
	public @interface SimpleValueQualifier {

		String value() default "";
	}


	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface MultipleAttributeQualifier {

		String name();

		int age();
	}


	private static final String FACTORY_QUALIFIER = "FACTORY";

	private static final String IMPL_QUALIFIER = "IMPL";


	public static class MultiQualifierClient {

		@Autowired @Qualifier(FACTORY_QUALIFIER)
		public Theta factoryTheta;

		@Autowired @Qualifier(IMPL_QUALIFIER)
		public Theta implTheta;
	}


	public interface Theta {
	}


	@Qualifier(IMPL_QUALIFIER)
	public static class ThetaImpl implements Theta {
	}


	@Qualifier(FACTORY_QUALIFIER)
	public static class QualifiedFactoryBean implements FactoryBean<Theta> {

		@Override
		public Theta getObject() {
			return new Theta() {};
		}

		@Override
		public Class<Theta> getObjectType() {
			return Theta.class;
		}

		@Override
		public boolean isSingleton() {
			return true;
		}
	}

}
