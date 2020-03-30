

package org.springframework.beans.factory.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.springframework.beans.BeanMetadataAttribute;
import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.parsing.BeanEntry;
import org.springframework.beans.factory.parsing.ConstructorArgumentEntry;
import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.beans.factory.parsing.PropertyEntry;
import org.springframework.beans.factory.parsing.QualifierEntry;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.LookupOverride;
import org.springframework.beans.factory.support.ManagedArray;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedProperties;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.beans.factory.support.MethodOverrides;
import org.springframework.beans.factory.support.ReplaceOverride;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;

/**
 * Stateful delegate class used to parse XML bean definitions. Intended for use by both the main parser and any extension
 * 用于解析XML bean定义的有状态委托类。用于主析器和任何扩展
 * {@link BeanDefinitionParser BeanDefinitionParsers} or {@link BeanDefinitionDecorator BeanDefinitionDecorators}.
 * @since 2.0
 * @see ParserContext
 * @see DefaultBeanDefinitionDocumentReader
 * 定义解析 Element 的各种方法
 *
 * 那么 spring 如何来区别bean 元素以及其它扩展元素的，大家可能很自然地就能想到使用元素名啊，的确使用元素名可以处理，
 * 但这就会出现这样的情况，程序员 A 扩展spring 定一个元素名为 c 的元素，
 * 同样程序员 B 扩展spring 也定义了名为 c 的元素，此时就无法区分了 。
 * 其实spring 是通过xml namespace 来区分的，同样查找扩展元素的解析器也是通过xml namespace 来处理的。
 * spring从根元素开始，在解析每个元素的时候，都会先查询元素的namespace uri，
 * 如果元素的namespace uri 为http://www.springframework.org/schema/beans，则由 spring IoC 来解析处理，这些元素包括beans、bean、import、alias，
 * 如果namespace uri 不是http://www.springframework.org/schema/beans，则会使用 NamespaceHandlerResolver 来解析出一个NamespaceHandler，
 * 使用NamespaceHandler 来解析处理这个元素。NamespaceHandlerResovler和NamespaceHandler 就是扩展 spring 的秘密所在。
 * NamespaceHandlerResolver是一个接口，spring使用与EntityResolver 相同的策略来实现，这个后面会提到。当这一步完成了spring 也就完成了读取解析xml 配置。
 *
 * 一开始就声明了各种常量字符串，都是在Spring的配置文件中常见的属性值。
 * BeanDefinitionParserDelegate类提供了解析spring配置文件功能,对于默认空间下的元素()在该类内部实现,
 * 对于其它命名空间下的元素可以通过绑定NamespaceHandler的方式来实现,针对每个命名空间下的元素提供不同BeanDefinitionParser来实现.
 */
public class BeanDefinitionParserDelegate {

	public static final String BEANS_NAMESPACE_URI = "http://www.springframework.org/schema/beans";
	public static final String MULTI_VALUE_ATTRIBUTE_DELIMITERS = ",; ";

	/**
	 * Value of a T/F attribute that represents true. Anything else represents false. Case seNsItive.
	 */
	public static final String TRUE_VALUE = "true";
	public static final String FALSE_VALUE = "false";
	public static final String DEFAULT_VALUE = "default";
	public static final String DESCRIPTION_ELEMENT = "description";
	public static final String AUTOWIRE_NO_VALUE = "no";
	public static final String AUTOWIRE_BY_NAME_VALUE = "byName";
	public static final String AUTOWIRE_BY_TYPE_VALUE = "byType";
	public static final String AUTOWIRE_CONSTRUCTOR_VALUE = "constructor";
	public static final String AUTOWIRE_AUTODETECT_VALUE = "autodetect";
	public static final String NAME_ATTRIBUTE = "name";
	public static final String BEAN_ELEMENT = "bean";
	public static final String META_ELEMENT = "meta";
	public static final String ID_ATTRIBUTE = "id";
	public static final String PARENT_ATTRIBUTE = "parent";
	public static final String CLASS_ATTRIBUTE = "class";
	public static final String ABSTRACT_ATTRIBUTE = "abstract";
	public static final String SCOPE_ATTRIBUTE = "scope";
	private static final String SINGLETON_ATTRIBUTE = "singleton";
	public static final String LAZY_INIT_ATTRIBUTE = "lazy-init";
	public static final String AUTOWIRE_ATTRIBUTE = "autowire";
	public static final String AUTOWIRE_CANDIDATE_ATTRIBUTE = "autowire-candidate";
	public static final String PRIMARY_ATTRIBUTE = "primary";
	public static final String DEPENDS_ON_ATTRIBUTE = "depends-on";
	public static final String INIT_METHOD_ATTRIBUTE = "init-method";
	public static final String DESTROY_METHOD_ATTRIBUTE = "destroy-method";
	public static final String FACTORY_METHOD_ATTRIBUTE = "factory-method";
	public static final String FACTORY_BEAN_ATTRIBUTE = "factory-bean";
	public static final String CONSTRUCTOR_ARG_ELEMENT = "constructor-arg";
	public static final String INDEX_ATTRIBUTE = "index";
	public static final String TYPE_ATTRIBUTE = "type";
	public static final String VALUE_TYPE_ATTRIBUTE = "value-type";
	public static final String KEY_TYPE_ATTRIBUTE = "key-type";
	public static final String PROPERTY_ELEMENT = "property";
	public static final String REF_ATTRIBUTE = "ref";
	public static final String VALUE_ATTRIBUTE = "value";
	public static final String LOOKUP_METHOD_ELEMENT = "lookup-method";
	public static final String REPLACED_METHOD_ELEMENT = "replaced-method";
	public static final String REPLACER_ATTRIBUTE = "replacer";
	public static final String ARG_TYPE_ELEMENT = "arg-type";
	public static final String ARG_TYPE_MATCH_ATTRIBUTE = "match";
	public static final String REF_ELEMENT = "ref";
	public static final String IDREF_ELEMENT = "idref";
	public static final String BEAN_REF_ATTRIBUTE = "bean";
	public static final String PARENT_REF_ATTRIBUTE = "parent";
	public static final String VALUE_ELEMENT = "value";
	public static final String NULL_ELEMENT = "null";
	public static final String ARRAY_ELEMENT = "array";
	public static final String LIST_ELEMENT = "list";
	public static final String SET_ELEMENT = "set";
	public static final String MAP_ELEMENT = "map";
	public static final String ENTRY_ELEMENT = "entry";
	public static final String KEY_ELEMENT = "key";
	public static final String KEY_ATTRIBUTE = "key";
	public static final String KEY_REF_ATTRIBUTE = "key-ref";
	public static final String VALUE_REF_ATTRIBUTE = "value-ref";
	public static final String PROPS_ELEMENT = "props";
	public static final String PROP_ELEMENT = "prop";
	public static final String MERGE_ATTRIBUTE = "merge";
	public static final String QUALIFIER_ELEMENT = "qualifier";
	public static final String QUALIFIER_ATTRIBUTE_ELEMENT = "attribute";
	public static final String DEFAULT_LAZY_INIT_ATTRIBUTE = "default-lazy-init";
	public static final String DEFAULT_MERGE_ATTRIBUTE = "default-merge";
	public static final String DEFAULT_AUTOWIRE_ATTRIBUTE = "default-autowire";
	public static final String DEFAULT_AUTOWIRE_CANDIDATES_ATTRIBUTE = "default-autowire-candidates";
	public static final String DEFAULT_INIT_METHOD_ATTRIBUTE = "default-init-method";
	public static final String DEFAULT_DESTROY_METHOD_ATTRIBUTE = "default-destroy-method";


	protected final Log logger = LogFactory.getLog(getClass());

	private final XmlReaderContext readerContext;

	private final DocumentDefaultsDefinition defaults = new DocumentDefaultsDefinition();

	private final ParseState parseState = new ParseState();

	/**
	 * Stores all used bean names so we can enforce uniqueness on a per
	 * beans-element basis. Duplicate bean ids/names may not exist within the
	 * same level of beans element nesting, but may be duplicated across levels.
	 */
	private final Set<String> usedNames = new HashSet<>();

	/**
	 * Create a new BeanDefinitionParserDelegate associated with the supplied
	 * {@link XmlReaderContext}.
	 */
	public BeanDefinitionParserDelegate(XmlReaderContext readerContext) {
		Assert.notNull(readerContext, "XmlReaderContext must not be null");
		this.readerContext = readerContext;
	}

	/**
	 * Get the {@link XmlReaderContext} associated with this helper instance.
	 */
	public final XmlReaderContext getReaderContext() {
		return this.readerContext;
	}

	/**
	 * Invoke the {@link org.springframework.beans.factory.parsing.SourceExtractor}
	 * to pull the source metadata from the supplied {@link Element}.
	 */
	@Nullable
	protected Object extractSource(Element ele) {
		return this.readerContext.extractSource(ele);
	}

	/**
	 * Report an error with the given message for the given source element.
	 */
	protected void error(String message, Node source) {
		this.readerContext.error(message, source, this.parseState.snapshot());
	}

	/**
	 * Report an error with the given message for the given source element.
	 */
	protected void error(String message, Element source) {
		this.readerContext.error(message, source, this.parseState.snapshot());
	}

	/**
	 * Report an error with the given message for the given source element.
	 */
	protected void error(String message, Element source, Throwable cause) {
		this.readerContext.error(message, source, this.parseState.snapshot(), cause);
	}


	/**
	 * Initialize the default settings assuming a {@code null} parent delegate.
	 */
	public void initDefaults(Element root) {
		initDefaults(root, null);
	}

	/**
	 * Initialize the default lazy-init, autowire, dependency check settings,
	 * init-method, destroy-method and merge settings. Support nested 'beans'
	 * element use cases by falling back to the given parent in case the
	 * defaults are not explicitly set locally.
	 * @see #populateDefaults(DocumentDefaultsDefinition, DocumentDefaultsDefinition, org.w3c.dom.Element)
	 * @see #getDefaults()
	 */
	public void initDefaults(Element root, @Nullable BeanDefinitionParserDelegate parent) {
		populateDefaults(this.defaults, (parent != null ? parent.defaults : null), root);
		this.readerContext.fireDefaultsRegistered(this.defaults);
	}

	/**
	 * Populate the given DocumentDefaultsDefinition instance with the default lazy-init,
	 * autowire, dependency check settings, init-method, destroy-method and merge settings.
	 * Support nested 'beans' element use cases by falling back to {@code parentDefaults} in case the defaults are not explicitly set locally.
	 * @param defaults the defaults to populate
	 * @param parentDefaults the parent BeanDefinitionParserDelegate (if any) defaults to fall back to
	 * @param root the root element of the current bean definition document (or nested beans element)
	 */
	protected void populateDefaults(DocumentDefaultsDefinition defaults, @Nullable DocumentDefaultsDefinition parentDefaults, Element root) {
		String lazyInit = root.getAttribute(DEFAULT_LAZY_INIT_ATTRIBUTE);
		if (isDefaultValue(lazyInit)) {
			// Potentially inherited from outer <beans> sections, otherwise falling back to false.
			lazyInit = (parentDefaults != null ? parentDefaults.getLazyInit() : FALSE_VALUE);
		}
		defaults.setLazyInit(lazyInit);

		String merge = root.getAttribute(DEFAULT_MERGE_ATTRIBUTE);
		if (isDefaultValue(merge)) {
			// Potentially inherited from outer <beans> sections, otherwise falling back to false.
			merge = (parentDefaults != null ? parentDefaults.getMerge() : FALSE_VALUE);
		}
		defaults.setMerge(merge);

		String autowire = root.getAttribute(DEFAULT_AUTOWIRE_ATTRIBUTE);
		if (isDefaultValue(autowire)) {
			// Potentially inherited from outer <beans> sections, otherwise falling back to 'no'.
			autowire = (parentDefaults != null ? parentDefaults.getAutowire() : AUTOWIRE_NO_VALUE);
		}
		defaults.setAutowire(autowire);

		if (root.hasAttribute(DEFAULT_AUTOWIRE_CANDIDATES_ATTRIBUTE)) {
			defaults.setAutowireCandidates(root.getAttribute(DEFAULT_AUTOWIRE_CANDIDATES_ATTRIBUTE));
		}else if (parentDefaults != null) {
			defaults.setAutowireCandidates(parentDefaults.getAutowireCandidates());
		}

		if (root.hasAttribute(DEFAULT_INIT_METHOD_ATTRIBUTE)) {
			defaults.setInitMethod(root.getAttribute(DEFAULT_INIT_METHOD_ATTRIBUTE));
		}else if (parentDefaults != null) {
			defaults.setInitMethod(parentDefaults.getInitMethod());
		}

		if (root.hasAttribute(DEFAULT_DESTROY_METHOD_ATTRIBUTE)) {
			defaults.setDestroyMethod(root.getAttribute(DEFAULT_DESTROY_METHOD_ATTRIBUTE));
		}else if (parentDefaults != null) {
			defaults.setDestroyMethod(parentDefaults.getDestroyMethod());
		}
		defaults.setSource(this.readerContext.extractSource(root));
	}

	/**
	 * Return the defaults definition object.
	 */
	public DocumentDefaultsDefinition getDefaults() {
		return this.defaults;
	}

	/**
	 * Return the default settings for bean definitions as indicated within
	 * the attributes of the top-level {@code <beans/>} element.
	 */
	public BeanDefinitionDefaults getBeanDefinitionDefaults() {
		BeanDefinitionDefaults bdd = new BeanDefinitionDefaults();
		bdd.setLazyInit(TRUE_VALUE.equalsIgnoreCase(this.defaults.getLazyInit()));
		bdd.setAutowireMode(getAutowireMode(DEFAULT_VALUE));
		bdd.setInitMethodName(this.defaults.getInitMethod());
		bdd.setDestroyMethodName(this.defaults.getDestroyMethod());
		return bdd;
	}

	/**
	 * Return any patterns provided in the 'default-autowire-candidates'
	 * attribute of the top-level {@code <beans/>} element.
	 */
	@Nullable
	public String[] getAutowireCandidatePatterns() {
		String candidatePattern = this.defaults.getAutowireCandidates();
		return (candidatePattern != null ? StringUtils.commaDelimitedListToStringArray(candidatePattern) : null);
	}


	/**
	 * Parses the supplied {@code <bean>} element. May return {@code null}
	 * if there were errors during parse. Errors are reported to the
	 * {@link org.springframework.beans.factory.parsing.ProblemReporter}.
	 * 解析bean的属性和子节点
	 */
	@Nullable
	public BeanDefinitionHolder parseBeanDefinitionElement(Element ele) {
		return parseBeanDefinitionElement(ele, null);
	}

	/**
	 *  最终解析xml成BeanDefinition
	 * 填充各种默认的属性
	 * Parses the supplied {@code <bean>} element. May return {@code null}
	 * if there were errors during parse. Errors are reported to the
	 * {@link org.springframework.beans.factory.parsing.ProblemReporter}.
	 */
	@Nullable
	public BeanDefinitionHolder parseBeanDefinitionElement(Element ele, @Nullable BeanDefinition containingBean) {
		// 1、解析bean的id和bean的别名
		//  获取bean标签的id属性值
		String id = ele.getAttribute(ID_ATTRIBUTE);
		// name 属性 "" // 获取bean标签的name（别名）属性值 ，缓存至List集合
		String nameAttr = ele.getAttribute(NAME_ATTRIBUTE);
		// name 属性值就是别名 alias，解析 name 的值，放到 alias 集合中 // 对name属性值按照逗号和分号进行分割，将分割后的所有name属性值作为当前bean的别名
		List<String> aliases = new ArrayList<>();
		if (StringUtils.hasLength(nameAttr)) {
			// 这里可以看到 name 的值可以使用 , ; 和空格进行分割
			String[] nameArr = StringUtils.tokenizeToStringArray(nameAttr, MULTI_VALUE_ATTRIBUTE_DELIMITERS);
			aliases.addAll(Arrays.asList(nameArr));
		}
		// beanName 的值就是 id 的值 // 以id属性的值作为当前bean的默认名称，如果没有id属性，那么将name属性的第一个值作为当前bean的名称
		// 如果bean的id为空，但是别名不为空的话，那么默认采用第一个别名作为beanName
		// 例如：<bean class="com.lyc.cn.v2.day01.Dog" name="myDog1,myDog2"/>，使用myDog1作为beanName
		String beanName = id;
		// 如果 beanName 为空，且 alias 不为空，则获取第一个别名作为beanName
		if (!StringUtils.hasText(beanName) && !aliases.isEmpty()) {
			beanName = aliases.remove(0);
			if (logger.isTraceEnabled()) {
				logger.trace("No XML 'id' specified - using '" + beanName + "' as bean name and " + aliases + " as aliases");
			}
		}
		// 如果父 bean 为空，则检查beanName的唯一性
		// 检查当前bean的名称与已经解析的bean名称是否有相同的，如果有相同的则抛出异常，保证所有不同bean的名称和别名相互之间不相同
		// 2、containingBean不为空，则检查beanName和别名是否被使用
		if (containingBean == null) {
			checkNameUniqueness(beanName, aliases, ele);
		}
		// 创建 AbstractBeanDefinition 在该方法内部，会解析bean所有的属性和子节点
		// 对bean标签中的各个属性以及子标签进行解析
		// 3、解析bean标签，将其转换为BeanDefinition对象
		AbstractBeanDefinition beanDefinition = parseBeanDefinitionElement(ele, beanName, containingBean);
		// 4、如果beanDefinition且未配置bean的id属性，name属性，则为当前bean生成id和别名
		// 例如：<bean class="com.lyc.cn.v2.day01.Dog"/>，<bean factory-bean="dog4"/>，<bean parent="outer"/>等
		if (beanDefinition != null) {
			// 如果没有指定id或name属性，则为当前bean自动生成名称
			if (!StringUtils.hasText(beanName)) {
				try {
					// containingBean不为null，则当前bean是内部bean，使用BeanDefinitionReaderUtils生成beanName
					// 如果 beanName为空，则根据spring中的命名规则为该bean生成对应的beanName  // 如果父bean为空，那么按照默认方式生成bean名称
					if (containingBean != null) {
						beanName = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, this.readerContext.getRegistry(), true);
					}else {
						// 否则，使用XmlReaderContext对象生成beanName
						// 如果父bean不为空，则生成名称时按照一定规则将父bean加入生成的bean名称中
						beanName = this.readerContext.generateBeanName(beanDefinition);
						// Register an alias for the plain bean class name, if still possible,
						// if the generator returned the class name plus a suffix.
						// This is expected for Spring 1.2/2.0 backwards compatibility.
						// 把该bean的类名也注册为别名，为了向后兼容
						// 如果生成器返回的是类名加上后缀，则为普通bean类名注册一个别名。这在Spring 1.2/2.0的向后兼容性中是可以实现的。
						String beanClassName = beanDefinition.getBeanClassName();
						if (beanClassName != null && beanName.startsWith(beanClassName) && beanName.length() > beanClassName.length() &&
								!this.readerContext.getRegistry().isBeanNameInUse(beanClassName)) {
							aliases.add(beanClassName);
						}
					}
					if (logger.isTraceEnabled()) logger.trace("Neither XML 'id' nor 'name' specified - " + "using generated bean name [" + beanName + "]");
				} catch (Exception ex) {
					error(ex.getMessage(), ele);
					return null;
				}
			}
			// 5、创建并返回BeanDefinitionHolder对象
			// BeanDefinitionHolder 创建对象返回，分别设置对应的属性值  // 将生成的BeanDefinition，beanName和alias封装到BeanDefinitionHolder中
			String[] aliasesArray = StringUtils.toStringArray(aliases);
			return new BeanDefinitionHolder(beanDefinition, beanName, aliasesArray);
		}
		return null;
	}

	/**
	 * Validate that the specified bean name and aliases have not been used already
	 * within the current level of beans element nesting.
	 */
	protected void checkNameUniqueness(String beanName, List<String> aliases, Element beanElement) {
		String foundName = null;
		if (StringUtils.hasText(beanName) && this.usedNames.contains(beanName)) {
			foundName = beanName;
		}
		if (foundName == null) {
			foundName = CollectionUtils.findFirstMatch(this.usedNames, aliases);
		}
		if (foundName != null) {
			error("Bean name '" + foundName + "' is already used in this <beans> element", beanElement);
		}
		this.usedNames.add(beanName);
		this.usedNames.addAll(aliases);
	}

	/**
	 * Parse the bean definition itself, without regard to name or aliases. May return
	 * {@code null} if problems occurred during the parsing of the bean definition.
	 * // 解析属性和子节点
	 *
	 */
	@Nullable
	public AbstractBeanDefinition parseBeanDefinitionElement(Element ele, String beanName, @Nullable BeanDefinition containingBean) {
		// 将当前bean的名称push到parseState属性中，以标识当前beanName的bean正在被解析
		this.parseState.push(new BeanEntry(beanName));
		// 1、解析class属性 // 获取 class 属性的值
		String className = null;
		if (ele.hasAttribute(CLASS_ATTRIBUTE)) {
			className = ele.getAttribute(CLASS_ATTRIBUTE).trim();// com.goat.chapter200.CompactDiscImpl
		}
		// 2、解析parent属性 // 获取 parent 属性的值
		String parent = null;
		if (ele.hasAttribute(PARENT_ATTRIBUTE)) {
			parent = ele.getAttribute(PARENT_ATTRIBUTE);
		}

		try {
			// 3、创建 AbstractBeanDefinition 对象，底层是 GenericBeanDefinition // 创建一个BeanDefinition的对象
			AbstractBeanDefinition bd = createBeanDefinition(className, parent);
			// 解析所有属性 // 转换基本的bean属性的值
			// 4、解析bean标签属性
			parseBeanDefinitionAttributes(ele, beanName, containingBean, bd);
			bd.setDescription(DomUtils.getChildElementValueByTagName(ele, DESCRIPTION_ELEMENT));
			// 5、解析meta标签  <meta> // 转换并封装元节点标签
			parseMetaElements(ele, bd);
			// 6、解析 <lookup-method /> // 转换lookup-method子标签，将其封装到MethodOverrides对象中
			parseLookupOverrideSubElements(ele, bd.getMethodOverrides());
			// 7、解析 <replaced-method/> // 转换replaced-method子标签，也将其封装到MethodOverrides对象中
			parseReplacedMethodSubElements(ele, bd.getMethodOverrides());
			// 8、解析 <constructor-arg /> // 解析并且封装constructor-arg子标签到BeanDefinition中
			parseConstructorArgElements(ele, bd);
			// 9、解析 <property />  // 解析并且封装property子标签到BeanDefinition中
			parsePropertyElements(ele, bd);
			// 10、解析 <qualifier /> // 解析并且封装qualifier子标签到BeanDefinition中
			parseQualifierElements(ele, bd);
			// 11、设置bean定义来源和元数据的来源  封装一些资源属性
			bd.setResource(this.readerContext.getResource());
			bd.setSource(extractSource(ele));

			return bd;
		}catch (ClassNotFoundException ex) {
			error("Bean class [" + className + "] not found", ele, ex);
		}catch (NoClassDefFoundError err) {
			error("Class that bean class [" + className + "] depends on not found", ele, err);
		}catch (Throwable ex) {
			error("Unexpected failure during bean definition parsing", ele, ex);
		}finally {
			this.parseState.pop();
		}
		return null;
	}

	/**
	 * Apply the attributes of the given bean element to the given bean * definition.
	 * @param ele bean declaration element
	 * @param beanName bean name
	 * @param containingBean containing bean definition
	 * @return a bean definition initialized according to the bean element attributes
	 *  解析所有属性
	 */
	public AbstractBeanDefinition parseBeanDefinitionAttributes(Element ele, String beanName,@Nullable BeanDefinition containingBean, AbstractBeanDefinition bd) {
		// singleton 和 scope 只能有一个 // 检查当前bean是否有singleton属性，有则提示应该使用scope属性，并抛出异常
		// 1、设置bean作用域
		if (ele.hasAttribute(SINGLETON_ATTRIBUTE)) {
			error("Old 1.x 'singleton' attribute in use - upgrade to 'scope' declaration", ele);
		}else if (ele.hasAttribute(SCOPE_ATTRIBUTE)) {
			// 获取并设置scope属性值
			bd.setScope(ele.getAttribute(SCOPE_ATTRIBUTE));
		}else if (containingBean != null) {
			// Take default from containing bean in case of an inner bean definition. // 没有scope属性值，则将父bean的scope属性设置给当前bean
			// 未明确指定bean的作用域,且当前被解析bean是内部bean的话，则默认使用outer bean的的作用域作为当前bean的作用域
			// 例如:下面的配置，解析到inner属性时，inner未指定作用域，则使用outer的作用域，也就是prototype
			/**
			 <bean id="outer" class="com.lyc.cn.v2.day01.inner.Outer" scope="prototype">
				 <property name="inner">
					 <bean id="inner" class="com.lyc.cn.v2.day01.inner.Inner"/>
				 </property>
			 </bean>
			 **/
			bd.setScope(containingBean.getScope());
		}
		// 2、设置abstract属性 // abstract 属性  // 获取并设置abstract属性值
		if (ele.hasAttribute(ABSTRACT_ATTRIBUTE)) {
			bd.setAbstract(TRUE_VALUE.equals(ele.getAttribute(ABSTRACT_ATTRIBUTE)));
		}
		// 3、设置lazy-init(延迟加载)属性；
		// 如果该属性为true的话，ApplicationContext容器在初始化时不会加载该bean； 而是在第一次向容器索取该bean时才会被初始化
		// 获取并设置lazy-init属性值，如果当前bean的lazy-init是默认值， 则将beans标签中设置的lazy-init属性值设置给当前bean
		String lazyInit = ele.getAttribute(LAZY_INIT_ATTRIBUTE);
		if (isDefaultValue(lazyInit)) {
			lazyInit = this.defaults.getLazyInit();
		}
		bd.setLazyInit(TRUE_VALUE.equals(lazyInit));
		// 4、设置autowire属性，此属性默认不开启// 获取并设置autowire属性值
		String autowire = ele.getAttribute(AUTOWIRE_ATTRIBUTE);
		bd.setAutowireMode(getAutowireMode(autowire));
		// 5、设置depends-on属性，如果BeanA依赖于BeanB，可通过depends-on属性使BeanB在BeanA之前完初始化
		// 获取depends-on属性值，并且按照逗号或分号进行分割，然后设置depends-on属性值
		if (ele.hasAttribute(DEPENDS_ON_ATTRIBUTE)) {
			String dependsOn = ele.getAttribute(DEPENDS_ON_ATTRIBUTE);
			bd.setDependsOn(StringUtils.tokenizeToStringArray(dependsOn, MULTI_VALUE_ATTRIBUTE_DELIMITERS));
		}
		// 获取并设置autowire-candicate属性值，如果其为空，那么获取当前xml
		// 最外层beans标签设置的autowire-candicate属性值，并将其设置给当前bean
		// 6、设置autowire-candidate属性，当一个接口有多个实现类时， 配置autowire-candidate属性可以明确指定实现类是否参与自动注入
		String autowireCandidate = ele.getAttribute(AUTOWIRE_CANDIDATE_ATTRIBUTE);
		if (isDefaultValue(autowireCandidate)) {
			String candidatePattern = this.defaults.getAutowireCandidates();
			if (candidatePattern != null) {
				String[] patterns = StringUtils.commaDelimitedListToStringArray(candidatePattern);
				bd.setAutowireCandidate(PatternMatchUtils.simpleMatch(patterns, beanName));
			}
		}else {
			bd.setAutowireCandidate(TRUE_VALUE.equals(autowireCandidate));
		}
		// 7、设置primary属性，当byType注入有多个类型时，可以指定primary="true"，提高注入的优先级，避免抛出异常
		//  获取并设置primary属性值
		if (ele.hasAttribute(PRIMARY_ATTRIBUTE)) {
			bd.setPrimary(TRUE_VALUE.equals(ele.getAttribute(PRIMARY_ATTRIBUTE)));
		}
		// 获取并设置init-method属性值，如果该属性为空，
		// 则获取当前xml最外层beans标签设置的init-method属性值，并且设置给当前bean
		// 8、设置init-method，bean初始化完成后回调该方法
		if (ele.hasAttribute(INIT_METHOD_ATTRIBUTE)) {
			String initMethodName = ele.getAttribute(INIT_METHOD_ATTRIBUTE);
			bd.setInitMethodName(initMethodName);
		}else if (this.defaults.getInitMethod() != null) {
			// 尝试从DocumentDefaultsDefinition对象中获取init-method属性
			// DocumentDefaultsDefinition对象保存了Spring bean的一些简单设置，
			// 我们可以通过该类设定通用的bean属性模板
			bd.setInitMethodName(this.defaults.getInitMethod());
			bd.setEnforceInitMethod(false);
		}
		// 获取并设置destroy-method属性的值，如果该属性值为空，
		// 则获取当前xml最外层beans标签设置的destroy-method属性值，并设置给当前bean
		// 9、设置destroy-method属性，bean销毁后回调该方法
		if (ele.hasAttribute(DESTROY_METHOD_ATTRIBUTE)) {
			String destroyMethodName = ele.getAttribute(DESTROY_METHOD_ATTRIBUTE);
			bd.setDestroyMethodName(destroyMethodName);
		}else if (this.defaults.getDestroyMethod() != null) {
			// 尝试从DocumentDefaultsDefinition对象中获取destroy-method属性
			bd.setDestroyMethodName(this.defaults.getDestroyMethod());
			bd.setEnforceDestroyMethod(false);
		}
		// 10、设置factory-method属性，该属性可指定静态工厂或实例工厂方法实例化bean // 获取并设置fatory-method属性的值
		if (ele.hasAttribute(FACTORY_METHOD_ATTRIBUTE)) {
			bd.setFactoryMethodName(ele.getAttribute(FACTORY_METHOD_ATTRIBUTE));
		}
		// 获取并设置factory-bean属性的值
		// 11、设置factory-bean属性，一般和factory-method属性一起使用，指定工厂bean和工厂bean的工厂方法
		if (ele.hasAttribute(FACTORY_BEAN_ATTRIBUTE)) {
			bd.setFactoryBeanName(ele.getAttribute(FACTORY_BEAN_ATTRIBUTE));
		}
		return bd;
	}

	/**
	 * Create a bean definition for the given class name and parent name.
	 * @param className the name of the bean class
	 * @param parentName the name of the bean's parent bean
	 * @return the newly created bean definition
	 * @throws ClassNotFoundException if bean class resolution was attempted but failed
	 */
	protected AbstractBeanDefinition createBeanDefinition(@Nullable String className, @Nullable String parentName) throws ClassNotFoundException {
		return BeanDefinitionReaderUtils.createBeanDefinition(parentName, className, this.readerContext.getBeanClassLoader());
	}

	// 解析 <meta>
	public void parseMetaElements(Element ele, BeanMetadataAttributeAccessor attributeAccessor) {
		NodeList nl = ele.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			// 判断是 meta 节点
			if (isCandidateElement(node) && nodeNameEquals(node, META_ELEMENT)) {
				Element metaElement = (Element) node;
				String key = metaElement.getAttribute(KEY_ATTRIBUTE);
				String value = metaElement.getAttribute(VALUE_ATTRIBUTE);
				BeanMetadataAttribute attribute = new BeanMetadataAttribute(key, value);
				attribute.setSource(extractSource(metaElement));
				attributeAccessor.addMetadataAttribute(attribute);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public int getAutowireMode(String attValue) {
		String att = attValue;
		if (isDefaultValue(att)) {
			att = this.defaults.getAutowire();
		}
		int autowire = AbstractBeanDefinition.AUTOWIRE_NO;
		if (AUTOWIRE_BY_NAME_VALUE.equals(att)) {
			autowire = AbstractBeanDefinition.AUTOWIRE_BY_NAME;
		}else if (AUTOWIRE_BY_TYPE_VALUE.equals(att)) {
			autowire = AbstractBeanDefinition.AUTOWIRE_BY_TYPE;
		}else if (AUTOWIRE_CONSTRUCTOR_VALUE.equals(att)) {
			autowire = AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR;
		}else if (AUTOWIRE_AUTODETECT_VALUE.equals(att)) {
			autowire = AbstractBeanDefinition.AUTOWIRE_AUTODETECT;
		}
		// Else leave default value.
		return autowire;
	}

	/**
	 * Parse constructor-arg sub-elements of the given bean element.
	 */
	public void parseConstructorArgElements(Element beanEle, BeanDefinition bd) {
		NodeList nl = beanEle.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (isCandidateElement(node) && nodeNameEquals(node, CONSTRUCTOR_ARG_ELEMENT)) {
				parseConstructorArgElement((Element) node, bd);
			}
		}
	}

	/**
	 * Parse property sub-elements of the given bean element.
	 */
	public void parsePropertyElements(Element beanEle, BeanDefinition bd) {
		NodeList nl = beanEle.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (isCandidateElement(node) && nodeNameEquals(node, PROPERTY_ELEMENT)) {
				parsePropertyElement((Element) node, bd);
			}
		}
	}

	/**
	 * Parse qualifier sub-elements of the given bean element.
	 */
	public void parseQualifierElements(Element beanEle, AbstractBeanDefinition bd) {
		NodeList nl = beanEle.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (isCandidateElement(node) && nodeNameEquals(node, QUALIFIER_ELEMENT)) {
				parseQualifierElement((Element) node, bd);
			}
		}
	}

	/**
	 * Parse lookup-override sub-elements of the given bean element.
	 */
	public void parseLookupOverrideSubElements(Element beanEle, MethodOverrides overrides) {
		NodeList nl = beanEle.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (isCandidateElement(node) && nodeNameEquals(node, LOOKUP_METHOD_ELEMENT)) {
				Element ele = (Element) node;
				String methodName = ele.getAttribute(NAME_ATTRIBUTE);
				String beanRef = ele.getAttribute(BEAN_ELEMENT);
				LookupOverride override = new LookupOverride(methodName, beanRef);
				override.setSource(extractSource(ele));
				overrides.addOverride(override);
			}
		}
	}

	/**
	 * Parse replaced-method sub-elements of the given bean element.
	 */
	public void parseReplacedMethodSubElements(Element beanEle, MethodOverrides overrides) {
		NodeList nl = beanEle.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			// 查找replaced-method子标签
			if (isCandidateElement(node) && nodeNameEquals(node, REPLACED_METHOD_ELEMENT)) {
				Element replacedMethodEle = (Element) node;
				// 获取name属性和replacer属性的值
				String name = replacedMethodEle.getAttribute(NAME_ATTRIBUTE);
				String callback = replacedMethodEle.getAttribute(REPLACER_ATTRIBUTE);
				// 将name属性和replacer属性的值封装到ReplaceOverride对象中
				ReplaceOverride replaceOverride = new ReplaceOverride(name, callback);
				// Look for arg-type match elements. // 获取并且封装arg-type子标签的属性
				List<Element> argTypeEles = DomUtils.getChildElementsByTagName(replacedMethodEle, ARG_TYPE_ELEMENT);
				for (Element argTypeEle : argTypeEles) {
					String match = argTypeEle.getAttribute(ARG_TYPE_MATCH_ATTRIBUTE);
					match = (StringUtils.hasText(match) ? match : DomUtils.getTextValue(argTypeEle));
					if (StringUtils.hasText(match)) {
						replaceOverride.addTypeIdentifier(match);
					}
				}
				replaceOverride.setSource(extractSource(replacedMethodEle));
				overrides.addOverride(replaceOverride);
			}
		}
	}

	/**
	 * Parse a constructor-arg element.
	 */
	public void parseConstructorArgElement(Element ele, BeanDefinition bd) {
		// 获取index，type和name属性值
		String indexAttr = ele.getAttribute(INDEX_ATTRIBUTE);
		String typeAttr = ele.getAttribute(TYPE_ATTRIBUTE);
		String nameAttr = ele.getAttribute(NAME_ATTRIBUTE);
		// 当有index属性值时的处理方式
		if (StringUtils.hasLength(indexAttr)) {
			try {
				int index = Integer.parseInt(indexAttr);
				if (index < 0) {
					error("'index' cannot be lower than 0", ele);
				}else {
					try {
						this.parseState.push(new ConstructorArgumentEntry(index));
						// 对属性值进行解析
						Object value = parsePropertyValue(ele, bd, null);
						ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(value);
						// 封装type属性值
						if (StringUtils.hasLength(typeAttr)) {
							valueHolder.setType(typeAttr);
						}
						// 封装name属性值
						if (StringUtils.hasLength(nameAttr)) {
							valueHolder.setName(nameAttr);
						}
						valueHolder.setSource(extractSource(ele));
						if (bd.getConstructorArgumentValues().hasIndexedArgumentValue(index)) {
							error("Ambiguous constructor-arg entries for index " + index, ele);
						}else {
							bd.getConstructorArgumentValues().addIndexedArgumentValue(index, valueHolder);
						}
					}finally {
						this.parseState.pop();
					}
				}
			}catch (NumberFormatException ex) {
				error("Attribute 'index' of tag 'constructor-arg' must be an integer", ele);
			}
		}
		// 当没有index属性值时的处理方式
		else {
			try {
				this.parseState.push(new ConstructorArgumentEntry());
				// 对属性值进行解析
				Object value = parsePropertyValue(ele, bd, null);
				// 对解析的属性值进行封装
				ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(value);
				// 设置type属性值
				if (StringUtils.hasLength(typeAttr)) {
					valueHolder.setType(typeAttr);
				}
				// 设置name属性值
				if (StringUtils.hasLength(nameAttr)) {
					valueHolder.setName(nameAttr);
				}
				valueHolder.setSource(extractSource(ele));
				bd.getConstructorArgumentValues().addGenericArgumentValue(valueHolder);
			}finally {
				this.parseState.pop();
			}
		}
	}

	/**
	 * Parse a property element.
	 */
	public void parsePropertyElement(Element ele, BeanDefinition bd) {
		// 获取name属性的值
		String propertyName = ele.getAttribute(NAME_ATTRIBUTE);
		if (!StringUtils.hasLength(propertyName)) {
			error("Tag 'property' must have a 'name' attribute", ele);
			return;
		}
		this.parseState.push(new PropertyEntry(propertyName));
		try {
			if (bd.getPropertyValues().contains(propertyName)) {
				error("Multiple 'property' definitions for property '" + propertyName + "'", ele);
				return;
			}
			// 解析property标签及其子标签的值
			Object val = parsePropertyValue(ele, bd, propertyName);
			PropertyValue pv = new PropertyValue(propertyName, val);
			parseMetaElements(ele, pv);
			pv.setSource(extractSource(ele));
			bd.getPropertyValues().addPropertyValue(pv);
		}finally {
			this.parseState.pop();
		}
	}

	/**
	 * Parse a qualifier element.
	 */
	public void parseQualifierElement(Element ele, AbstractBeanDefinition bd) {
		String typeName = ele.getAttribute(TYPE_ATTRIBUTE);
		if (!StringUtils.hasLength(typeName)) {
			error("Tag 'qualifier' must have a 'type' attribute", ele);
			return;
		}
		this.parseState.push(new QualifierEntry(typeName));
		try {
			AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(typeName);
			qualifier.setSource(extractSource(ele));
			String value = ele.getAttribute(VALUE_ATTRIBUTE);
			if (StringUtils.hasLength(value)) {
				qualifier.setAttribute(AutowireCandidateQualifier.VALUE_KEY, value);
			}
			NodeList nl = ele.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if (isCandidateElement(node) && nodeNameEquals(node, QUALIFIER_ATTRIBUTE_ELEMENT)) {
					Element attributeEle = (Element) node;
					String attributeName = attributeEle.getAttribute(KEY_ATTRIBUTE);
					String attributeValue = attributeEle.getAttribute(VALUE_ATTRIBUTE);
					if (StringUtils.hasLength(attributeName) && StringUtils.hasLength(attributeValue)) {
						BeanMetadataAttribute attribute = new BeanMetadataAttribute(attributeName, attributeValue);
						attribute.setSource(extractSource(attributeEle));
						qualifier.addMetadataAttribute(attribute);
					}else {
						error("Qualifier 'attribute' tag must have a 'name' and 'value'", attributeEle);
						return;
					}
				}
			}
			bd.addQualifier(qualifier);
		}finally {
			this.parseState.pop();
		}
	}

	/**
	 * Get the value of a property element. May be a list etc.
	 * Also used for constructor arguments, "propertyName" being null in this case.
	 */
	@Nullable
	public Object parsePropertyValue(Element ele, BeanDefinition bd, @Nullable String propertyName) {
		String elementName = (propertyName != null ? "<property> element for property '" + propertyName + "'" : "<constructor-arg> element");
		// Should only have one child element: ref, value, list, etc.  // 判断当前标签是否至少有一个非description和meta的子标签，没有则抛出异常
		NodeList nl = ele.getChildNodes();
		Element subElement = null;
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element && !nodeNameEquals(node, DESCRIPTION_ELEMENT) && !nodeNameEquals(node, META_ELEMENT)) {
				// Child element is what we're looking for.
				if (subElement != null) {
					error(elementName + " must not contain more than one sub-element", ele);
				}else {
					subElement = (Element) node;
				}
			}
		}
		// 获取ref和value属性的值
		boolean hasRefAttribute = ele.hasAttribute(REF_ATTRIBUTE);
		boolean hasValueAttribute = ele.hasAttribute(VALUE_ATTRIBUTE);
		// 判断当前标签不能同时设置ref和value属性的值，同时设置了则报错
		if ((hasRefAttribute && hasValueAttribute) || ((hasRefAttribute || hasValueAttribute) && subElement != null)) {
			error(elementName + " is only allowed to contain either 'ref' attribute OR 'value' attribute OR sub-element", ele);
		}
		// 如果有ref属性值，则对ref属性值进行解析
		if (hasRefAttribute) {
			String refName = ele.getAttribute(REF_ATTRIBUTE);
			if (!StringUtils.hasText(refName)) {
				error(elementName + " contains empty 'ref' attribute", ele);
			}
			// 如果是ref属性值，则将其值封装为RuntimeBeanReference类型的对象
			RuntimeBeanReference ref = new RuntimeBeanReference(refName);
			ref.setSource(extractSource(ele));
			return ref;
		}
		// 如果有value属性值，则对value属性值进行处理
		else if (hasValueAttribute) {
			// 将value属性值封装到TypedStringValue对象中
			TypedStringValue valueHolder = new TypedStringValue(ele.getAttribute(VALUE_ATTRIBUTE));
			valueHolder.setSource(extractSource(ele));
			return valueHolder;
		}
		else if (subElement != null) {
			// 如果当前标签还有子标签，那么对该子标签进行解析
			return parsePropertySubElement(subElement, bd);
		}
		else {
			// Neither child element nor "ref" or "value" attribute found.// 如果不满足前面任何一个条件，则抛出异常
			error(elementName + " must specify a ref or value", ele);
			return null;
		}
	}

	@Nullable
	public Object parsePropertySubElement(Element ele, @Nullable BeanDefinition bd) {
		return parsePropertySubElement(ele, bd, null);
	}

	/**
	 * Parse a value, ref or collection sub-element of a property or constructor-arg element.
	 * @param ele subelement of property element; we don't know which yet
	 * @param defaultValueType the default type (class name) for any
	 * {@code <value>} tag that might be created
	 */
	@Nullable
	public Object parsePropertySubElement(Element ele, @Nullable BeanDefinition bd, @Nullable String defaultValueType) {
		// 如果该子标签使用的自定义的标签名，则使用自定义子标签进行解析
		if (!isDefaultNamespace(ele)) {
			return parseNestedCustomElement(ele, bd);
		}else if (nodeNameEquals(ele, BEAN_ELEMENT)) {
			// 如果当前子标签是bean子标签，则递归的对当前bean进行解析
			BeanDefinitionHolder nestedBd = parseBeanDefinitionElement(ele, bd);
			if (nestedBd != null) {
				nestedBd = decorateBeanDefinitionIfRequired(ele, nestedBd, bd);
			}
			return nestedBd;
		}else if (nodeNameEquals(ele, REF_ELEMENT)) {
			// A generic reference to any name of any bean. // 如果当前子标签是ref子标签，则获取其bean属性的值，并且将其封装到RuntimeBeanReference对象中
			String refName = ele.getAttribute(BEAN_REF_ATTRIBUTE);
			boolean toParent = false;
			if (!StringUtils.hasLength(refName)) {
				// A reference to the id of another bean in a parent context.
				refName = ele.getAttribute(PARENT_REF_ATTRIBUTE);
				toParent = true;
				if (!StringUtils.hasLength(refName)) {
					error("'bean' or 'parent' is required for <ref> element", ele);
					return null;
				}
			}
			if (!StringUtils.hasText(refName)) {
				error("<ref> element contains empty target attribute", ele);
				return null;
			}
			RuntimeBeanReference ref = new RuntimeBeanReference(refName, toParent);
			ref.setSource(extractSource(ele));
			return ref;
		}else if (nodeNameEquals(ele, IDREF_ELEMENT)) {
			// 如果当前子标签是idref子标签，则还是将其封装到一个RuntimeBeanReference对象中
			return parseIdRefElement(ele);
		}else if (nodeNameEquals(ele, VALUE_ELEMENT)) {
			// 如果当前子标签是value子标签，则将其封装到一个TypedStringValue对象中，并且会检查其type属性指定的class是否存在
			return parseValueElement(ele, defaultValueType);
		}else if (nodeNameEquals(ele, NULL_ELEMENT)) {
			// 如果子标签是一个null子标签，则封装一个包含空属性的TypedStringValue对象
			// It's a distinguished null value. Let's wrap it in a TypedStringValue
			// object in order to preserve the source location.
			TypedStringValue nullHolder = new TypedStringValue(null);
			nullHolder.setSource(extractSource(ele));
			return nullHolder;
		}else if (nodeNameEquals(ele, ARRAY_ELEMENT)) {
			// 如果是array子标签，则对array子标签进行解析
			return parseArrayElement(ele, bd);
		}else if (nodeNameEquals(ele, LIST_ELEMENT)) {
			// 如果是list子标签，则对list子标签进行解析
			return parseListElement(ele, bd);
		}else if (nodeNameEquals(ele, SET_ELEMENT)) {
			// 如果是set子标签，则对set子标签进行解析
			return parseSetElement(ele, bd);
		}else if (nodeNameEquals(ele, MAP_ELEMENT)) {
			// 如果是map子标签，则对map子标签进行解析
			return parseMapElement(ele, bd);
		}else if (nodeNameEquals(ele, PROPS_ELEMENT)) {
			// 如果是props子标签，则对props子标签进行解析
			return parsePropsElement(ele);
		}else {
			error("Unknown property sub-element: [" + ele.getNodeName() + "]", ele);
			return null;
		}
	}

	/**
	 * Return a typed String value Object for the given 'idref' element.
	 */
	@Nullable
	public Object parseIdRefElement(Element ele) {
		// A generic reference to any name of any bean.
		String refName = ele.getAttribute(BEAN_REF_ATTRIBUTE);
		if (!StringUtils.hasLength(refName)) {
			error("'bean' is required for <idref> element", ele);
			return null;
		}
		if (!StringUtils.hasText(refName)) {
			error("<idref> element contains empty target attribute", ele);
			return null;
		}
		RuntimeBeanNameReference ref = new RuntimeBeanNameReference(refName);
		ref.setSource(extractSource(ele));
		return ref;
	}

	/**
	 * Return a typed String value Object for the given value element.
	 */
	public Object parseValueElement(Element ele, @Nullable String defaultTypeName) {
		// It's a literal value.
		String value = DomUtils.getTextValue(ele);
		String specifiedTypeName = ele.getAttribute(TYPE_ATTRIBUTE);
		String typeName = specifiedTypeName;
		if (!StringUtils.hasText(typeName)) {
			typeName = defaultTypeName;
		}
		try {
			TypedStringValue typedValue = buildTypedStringValue(value, typeName);
			typedValue.setSource(extractSource(ele));
			typedValue.setSpecifiedTypeName(specifiedTypeName);
			return typedValue;
		}catch (ClassNotFoundException ex) {
			error("Type class [" + typeName + "] not found for <value> element", ele, ex);
			return value;
		}
	}

	/**
	 * Build a typed String value Object for the given raw value.
	 * @see org.springframework.beans.factory.config.TypedStringValue
	 */
	protected TypedStringValue buildTypedStringValue(String value, @Nullable String targetTypeName) throws ClassNotFoundException {
		ClassLoader classLoader = this.readerContext.getBeanClassLoader();
		TypedStringValue typedValue;
		if (!StringUtils.hasText(targetTypeName)) {
			typedValue = new TypedStringValue(value);
		}else if (classLoader != null) {
			Class<?> targetType = ClassUtils.forName(targetTypeName, classLoader);
			typedValue = new TypedStringValue(value, targetType);
		}else {
			typedValue = new TypedStringValue(value, targetTypeName);
		}
		return typedValue;
	}

	/**
	 * Parse an array element.
	 */
	public Object parseArrayElement(Element arrayEle, @Nullable BeanDefinition bd) {
		String elementType = arrayEle.getAttribute(VALUE_TYPE_ATTRIBUTE);
		NodeList nl = arrayEle.getChildNodes();
		ManagedArray target = new ManagedArray(elementType, nl.getLength());
		target.setSource(extractSource(arrayEle));
		target.setElementTypeName(elementType);
		target.setMergeEnabled(parseMergeAttribute(arrayEle));
		parseCollectionElements(nl, target, bd, elementType);
		return target;
	}

	/**
	 * Parse a list element.
	 */
	public List<Object> parseListElement(Element collectionEle, @Nullable BeanDefinition bd) {
		String defaultElementType = collectionEle.getAttribute(VALUE_TYPE_ATTRIBUTE);
		NodeList nl = collectionEle.getChildNodes();
		ManagedList<Object> target = new ManagedList<>(nl.getLength());
		target.setSource(extractSource(collectionEle));
		target.setElementTypeName(defaultElementType);
		target.setMergeEnabled(parseMergeAttribute(collectionEle));
		parseCollectionElements(nl, target, bd, defaultElementType);
		return target;
	}

	/**
	 * Parse a set element.
	 */
	public Set<Object> parseSetElement(Element collectionEle, @Nullable BeanDefinition bd) {
		String defaultElementType = collectionEle.getAttribute(VALUE_TYPE_ATTRIBUTE);
		NodeList nl = collectionEle.getChildNodes();
		ManagedSet<Object> target = new ManagedSet<>(nl.getLength());
		target.setSource(extractSource(collectionEle));
		target.setElementTypeName(defaultElementType);
		target.setMergeEnabled(parseMergeAttribute(collectionEle));
		parseCollectionElements(nl, target, bd, defaultElementType);
		return target;
	}

	protected void parseCollectionElements(NodeList elementNodes, Collection<Object> target, @Nullable BeanDefinition bd, String defaultElementType) {
		for (int i = 0; i < elementNodes.getLength(); i++) {
			Node node = elementNodes.item(i);
			if (node instanceof Element && !nodeNameEquals(node, DESCRIPTION_ELEMENT)) {
				target.add(parsePropertySubElement((Element) node, bd, defaultElementType));
			}
		}
	}

	/**
	 * Parse a map element.
	 */
	public Map<Object, Object> parseMapElement(Element mapEle, @Nullable BeanDefinition bd) {
		String defaultKeyType = mapEle.getAttribute(KEY_TYPE_ATTRIBUTE);
		String defaultValueType = mapEle.getAttribute(VALUE_TYPE_ATTRIBUTE);

		List<Element> entryEles = DomUtils.getChildElementsByTagName(mapEle, ENTRY_ELEMENT);
		ManagedMap<Object, Object> map = new ManagedMap<>(entryEles.size());
		map.setSource(extractSource(mapEle));
		map.setKeyTypeName(defaultKeyType);
		map.setValueTypeName(defaultValueType);
		map.setMergeEnabled(parseMergeAttribute(mapEle));

		for (Element entryEle : entryEles) {
			// Should only have one value child element: ref, value, list, etc.
			// Optionally, there might be a key child element.
			NodeList entrySubNodes = entryEle.getChildNodes();
			Element keyEle = null;
			Element valueEle = null;
			for (int j = 0; j < entrySubNodes.getLength(); j++) {
				Node node = entrySubNodes.item(j);
				if (node instanceof Element) {
					Element candidateEle = (Element) node;
					if (nodeNameEquals(candidateEle, KEY_ELEMENT)) {
						if (keyEle != null) {
							error("<entry> element is only allowed to contain one <key> sub-element", entryEle);
						}else {
							keyEle = candidateEle;
						}
					}else {
						// Child element is what we're looking for.
						if (nodeNameEquals(candidateEle, DESCRIPTION_ELEMENT)) {
							// the element is a <description> -> ignore it
						}else if (valueEle != null) {
							error("<entry> element must not contain more than one value sub-element", entryEle);
						}else {
							valueEle = candidateEle;
						}
					}
				}
			}

			// Extract key from attribute or sub-element.
			Object key = null;
			boolean hasKeyAttribute = entryEle.hasAttribute(KEY_ATTRIBUTE);
			boolean hasKeyRefAttribute = entryEle.hasAttribute(KEY_REF_ATTRIBUTE);
			if ((hasKeyAttribute && hasKeyRefAttribute) || (hasKeyAttribute || hasKeyRefAttribute) && keyEle != null) {
				error("<entry> element is only allowed to contain either a 'key' attribute OR a 'key-ref' attribute OR a <key> sub-element", entryEle);
			}
			if (hasKeyAttribute) {
				key = buildTypedStringValueForMap(entryEle.getAttribute(KEY_ATTRIBUTE), defaultKeyType, entryEle);
			}else if (hasKeyRefAttribute) {
				String refName = entryEle.getAttribute(KEY_REF_ATTRIBUTE);
				if (!StringUtils.hasText(refName)) {
					error("<entry> element contains empty 'key-ref' attribute", entryEle);
				}
				RuntimeBeanReference ref = new RuntimeBeanReference(refName);
				ref.setSource(extractSource(entryEle));
				key = ref;
			}else if (keyEle != null) {
				key = parseKeyElement(keyEle, bd, defaultKeyType);
			}else {
				error("<entry> element must specify a key", entryEle);
			}

			// Extract value from attribute or sub-element.
			Object value = null;
			boolean hasValueAttribute = entryEle.hasAttribute(VALUE_ATTRIBUTE);
			boolean hasValueRefAttribute = entryEle.hasAttribute(VALUE_REF_ATTRIBUTE);
			boolean hasValueTypeAttribute = entryEle.hasAttribute(VALUE_TYPE_ATTRIBUTE);
			if ((hasValueAttribute && hasValueRefAttribute) || (hasValueAttribute || hasValueRefAttribute) && valueEle != null) {
				error("<entry> element is only allowed to contain either 'value' attribute OR 'value-ref' attribute OR <value> sub-element", entryEle);

			}
			if ((hasValueTypeAttribute && hasValueRefAttribute) || (hasValueTypeAttribute && !hasValueAttribute) || (hasValueTypeAttribute && valueEle != null)) {
				error("<entry> element is only allowed to contain a 'value-type' attribute when it has a 'value' attribute", entryEle);
			}
			if (hasValueAttribute) {
				String valueType = entryEle.getAttribute(VALUE_TYPE_ATTRIBUTE);
				if (!StringUtils.hasText(valueType)) {
					valueType = defaultValueType;
				}
				value = buildTypedStringValueForMap(entryEle.getAttribute(VALUE_ATTRIBUTE), valueType, entryEle);
			}else if (hasValueRefAttribute) {
				String refName = entryEle.getAttribute(VALUE_REF_ATTRIBUTE);
				if (!StringUtils.hasText(refName)) {
					error("<entry> element contains empty 'value-ref' attribute", entryEle);
				}
				RuntimeBeanReference ref = new RuntimeBeanReference(refName);
				ref.setSource(extractSource(entryEle));
				value = ref;
			}else if (valueEle != null) {
				value = parsePropertySubElement(valueEle, bd, defaultValueType);
			}else {
				error("<entry> element must specify a value", entryEle);
			}
			// Add final key and value to the Map.
			map.put(key, value);
		}
		return map;
	}

	/**
	 * Build a typed String value Object for the given raw value.
	 * @see org.springframework.beans.factory.config.TypedStringValue
	 */
	protected final Object buildTypedStringValueForMap(String value, String defaultTypeName, Element entryEle) {
		try {
			TypedStringValue typedValue = buildTypedStringValue(value, defaultTypeName);
			typedValue.setSource(extractSource(entryEle));
			return typedValue;
		}catch (ClassNotFoundException ex) {
			error("Type class [" + defaultTypeName + "] not found for Map key/value type", entryEle, ex);
			return value;
		}
	}

	/**
	 * Parse a key sub-element of a map element.
	 */
	@Nullable
	protected Object parseKeyElement(Element keyEle, @Nullable BeanDefinition bd, String defaultKeyTypeName) {
		NodeList nl = keyEle.getChildNodes();
		Element subElement = null;
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element) {
				// Child element is what we're looking for.
				if (subElement != null) {
					error("<key> element must not contain more than one value sub-element", keyEle);
				}else {
					subElement = (Element) node;
				}
			}
		}
		if (subElement == null) return null;
		return parsePropertySubElement(subElement, bd, defaultKeyTypeName);
	}

	/**
	 * Parse a props element.
	 */
	public Properties parsePropsElement(Element propsEle) {
		ManagedProperties props = new ManagedProperties();
		props.setSource(extractSource(propsEle));
		props.setMergeEnabled(parseMergeAttribute(propsEle));

		List<Element> propEles = DomUtils.getChildElementsByTagName(propsEle, PROP_ELEMENT);
		for (Element propEle : propEles) {
			String key = propEle.getAttribute(KEY_ATTRIBUTE);
			// Trim the text value to avoid unwanted whitespace
			// caused by typical XML formatting.
			String value = DomUtils.getTextValue(propEle).trim();
			TypedStringValue keyHolder = new TypedStringValue(key);
			keyHolder.setSource(extractSource(propEle));
			TypedStringValue valueHolder = new TypedStringValue(value);
			valueHolder.setSource(extractSource(propEle));
			props.put(keyHolder, valueHolder);
		}
		return props;
	}

	/**
	 * Parse the merge attribute of a collection element, if any.
	 */
	public boolean parseMergeAttribute(Element collectionElement) {
		String value = collectionElement.getAttribute(MERGE_ATTRIBUTE);
		if (isDefaultValue(value)) {
			value = this.defaults.getMerge();
		}
		return TRUE_VALUE.equals(value);
	}

	@Nullable
	public BeanDefinition parseCustomElement(Element ele) {
		return parseCustomElement(ele, null);
	}

	@Nullable
	public BeanDefinition parseCustomElement(Element ele, @Nullable BeanDefinition containingBd) {
		// 获取当前标签对应的命名空间指定的url
		String namespaceUri = getNamespaceURI(ele);
		if (namespaceUri == null) return null;
		// 获取当前url所对应的NameSpaceHandler处理逻辑，也即我们定义的MyNameSpaceHandler
		NamespaceHandler handler = this.readerContext.getNamespaceHandlerResolver().resolve(namespaceUri);
		if (handler == null) {
			error("Unable to locate Spring NamespaceHandler for XML schema namespace [" + namespaceUri + "]", ele);
			return null;
		}
		// 调用当前命名空间处理逻辑的parse()方法，以对当前标签进行转换
		return handler.parse(ele, new ParserContext(this.readerContext, this, containingBd));
	}

	public BeanDefinitionHolder decorateBeanDefinitionIfRequired(Element ele, BeanDefinitionHolder definitionHolder) {
		return decorateBeanDefinitionIfRequired(ele, definitionHolder, null);
	}

	public BeanDefinitionHolder decorateBeanDefinitionIfRequired(Element ele, BeanDefinitionHolder definitionHolder, @Nullable BeanDefinition containingBd) {
		BeanDefinitionHolder finalDefinition = definitionHolder;
		// Decorate based on custom attributes first.  // 处理自定义属性
		NamedNodeMap attributes = ele.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node node = attributes.item(i);
			finalDefinition = decorateIfRequired(node, finalDefinition, containingBd);
		}

		// Decorate based on custom nested elements. // 处理自定义子标签
		NodeList children = ele.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				finalDefinition = decorateIfRequired(node, finalDefinition, containingBd);
			}
		}
		return finalDefinition;
	}

	public BeanDefinitionHolder decorateIfRequired(Node node, BeanDefinitionHolder originalDef, @Nullable BeanDefinition containingBd) {
		// 获取当前自定义属性或子标签的命名空间url
		String namespaceUri = getNamespaceURI(node);
		// 判断其如果为spring默认的命名空间则不对其进行处理
		if (namespaceUri != null && !isDefaultNamespace(namespaceUri)) {
			// 获取当前命名空间对应的NamespaceHandler对象
			NamespaceHandler handler = this.readerContext.getNamespaceHandlerResolver().resolve(namespaceUri);
			if (handler != null) {
				// 对当前的BeanDefinitionHolder进行装饰
				BeanDefinitionHolder decorated = handler.decorate(node, originalDef, new ParserContext(this.readerContext, this, containingBd));
				if (decorated != null) {
					return decorated;
				}
			}else if (namespaceUri.startsWith("http://www.springframework.org/")) {
				error("Unable to locate Spring NamespaceHandler for XML schema namespace [" + namespaceUri + "]", node);
			}else {
				// A custom namespace, not to be handled by Spring - maybe "xml:...".
				if (logger.isDebugEnabled()) logger.debug("No Spring NamespaceHandler found for XML schema namespace [" + namespaceUri + "]");
			}
		}
		return originalDef;
	}

	@Nullable
	private BeanDefinitionHolder parseNestedCustomElement(Element ele, @Nullable BeanDefinition containingBd) {
		BeanDefinition innerDefinition = parseCustomElement(ele, containingBd);
		if (innerDefinition == null) {
			error("Incorrect usage of element '" + ele.getNodeName() + "' in a nested manner. This tag cannot be used nested inside <property>.", ele);
			return null;
		}
		String id = ele.getNodeName() + BeanDefinitionReaderUtils.GENERATED_BEAN_NAME_SEPARATOR + ObjectUtils.getIdentityHexString(innerDefinition);
		if (logger.isTraceEnabled()) logger.trace("Using generated bean name [" + id + "] for nested custom element '" + ele.getNodeName() + "'");
		return new BeanDefinitionHolder(innerDefinition, id);
	}


	/**
	 * Get the namespace URI for the supplied node.
	 * The default implementation uses {@link Node#getNamespaceURI}.
	 * Subclasses may override the default implementation to provide a different namespace identification mechanism.
	 * @param node the node
	 * @return eg：http://www.springframework.org/schema/beans
	 */
	@Nullable
	public String getNamespaceURI(Node node) {
		return node.getNamespaceURI();
	}

	/**
	 * Get the local name for the supplied {@link Node}.
	 * The default implementation calls {@link Node#getLocalName}.
	 * Subclasses may override the default implementation to provide a different mechanism for getting the local name.
	 * @param node the {@code Node}
	 */
	public String getLocalName(Node node) {
		return node.getLocalName();
	}

	/**
	 * Determine whether the name of the supplied node is equal to the supplied name.
	 * The default implementation checks the supplied desired name against both
	 * {@link Node#getNodeName()} and {@link Node#getLocalName()}.
	 * Subclasses may override the default implementation to provide a different
	 * mechanism for comparing node names.
	 * @param node the node to compare
	 * @param desiredName the name to check for
	 */
	public boolean nodeNameEquals(Node node, String desiredName) {
		return desiredName.equals(node.getNodeName()) || desiredName.equals(getLocalName(node));
	}

	public boolean isDefaultNamespace(@Nullable String namespaceUri) {
		return (!StringUtils.hasLength(namespaceUri) || BEANS_NAMESPACE_URI.equals(namespaceUri));
	}

	public boolean isDefaultNamespace(Node node) {
		return isDefaultNamespace(getNamespaceURI(node));
	}

	private boolean isDefaultValue(String value) {
		return (DEFAULT_VALUE.equals(value) || "".equals(value));
	}

	private boolean isCandidateElement(Node node) {
		return (node instanceof Element && (isDefaultNamespace(node) || !isDefaultNamespace(node.getParentNode())));
	}

}
