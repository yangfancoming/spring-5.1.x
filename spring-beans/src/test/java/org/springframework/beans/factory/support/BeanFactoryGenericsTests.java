package org.springframework.beans.factory.support;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.core.OverridingClassLoader;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.tests.Assume;
import org.springframework.tests.TestGroup;
import org.springframework.tests.sample.beans.GenericBean;
import org.springframework.tests.sample.beans.GenericIntegerBean;
import org.springframework.tests.sample.beans.GenericSetOfIntegerBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.tests.sample.objects.goat.GoatObject;
import static org.junit.Assert.*;

/**
 * @since 20.01.2006
 */
public class BeanFactoryGenericsTests {

	DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

	/**
	 * 测试  通过 MutablePropertyValues 对象，给bean的 Set<Integer> integerSet 属性赋值的功能
	 * 走 无参构造函数
	*/
	@Test
	public void testGenericSetProperty() {
		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		Set<String> input = new HashSet<String>(){{add("4");add("5");}};
		// 通过 setIntegerSet 方法 给GenericBean对象的 integerSet 属性注入值
		rbd.getPropertyValues().add("integerSet", input);
		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");
		assertTrue(gb.getIntegerSet().contains(new Integer(4)));
		assertTrue(gb.getIntegerSet().contains(new Integer(5)));
	}

	/**
	 * 测试  通过 MutablePropertyValues 对象，给bean的 List<Resource> resourceList 属性赋值的功能
	 * 走 无参构造函数
	 */
	@Test
	public void testGenericListProperty() throws Exception {
		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		List<String> input = new ArrayList<>();
		input.add("http://localhost:8080");
		input.add("http://localhost:9090");
		rbd.getPropertyValues().add("resourceList", input);
		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");
		assertEquals(new UrlResource("http://localhost:8080"), gb.getResourceList().get(0));
		assertEquals(new UrlResource("http://localhost:9090"), gb.getResourceList().get(1));
	}

	/**
	 * 测试 通过AUTOWIRE_BY_TYPE自动注入方式为  private List<Resource> resourceList; 属性赋值功能
	 * 走 无参构造函数
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(org.springframework.beans.factory.ListableBeanFactory, java.lang.Class, boolean, boolean)
	 * @see DefaultListableBeanFactory#getBeanNamesForType(java.lang.Class, boolean, boolean)
	 * @see DefaultListableBeanFactory#doGetBeanNamesForType(org.springframework.core.ResolvableType, boolean, boolean)
	*/
	@Test
	public void testGenericListPropertyWithAutowiring() throws Exception {
		// 手动注册2个单例bean
		bf.registerSingleton("resource1", new UrlResource("http://localhost:8080"));
		bf.registerSingleton("resource2", new UrlResource("http://localhost:9090"));
		RootBeanDefinition rbd = new RootBeanDefinition(GenericIntegerBean.class);
		rbd.setAutowireMode(RootBeanDefinition.AUTOWIRE_BY_TYPE); // AUTOWIRE_BY_NAME AUTOWIRE_BY_TYPE
		bf.registerBeanDefinition("genericBean", rbd);
		// 走 getBean doCreateBean populate initializeBean invokeInitMethod 流程，再populate中 寻找依赖
		GenericIntegerBean gb = (GenericIntegerBean) bf.getBean("genericBean");
		assertEquals(new UrlResource("http://localhost:8080"), gb.getResourceList().get(0));
		assertEquals(new UrlResource("http://localhost:9090"), gb.getResourceList().get(1));
	}

	/**
	 * @see TypeConverterDelegate#convertIfNecessary(java.lang.String, java.lang.Object, java.lang.Object, java.lang.Class, org.springframework.core.convert.TypeDescriptor)
	*/
	@Test
	public void testGenericListPropertyWithInvalidElementType() {
		RootBeanDefinition rbd = new RootBeanDefinition(GenericIntegerBean.class);
		List<Integer> input = new ArrayList<Integer>(){{add(1);}};
		// 通过 setTestBeanList 方法 给GenericBean对象的 testBeanList 属性注入值
		rbd.getPropertyValues().add("testBeanList", input);
		bf.registerBeanDefinition("genericBean", rbd);
		try {
			// 走流程时 报错，因为 List<Integer>  与  List<TestBean> 类型不匹配
			bf.getBean("genericBean");
			fail("Should have thrown BeanCreationException");
		}catch (BeanCreationException ex) {
			System.out.println(ex);
			assertTrue(ex.getMessage().contains("genericBean") && ex.getMessage().contains("testBeanList[0]"));
			assertTrue(ex.getMessage().contains(TestBean.class.getName()) && ex.getMessage().contains("Integer"));
		}
	}

	// 没懂 啥意思
	@Test
	public void testGenericListPropertyWithOptionalAutowiring() {
		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		rbd.setAutowireMode(RootBeanDefinition.AUTOWIRE_BY_TYPE);
		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");
		assertNull(gb.getResourceList());
	}

	/**
	 * 测试  通过 MutablePropertyValues 对象，给bean的 Map<Short, Integer> shortMap; 属性赋值的功能
	 * 走 无参构造函数
	 * 将 Map<String, String> 类型 注入到 Map<Short, Integer> shortMap 类型中
	 * @see DefaultListableBeanFactory#resolveMultipleBeans(org.springframework.beans.factory.config.DependencyDescriptor, java.lang.String, java.util.Set, org.springframework.beans.TypeConverter)
	*/
	@Test
	public void testGenericMapProperty() {
		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		Map<String, String> input = new HashMap<String, String>() {{put("4", "5");put("6", "7");}};
		rbd.getPropertyValues().add("shortMap", input);
		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");
		assertEquals(new Integer(5), gb.getShortMap().get(new Short("4")));
		assertEquals(new Integer(7), gb.getShortMap().get(new Short("6")));
	}

	/**
	 * xml 方式
	 * 测试 private ArrayList<String[]> listOfArrays;  属性的注入
	*/
	@Test
	public void testGenericListOfArraysProperty() {
		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("genericBeanTests.xml", getClass()));
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("listOfArrays");
		assertEquals(1, gb.getListOfArrays().size());
		String[] array = gb.getListOfArrays().get(0);
		assertEquals(2, array.length);
		assertEquals("value1", array[0]);
		assertEquals("value2", array[1]);
	}

	/**
	 * 测试  通过 指定构造函数 注入方式  给GenericBean对象的 integerSet 属性注入值
	 * GenericBean 单参 integerSet 构造函数 执行
	 * @see AbstractAutowireCapableBeanFactory#determineConstructorsFromBeanPostProcessors(java.lang.Class, java.lang.String)
	 * @see AbstractAutowireCapableBeanFactory#autowireConstructor(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.reflect.Constructor[], java.lang.Object[])
	 * @see Constructor#newInstance(java.lang.Object...)
	 */
	@Test
	public void testGenericSetConstructor() {
		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		// 默认情况为null
		assertFalse(rbd.hasConstructorArgumentValues());
		// 匿名内部类+初始化块
		Set<String> input = new HashSet<String>(){{add("4");add("5");}};
		// 设置：通过 构造函数注入方式
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
		assertTrue(rbd.hasConstructorArgumentValues());
		bf.registerBeanDefinition("genericBean", rbd);
		// 使用：getBean 触发构造函数注入
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");
		assertTrue(gb.getIntegerSet().contains(new Integer(4)));
		assertTrue(gb.getIntegerSet().contains(new Integer(5)));
	}

	/**
	 * 自定义测试用例：从指定构造方法中获取参数名称列表
	*/
	@Test
	public void testParameterNameDiscoverer() {
		// 参数类型及个数
		Class[] classes = new Class[]{Set.class};
		// 从指定类中根据参数个数和类型，获取唯一的构造函数
		Constructor constructor = ReflectUtils.getConstructor(GenericBean.class,classes);
		// 最终处理的实现类为： LocalVariableTableParameterNameDiscoverer
		ParameterNameDiscoverer parameterNameDiscoverer = bf.getParameterNameDiscoverer();
		assertNotNull(parameterNameDiscoverer);
		String[] paramNames = parameterNameDiscoverer.getParameterNames(constructor);
		assertEquals("integerSet",paramNames[0]);
	}

	/**
	 * 自定义测试用例： 判断构造方法上是否有 ConstructorProperties 注解，若有，则取注解中的值
	 */
	@Test
	public void testConstructorPropertiesChecker() {
		Class[] classes = new Class[]{String.class,Integer.class};
		Constructor constructor = ReflectUtils.getConstructor(GoatObject.class,classes);
		Class[] parameterTypes = constructor.getParameterTypes();
		// 判断否则方法是否有 ConstructorProperties 注解，若有，则取注解中的值
		String[] evaluate = ConstructorResolver.ConstructorPropertiesChecker.evaluate(constructor, parameterTypes.length);
		System.out.println(evaluate);
	}

	/**
	 * 测试  通过 构造函数 自动注入方式  给GenericBean对象的 integerSet 属性注入值
	 * GenericBean 单参 integerSet 构造函数 执行
	 * @see AbstractAutowireCapableBeanFactory#autowireConstructor(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.reflect.Constructor[], java.lang.Object[])
	 * 同上面测试用例
	 * @see BeanFactoryGenericsTests#testGenericListPropertyWithAutowiring()
	*/
	@Test
	public void testGenericSetConstructorWithAutowiring() {
		bf.registerSingleton("integer1", new Integer(4));
		bf.registerSingleton("integer2", new Integer(5));
		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		// 通过 构造函数 自动注入方式   走 public GenericBean(Set<Integer> integerSet) 单参构造函数
		rbd.setAutowireMode(RootBeanDefinition.AUTOWIRE_CONSTRUCTOR);
		bf.registerBeanDefinition("genericBean", rbd);

		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");
		assertTrue(gb.getIntegerSet().contains(new Integer(4)));
		assertTrue(gb.getIntegerSet().contains(new Integer(5)));
	}

	// 走 无参构造 函数
	@Test
	public void testGenericSetConstructorWithOptionalAutowiring() {
		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		rbd.setAutowireMode(RootBeanDefinition.AUTOWIRE_CONSTRUCTOR);
		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");
		assertNull(gb.getIntegerSet());
	}

	/**
	 *  测试  显示指定使用双参构造函数注入的方式
	 * GenericBean 双参 integerSet 、 resourceList 构造函数 执行
	*/
	@Test
	public void testGenericSetListConstructor() throws Exception {
		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		Set<String> input = new HashSet<String>(){{add("4");add("5");}};
		List<String> input2 = new ArrayList<>();
		input2.add("http://localhost:8080");
		input2.add("http://localhost:9090");
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input2);

		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");
		assertTrue(gb.getIntegerSet().contains(new Integer(4)));
		assertTrue(gb.getIntegerSet().contains(new Integer(5)));
		assertEquals(new UrlResource("http://localhost:8080"), gb.getResourceList().get(0));
		assertEquals(new UrlResource("http://localhost:9090"), gb.getResourceList().get(1));
	}

	/**
	 * 测试 不显示指定构造函数，让系统自动判断注入方式
	 * GenericBean 双参 integerSet 、 resourceList 构造函数 执行
	*/
	@Test
	public void testGenericSetListConstructorWithAutowiring() throws Exception {
		bf.registerSingleton("integer1", new Integer(4));
		bf.registerSingleton("integer2", new Integer(5));
		bf.registerSingleton("resource1", new UrlResource("http://localhost:8080"));
		bf.registerSingleton("resource2", new UrlResource("http://localhost:9090"));

		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		rbd.setAutowireMode(RootBeanDefinition.AUTOWIRE_CONSTRUCTOR);
		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");

		assertTrue(gb.getIntegerSet().contains(new Integer(4)));
		assertTrue(gb.getIntegerSet().contains(new Integer(5)));
		assertEquals(new UrlResource("http://localhost:8080"), gb.getResourceList().get(0));
		assertEquals(new UrlResource("http://localhost:9090"), gb.getResourceList().get(1));
	}

	/**
	 * 测试 不显示指定构造函数，让系统自动判断注入方式
	 * GenericBean  无参 构造函数 执行
	 */
	@Test
	public void testGenericSetListConstructorWithOptionalAutowiring() throws Exception {
		bf.registerSingleton("resource1", new UrlResource("http://localhost:8080"));
		bf.registerSingleton("resource2", new UrlResource("http://localhost:9090"));

		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		rbd.setAutowireMode(RootBeanDefinition.AUTOWIRE_CONSTRUCTOR);
		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");

		assertNull(gb.getIntegerSet());
		assertNull(gb.getResourceList());
	}

	/**
	 * 测试 显示指定使用双参构造函数注入的方式
	 * GenericBean 双参 integerSet 、 shortMap 构造函数 执行
	 */
	@Test
	public void testGenericSetMapConstructor() {
		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		Set<String> input = new HashSet<String>(){{add("4");add("5");}};
		Map<String, String> input2 = new HashMap<String, String>() {{put("4", "5");put("6", "7");}};
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input2);

		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");

		assertTrue(gb.getIntegerSet().contains(new Integer(4)));
		assertTrue(gb.getIntegerSet().contains(new Integer(5)));
		assertEquals(new Integer(5), gb.getShortMap().get(new Short("4")));
		assertEquals(new Integer(7), gb.getShortMap().get(new Short("6")));
	}

	/**
	 * 测试 显示指定使用双参构造函数注入的方式
	 * GenericBean 双参 shortMap 、 resource 构造函数 执行
	 */
	@Test
	public void testGenericMapResourceConstructor() throws Exception {
		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		Map<String, String> input = new HashMap<String, String>() {{put("4", "5");put("6", "7");}};
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
		rbd.getConstructorArgumentValues().addGenericArgumentValue("http://localhost:8080");

		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");

		assertEquals(new Integer(5), gb.getShortMap().get(new Short("4")));
		assertEquals(new Integer(7), gb.getShortMap().get(new Short("6")));
		assertEquals(new UrlResource("http://localhost:8080"), gb.getResourceList().get(0));
	}

	/**
	 * 测试 显示指定使用双参构造函数注入的方式
	 * GenericBean 双参 plainMap 、 shortMap 构造函数 执行
	 */
	@Test
	public void testGenericMapMapConstructor() {
		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		Map<String, String> input = new HashMap<String, String>() {{put("1", "0");put("2", "3");}};
		Map<String, String> input2 = new HashMap<String, String>() {{put("4", "5");put("6", "7");}};
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input2);

		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");

		assertNotSame(gb.getPlainMap(), gb.getShortMap());
		assertEquals(2, gb.getPlainMap().size());
		assertEquals("0", gb.getPlainMap().get("1"));
		assertEquals("3", gb.getPlainMap().get("2"));
		assertEquals(2, gb.getShortMap().size());
		assertEquals(new Integer(5), gb.getShortMap().get(new Short("4")));
		assertEquals(new Integer(7), gb.getShortMap().get(new Short("6")));
	}

	// todo
	@Test
	public void testGenericMapMapConstructorWithSameRefAndConversion() {
		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		Map<String, String> input = new HashMap<String, String>() {{put("1", "0");put("2", "3");}};
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);

		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");

		assertNotSame(gb.getPlainMap(), gb.getShortMap());
		assertEquals(2, gb.getPlainMap().size());
		assertEquals("0", gb.getPlainMap().get("1"));
		assertEquals("3", gb.getPlainMap().get("2"));
		assertEquals(2, gb.getShortMap().size());
		assertEquals(new Integer(0), gb.getShortMap().get(new Short("1")));
		assertEquals(new Integer(3), gb.getShortMap().get(new Short("2")));
	}

	@Test
	public void testGenericMapMapConstructorWithSameRefAndNoConversion() {
		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		Map<Short, Integer> input = new HashMap<>();
		input.put(new Short((short) 1), new Integer(0));
		input.put(new Short((short) 2), new Integer(3));
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);

		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");

		assertSame(gb.getPlainMap(), gb.getShortMap());
		assertEquals(2, gb.getShortMap().size());
		assertEquals(new Integer(0), gb.getShortMap().get(new Short("1")));
		assertEquals(new Integer(3), gb.getShortMap().get(new Short("2")));
	}

	@Test
	public void testGenericMapWithKeyTypeConstructor() {
		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		Map<String, String> input = new HashMap<String, String>() {{put("4", "5");put("6", "7");}};
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);

		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");

		assertEquals("5", gb.getLongMap().get(new Long("4")));
		assertEquals("7", gb.getLongMap().get(new Long("6")));
	}

	@Test
	public void testGenericMapWithCollectionValueConstructor() {

		bf.addPropertyEditorRegistrar(new PropertyEditorRegistrar() {
			@Override
			public void registerCustomEditors(PropertyEditorRegistry registry) {
				registry.registerCustomEditor(Number.class, new CustomNumberEditor(Integer.class, false));
			}
		});
		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);

		Map<String, AbstractCollection<?>> input = new HashMap<>();
		HashSet<Integer> value1 = new HashSet<>();
		value1.add(new Integer(1));
		input.put("1", value1);
		ArrayList<Boolean> value2 = new ArrayList<>();
		value2.add(Boolean.TRUE);
		input.put("2", value2);
		rbd.getConstructorArgumentValues().addGenericArgumentValue(Boolean.TRUE);
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);

		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");

		assertTrue(gb.getCollectionMap().get(new Integer(1)) instanceof HashSet);
		assertTrue(gb.getCollectionMap().get(new Integer(2)) instanceof ArrayList);
	}


	// todo   对应bean属性factory-method  使用工厂方法 创建bean实例
	@Test
	public void testGenericSetFactoryMethod() {
		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		rbd.setFactoryMethodName("createInstance");

		Set<String> input = new HashSet<String>(){{add("4");add("5");}};
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);

		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");

		assertTrue(gb.getIntegerSet().contains(new Integer(4)));
		assertTrue(gb.getIntegerSet().contains(new Integer(5)));
	}

	@Test
	public void testGenericSetListFactoryMethod() throws Exception {

		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		rbd.setFactoryMethodName("createInstance");
		Set<String> input = new HashSet<String>(){{add("4");add("5");}};
		List<String> input2 = new ArrayList<>();
		input2.add("http://localhost:8080");
		input2.add("http://localhost:9090");
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input2);

		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");

		assertTrue(gb.getIntegerSet().contains(new Integer(4)));
		assertTrue(gb.getIntegerSet().contains(new Integer(5)));
		assertEquals(new UrlResource("http://localhost:8080"), gb.getResourceList().get(0));
		assertEquals(new UrlResource("http://localhost:9090"), gb.getResourceList().get(1));
	}

	@Test
	public void testGenericSetMapFactoryMethod() {

		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		rbd.setFactoryMethodName("createInstance");

		Set<String> input = new HashSet<String>(){{add("4");add("5");}};
		Map<String, String> input2 = new HashMap<String, String>() {{put("4", "5");put("6", "7");}};
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input2);

		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");

		assertTrue(gb.getIntegerSet().contains(new Integer(4)));
		assertTrue(gb.getIntegerSet().contains(new Integer(5)));
		assertEquals(new Integer(5), gb.getShortMap().get(new Short("4")));
		assertEquals(new Integer(7), gb.getShortMap().get(new Short("6")));
	}

	@Test
	public void testGenericMapResourceFactoryMethod() throws Exception {

		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		rbd.setFactoryMethodName("createInstance");
		Map<String, String> input = new HashMap<String, String>() {{put("4", "5");put("6", "7");}};
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
		rbd.getConstructorArgumentValues().addGenericArgumentValue("http://localhost:8080");

		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");

		assertEquals(new Integer(5), gb.getShortMap().get(new Short("4")));
		assertEquals(new Integer(7), gb.getShortMap().get(new Short("6")));
		assertEquals(new UrlResource("http://localhost:8080"), gb.getResourceList().get(0));
	}

	@Test
	public void testGenericMapMapFactoryMethod() {

		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		rbd.setFactoryMethodName("createInstance");
		Map<String, String> input = new HashMap<String, String>() {{put("1", "0");put("2", "3");}};
		Map<String, String> input2 = new HashMap<String, String>() {{put("4", "5");put("6", "7");}};
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input2);

		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");

		assertEquals("0", gb.getPlainMap().get("1"));
		assertEquals("3", gb.getPlainMap().get("2"));
		assertEquals(new Integer(5), gb.getShortMap().get(new Short("4")));
		assertEquals(new Integer(7), gb.getShortMap().get(new Short("6")));
	}

	@Test
	public void testGenericMapWithKeyTypeFactoryMethod() {

		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		rbd.setFactoryMethodName("createInstance");
		Map<String, String> input = new HashMap<String, String>() {{put("4", "5");put("6", "7");}};
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");
		assertEquals("5", gb.getLongMap().get(new Long("4")));
		assertEquals("7", gb.getLongMap().get(new Long("6")));
	}

	@Test
	public void testGenericMapWithCollectionValueFactoryMethod() {

		bf.addPropertyEditorRegistrar(new PropertyEditorRegistrar() {
			@Override
			public void registerCustomEditors(PropertyEditorRegistry registry) {
				registry.registerCustomEditor(Number.class, new CustomNumberEditor(Integer.class, false));
			}
		});
		RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
		rbd.setFactoryMethodName("createInstance");

		Map<String, AbstractCollection<?>> input = new HashMap<>();
		HashSet<Integer> value1 = new HashSet<>();
		value1.add(new Integer(1));
		input.put("1", value1);
		ArrayList<Boolean> value2 = new ArrayList<>();
		value2.add(Boolean.TRUE);
		input.put("2", value2);
		rbd.getConstructorArgumentValues().addGenericArgumentValue(Boolean.TRUE);
		rbd.getConstructorArgumentValues().addGenericArgumentValue(input);

		bf.registerBeanDefinition("genericBean", rbd);
		GenericBean<?> gb = (GenericBean<?>) bf.getBean("genericBean");

		assertTrue(gb.getCollectionMap().get(new Integer(1)) instanceof HashSet);
		assertTrue(gb.getCollectionMap().get(new Integer(2)) instanceof ArrayList);
	}

	@Test
	public void testGenericListBean() throws Exception {

		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("genericBeanTests.xml", getClass()));
		List<?> list = (List<?>) bf.getBean("list");
		assertEquals(1, list.size());
		assertEquals(new URL("http://localhost:8080"), list.get(0));
	}

	@Test
	public void testGenericSetBean() throws Exception {

		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("genericBeanTests.xml", getClass()));
		Set<?> set = (Set<?>) bf.getBean("set");
		assertEquals(1, set.size());
		assertEquals(new URL("http://localhost:8080"), set.iterator().next());
	}

	@Test
	public void testGenericMapBean() throws Exception {

		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("genericBeanTests.xml", getClass()));
		Map<?, ?> map = (Map<?, ?>) bf.getBean("map");
		assertEquals(1, map.size());
		assertEquals(new Integer(10), map.keySet().iterator().next());
		assertEquals(new URL("http://localhost:8080"), map.values().iterator().next());
	}

	@Test
	public void testGenericallyTypedIntegerBean() {

		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("genericBeanTests.xml", getClass()));
		GenericIntegerBean gb = (GenericIntegerBean) bf.getBean("integerBean");
		assertEquals(new Integer(10), gb.getGenericProperty());
		assertEquals(new Integer(20), gb.getGenericListProperty().get(0));
		assertEquals(new Integer(30), gb.getGenericListProperty().get(1));
	}

	@Test
	public void testGenericallyTypedSetOfIntegerBean() {

		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("genericBeanTests.xml", getClass()));
		GenericSetOfIntegerBean gb = (GenericSetOfIntegerBean) bf.getBean("setOfIntegerBean");
		assertEquals(new Integer(10), gb.getGenericProperty().iterator().next());
		assertEquals(new Integer(20), gb.getGenericListProperty().get(0).iterator().next());
		assertEquals(new Integer(30), gb.getGenericListProperty().get(1).iterator().next());
	}

	@Test
	public void testSetBean() throws Exception {
		Assume.group(TestGroup.LONG_RUNNING);

		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("genericBeanTests.xml", getClass()));
		UrlSet us = (UrlSet) bf.getBean("setBean");
		assertEquals(1, us.size());
		assertEquals(new URL("https://www.springframework.org"), us.iterator().next());
	}

	/**
	 * Tests support for parameterized static {@code factory-method} declarations such as
	 * Mockito's {@code mock()} method which has the following signature.
	 * <pre>
	 * {@code
	 * public static <T> T mock(Class<T> classToMock)
	 * }
	 * </pre>
	 * See SPR-9493
	 */
	@Test
	public void parameterizedStaticFactoryMethod() {
		RootBeanDefinition rbd = new RootBeanDefinition(Mockito.class);
		rbd.setFactoryMethodName("mock");
		rbd.getConstructorArgumentValues().addGenericArgumentValue(Runnable.class);
		bf.registerBeanDefinition("mock", rbd);

		assertEquals(Runnable.class, bf.getType("mock"));
		assertEquals(Runnable.class, bf.getType("mock"));
		Map<String, Runnable> beans = bf.getBeansOfType(Runnable.class);
		assertEquals(1, beans.size());
	}

	/**
	 * Tests support for parameterized instance {@code factory-method} declarations such
	 * as EasyMock's {@code IMocksControl.createMock()} method which has the following
	 * signature.
	 * <pre>
	 * {@code
	 * public <T> T createMock(Class<T> toMock)
	 * }
	 * </pre>
	 * See SPR-10411
	 */
	@Test
	public void parameterizedInstanceFactoryMethod() {
		RootBeanDefinition rbd = new RootBeanDefinition(MocksControl.class);
		bf.registerBeanDefinition("mocksControl", rbd);
		rbd = new RootBeanDefinition();
		rbd.setFactoryBeanName("mocksControl");
		rbd.setFactoryMethodName("createMock");
		rbd.getConstructorArgumentValues().addGenericArgumentValue(Runnable.class);
		bf.registerBeanDefinition("mock", rbd);

		assertTrue(bf.isTypeMatch("mock", Runnable.class));
		assertTrue(bf.isTypeMatch("mock", Runnable.class));
		assertEquals(Runnable.class, bf.getType("mock"));
		assertEquals(Runnable.class, bf.getType("mock"));
		Map<String, Runnable> beans = bf.getBeansOfType(Runnable.class);
		assertEquals(1, beans.size());
	}

	@Test
	public void parameterizedInstanceFactoryMethodWithNonResolvedClassName() {
		RootBeanDefinition rbd = new RootBeanDefinition(MocksControl.class);
		bf.registerBeanDefinition("mocksControl", rbd);

		rbd = new RootBeanDefinition();
		rbd.setFactoryBeanName("mocksControl");
		rbd.setFactoryMethodName("createMock");
		rbd.getConstructorArgumentValues().addGenericArgumentValue(Runnable.class.getName());
		bf.registerBeanDefinition("mock", rbd);

		assertTrue(bf.isTypeMatch("mock", Runnable.class));
		assertTrue(bf.isTypeMatch("mock", Runnable.class));
		assertEquals(Runnable.class, bf.getType("mock"));
		assertEquals(Runnable.class, bf.getType("mock"));
		Map<String, Runnable> beans = bf.getBeansOfType(Runnable.class);
		assertEquals(1, beans.size());
	}

	@Test
	public void parameterizedInstanceFactoryMethodWithWrappedClassName() {
		RootBeanDefinition rbd = new RootBeanDefinition();
		rbd.setBeanClassName(Mockito.class.getName());
		rbd.setFactoryMethodName("mock");
		// TypedStringValue used to be equivalent to an XML-defined argument String
		rbd.getConstructorArgumentValues().addGenericArgumentValue(new TypedStringValue(Runnable.class.getName()));
		bf.registerBeanDefinition("mock", rbd);

		assertTrue(bf.isTypeMatch("mock", Runnable.class));
		assertTrue(bf.isTypeMatch("mock", Runnable.class));
		assertEquals(Runnable.class, bf.getType("mock"));
		assertEquals(Runnable.class, bf.getType("mock"));
		Map<String, Runnable> beans = bf.getBeansOfType(Runnable.class);
		assertEquals(1, beans.size());
	}

	@Test
	public void parameterizedInstanceFactoryMethodWithInvalidClassName() {
		RootBeanDefinition rbd = new RootBeanDefinition(MocksControl.class);
		bf.registerBeanDefinition("mocksControl", rbd);

		rbd = new RootBeanDefinition();
		rbd.setFactoryBeanName("mocksControl");
		rbd.setFactoryMethodName("createMock");
		rbd.getConstructorArgumentValues().addGenericArgumentValue("x");
		bf.registerBeanDefinition("mock", rbd);

		assertFalse(bf.isTypeMatch("mock", Runnable.class));
		assertFalse(bf.isTypeMatch("mock", Runnable.class));
		assertNull(bf.getType("mock"));
		assertNull(bf.getType("mock"));
		Map<String, Runnable> beans = bf.getBeansOfType(Runnable.class);
		assertEquals(0, beans.size());
	}

	@Test
	public void parameterizedInstanceFactoryMethodWithIndexedArgument() {
		RootBeanDefinition rbd = new RootBeanDefinition(MocksControl.class);
		bf.registerBeanDefinition("mocksControl", rbd);

		rbd = new RootBeanDefinition();
		rbd.setFactoryBeanName("mocksControl");
		rbd.setFactoryMethodName("createMock");
		rbd.getConstructorArgumentValues().addIndexedArgumentValue(0, Runnable.class);
		bf.registerBeanDefinition("mock", rbd);

		assertTrue(bf.isTypeMatch("mock", Runnable.class));
		assertTrue(bf.isTypeMatch("mock", Runnable.class));
		assertEquals(Runnable.class, bf.getType("mock"));
		assertEquals(Runnable.class, bf.getType("mock"));
		Map<String, Runnable> beans = bf.getBeansOfType(Runnable.class);
		assertEquals(1, beans.size());
	}

	@Test  // SPR-16720
	public void parameterizedInstanceFactoryMethodWithTempClassLoader() {
		bf.setTempClassLoader(new OverridingClassLoader(getClass().getClassLoader()));
		RootBeanDefinition rbd = new RootBeanDefinition(MocksControl.class);
		bf.registerBeanDefinition("mocksControl", rbd);

		rbd = new RootBeanDefinition();
		rbd.setFactoryBeanName("mocksControl");
		rbd.setFactoryMethodName("createMock");
		rbd.getConstructorArgumentValues().addGenericArgumentValue(Runnable.class);
		bf.registerBeanDefinition("mock", rbd);

		assertTrue(bf.isTypeMatch("mock", Runnable.class));
		assertTrue(bf.isTypeMatch("mock", Runnable.class));
		assertEquals(Runnable.class, bf.getType("mock"));
		assertEquals(Runnable.class, bf.getType("mock"));
		Map<String, Runnable> beans = bf.getBeansOfType(Runnable.class);
		assertEquals(1, beans.size());
	}

	@Test
	public void testGenericMatchingWithBeanNameDifferentiation() {
		bf.setAutowireCandidateResolver(new GenericTypeAwareAutowireCandidateResolver());
		bf.registerBeanDefinition("doubleStore", new RootBeanDefinition(NumberStore.class));
		bf.registerBeanDefinition("floatStore", new RootBeanDefinition(NumberStore.class));
		bf.registerBeanDefinition("numberBean",new RootBeanDefinition(NumberBean.class, RootBeanDefinition.AUTOWIRE_CONSTRUCTOR, false));

		NumberBean nb = bf.getBean(NumberBean.class);
		assertSame(bf.getBean("doubleStore"), nb.getDoubleStore());
		assertSame(bf.getBean("floatStore"), nb.getFloatStore());

		String[] numberStoreNames = bf.getBeanNamesForType(ResolvableType.forClass(NumberStore.class));
		String[] doubleStoreNames = bf.getBeanNamesForType(ResolvableType.forClassWithGenerics(NumberStore.class, Double.class));
		String[] floatStoreNames = bf.getBeanNamesForType(ResolvableType.forClassWithGenerics(NumberStore.class, Float.class));
		assertEquals(2, numberStoreNames.length);
		assertEquals("doubleStore", numberStoreNames[0]);
		assertEquals("floatStore", numberStoreNames[1]);
		assertEquals(0, doubleStoreNames.length);
		assertEquals(0, floatStoreNames.length);
	}

	@Test
	public void testGenericMatchingWithFullTypeDifferentiation() {
		bf.setDependencyComparator(AnnotationAwareOrderComparator.INSTANCE);
		bf.setAutowireCandidateResolver(new GenericTypeAwareAutowireCandidateResolver());

		RootBeanDefinition bd1 = new RootBeanDefinition(NumberStoreFactory.class);
		bd1.setFactoryMethodName("newDoubleStore");
		bf.registerBeanDefinition("store1", bd1);
		RootBeanDefinition bd2 = new RootBeanDefinition(NumberStoreFactory.class);
		bd2.setFactoryMethodName("newFloatStore");
		bf.registerBeanDefinition("store2", bd2);
		bf.registerBeanDefinition("numberBean",new RootBeanDefinition(NumberBean.class, RootBeanDefinition.AUTOWIRE_CONSTRUCTOR, false));

		NumberBean nb = bf.getBean(NumberBean.class);
		assertSame(bf.getBean("store1"), nb.getDoubleStore());
		assertSame(bf.getBean("store2"), nb.getFloatStore());

		String[] numberStoreNames = bf.getBeanNamesForType(ResolvableType.forClass(NumberStore.class));
		String[] doubleStoreNames = bf.getBeanNamesForType(ResolvableType.forClassWithGenerics(NumberStore.class, Double.class));
		String[] floatStoreNames = bf.getBeanNamesForType(ResolvableType.forClassWithGenerics(NumberStore.class, Float.class));
		assertEquals(2, numberStoreNames.length);
		assertEquals("store1", numberStoreNames[0]);
		assertEquals("store2", numberStoreNames[1]);
		assertEquals(1, doubleStoreNames.length);
		assertEquals("store1", doubleStoreNames[0]);
		assertEquals(1, floatStoreNames.length);
		assertEquals("store2", floatStoreNames[0]);

		ObjectProvider<NumberStore<?>> numberStoreProvider = bf.getBeanProvider(ResolvableType.forClass(NumberStore.class));
		ObjectProvider<NumberStore<Double>> doubleStoreProvider = bf.getBeanProvider(ResolvableType.forClassWithGenerics(NumberStore.class, Double.class));
		ObjectProvider<NumberStore<Float>> floatStoreProvider = bf.getBeanProvider(ResolvableType.forClassWithGenerics(NumberStore.class, Float.class));
		try {
			numberStoreProvider.getObject();
			fail("Should have thrown NoUniqueBeanDefinitionException");
		}catch (NoUniqueBeanDefinitionException ex) {
			// expected
		}
		try {
			numberStoreProvider.getIfAvailable();
			fail("Should have thrown NoUniqueBeanDefinitionException");
		}catch (NoUniqueBeanDefinitionException ex) {
			// expected
		}
		assertNull(numberStoreProvider.getIfUnique());
		assertSame(bf.getBean("store1"), doubleStoreProvider.getObject());
		assertSame(bf.getBean("store1"), doubleStoreProvider.getIfAvailable());
		assertSame(bf.getBean("store1"), doubleStoreProvider.getIfUnique());
		assertSame(bf.getBean("store2"), floatStoreProvider.getObject());
		assertSame(bf.getBean("store2"), floatStoreProvider.getIfAvailable());
		assertSame(bf.getBean("store2"), floatStoreProvider.getIfUnique());

		List<NumberStore<?>> resolved = new ArrayList<>();
		for (NumberStore<?> instance : numberStoreProvider) {
			resolved.add(instance);
		}
		assertEquals(2, resolved.size());
		assertSame(bf.getBean("store1"), resolved.get(0));
		assertSame(bf.getBean("store2"), resolved.get(1));

		resolved = numberStoreProvider.stream().collect(Collectors.toList());
		assertEquals(2, resolved.size());
		assertSame(bf.getBean("store1"), resolved.get(0));
		assertSame(bf.getBean("store2"), resolved.get(1));

		resolved = numberStoreProvider.orderedStream().collect(Collectors.toList());
		assertEquals(2, resolved.size());
		assertSame(bf.getBean("store2"), resolved.get(0));
		assertSame(bf.getBean("store1"), resolved.get(1));

		resolved = new ArrayList<>();
		for (NumberStore<Double> instance : doubleStoreProvider) {
			resolved.add(instance);
		}
		assertEquals(1, resolved.size());
		assertTrue(resolved.contains(bf.getBean("store1")));

		resolved = doubleStoreProvider.stream().collect(Collectors.toList());
		assertEquals(1, resolved.size());
		assertTrue(resolved.contains(bf.getBean("store1")));

		resolved = doubleStoreProvider.orderedStream().collect(Collectors.toList());
		assertEquals(1, resolved.size());
		assertTrue(resolved.contains(bf.getBean("store1")));

		resolved = new ArrayList<>();
		for (NumberStore<Float> instance : floatStoreProvider) {
			resolved.add(instance);
		}
		assertEquals(1, resolved.size());
		assertTrue(resolved.contains(bf.getBean("store2")));

		resolved = floatStoreProvider.stream().collect(Collectors.toList());
		assertEquals(1, resolved.size());
		assertTrue(resolved.contains(bf.getBean("store2")));

		resolved = floatStoreProvider.orderedStream().collect(Collectors.toList());
		assertEquals(1, resolved.size());
		assertTrue(resolved.contains(bf.getBean("store2")));
	}

	@Test
	public void testGenericMatchingWithUnresolvedOrderedStream() {
		bf.setDependencyComparator(AnnotationAwareOrderComparator.INSTANCE);
		bf.setAutowireCandidateResolver(new GenericTypeAwareAutowireCandidateResolver());

		RootBeanDefinition bd1 = new RootBeanDefinition(NumberStoreFactory.class);
		bd1.setFactoryMethodName("newDoubleStore");
		bf.registerBeanDefinition("store1", bd1);
		RootBeanDefinition bd2 = new RootBeanDefinition(NumberStoreFactory.class);
		bd2.setFactoryMethodName("newFloatStore");
		bf.registerBeanDefinition("store2", bd2);

		ObjectProvider<NumberStore<?>> numberStoreProvider = bf.getBeanProvider(ResolvableType.forClass(NumberStore.class));
		List<NumberStore<?>> resolved = numberStoreProvider.orderedStream().collect(Collectors.toList());
		assertEquals(2, resolved.size());
		assertSame(bf.getBean("store2"), resolved.get(0));
		assertSame(bf.getBean("store1"), resolved.get(1));
	}


	@SuppressWarnings("serial")
	public static class NamedUrlList extends LinkedList<URL> {
	}

	@SuppressWarnings("serial")
	public static class NamedUrlSet extends HashSet<URL> {
	}

	@SuppressWarnings("serial")
	public static class NamedUrlMap extends HashMap<Integer, URL> {
	}

	public static class CollectionDependentBean {

		public CollectionDependentBean(NamedUrlList list, NamedUrlSet set, NamedUrlMap map) {
			assertEquals(1, list.size());
			assertEquals(1, set.size());
			assertEquals(1, map.size());
		}
	}

	@SuppressWarnings("serial")
	public static class UrlSet extends HashSet<URL> {

		public UrlSet() {
			super();
		}

		public UrlSet(Set<? extends URL> urls) {
			super();
		}

		public void setUrlNames(Set<URI> urlNames) throws MalformedURLException {
			for (URI urlName : urlNames) {
				add(urlName.toURL());
			}
		}
	}


	/**
	 * Pseudo-implementation of EasyMock's {@code MocksControl} class.
	 */
	public static class MocksControl {

		@SuppressWarnings("unchecked")
		public <T> T createMock(Class<T> toMock) {
			return (T) Proxy.newProxyInstance(BeanFactoryGenericsTests.class.getClassLoader(), new Class<?>[] {toMock},
					new InvocationHandler() {
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							throw new UnsupportedOperationException("mocked!");
						}
					});
		}
	}


	public static class NumberStore<T extends Number> {
	}

	public static class DoubleStore extends NumberStore<Double> {
	}

	public static class FloatStore extends NumberStore<Float> {
	}

	public static class NumberBean {

		private final NumberStore<Double> doubleStore;

		private final NumberStore<Float> floatStore;

		public NumberBean(NumberStore<Double> doubleStore, NumberStore<Float> floatStore) {
			this.doubleStore = doubleStore;
			this.floatStore = floatStore;
		}

		public NumberStore<Double> getDoubleStore() {
			return this.doubleStore;
		}

		public NumberStore<Float> getFloatStore() {
			return this.floatStore;
		}
	}


	public static class NumberStoreFactory {

		@Order(1)
		public static NumberStore<Double> newDoubleStore() {
			return new DoubleStore();
		}

		@Order(0)
		public static NumberStore<Float> newFloatStore() {
			return new FloatStore();
		}
	}
}
