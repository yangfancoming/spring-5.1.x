

package org.springframework.beans.factory.xml;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/**
 * Default implementation of the {@link BeanDefinitionDocumentReader} interface that
 * reads bean definitions according to the "spring-beans" DTD and XSD format (Spring's default XML bean definition format).
 *
 * The structure, elements, and attribute names of the required XML document are hard-coded in this class.
 * (Of course a transform could be run if necessary to produce this format).
 * {@code <beans>} does not need to be the root element of the XML document:
 * this class will parse all bean definition elements in the XML file, regardless of the actual root element.
 * @since 18.12.2003
 *
 * DefaultBeanDefinitionDocumentReader 主要完成两件事情，解析<bean> 元素，为扩展spring 的元素寻找合适的解析器，并把相应的元素交给解析器解析。
 * 第一个任务，解析<bean> 元素，这个spring 的核心功能及IoC 或者是 DI，这由spring 自己来处理，
 * 这个工作有一个专门的委托类来处理BeanDefinitionParserDelegate，由它来解析 <bean> 元素，并把解析的结果注册到BeanDefinitionRegistry（XmlBeanFactory 实现了此接口） 中。
 *
 * 在spring 的源代码目录中有两个很特殊的文件：spring.schemas 和 spring.handlers，
 * 这两个文件以及 spring 中对 EntityResolver 和 NamespaceHandlerResolver 的实现 PluggableSchemaResolver 和DefaultNamespaceHandlerResolver 是扩展 spring 的关键所在。
 * 其实 spring.schemas 和 spring.handlers 文件是标准的 java properties 文件。这两个文件都被打包到 spring jar 包中的 META-INF 目录中，
 * PluggableSchemaResolver 通过读取 spring.schemas 文件，根据 xml 文件中实体的 system id 来解析这些实体，
 * 大家可以看一下 spring.schemas 文件中的 key 就可以知道 system id 是什么了（其实我也不知道 system id 和 public id 是啥，知道的朋友不妨在文后的回复中给我留言，谢谢）；
 * 而 DefaultNamespaceHandlerResolver则是根据元素的 namespace uri 在 spring.handlers 文件中查找具体的 NamespaceHandler 的实现。
 */
public class DefaultBeanDefinitionDocumentReader implements BeanDefinitionDocumentReader {

	public static final String BEAN_ELEMENT = BeanDefinitionParserDelegate.BEAN_ELEMENT;
	public static final String NESTED_BEANS_ELEMENT = "beans";
	public static final String ALIAS_ELEMENT = "alias";
	public static final String NAME_ATTRIBUTE = "name";
	public static final String ALIAS_ATTRIBUTE = "alias";
	public static final String IMPORT_ELEMENT = "import";
	public static final String RESOURCE_ATTRIBUTE = "resource";
	public static final String PROFILE_ATTRIBUTE = "profile";

	protected final Log logger = LogFactory.getLog(getClass());

	@Nullable
	private XmlReaderContext readerContext;

	@Nullable
	private BeanDefinitionParserDelegate delegate;


	/**
	 * This implementation parses bean definitions according to the "spring-beans" XSD (or DTD, historically).
	 * Opens a DOM Document; then initializes the default settings  specified at the {@code <beans/>} level; then parses the contained bean definitions.
	 * 在完成了 Resource 到Document 的转换后，下面就是从Document 中解析出各个bean 的配置了，
	 * 为此spring 又抽象了一个接口BeanDefinitionDocumentReader，
	 * 从它的名称中可以一目了然这个接口负责从Document 中读取bean 定义，
	 * 这个接口中只定义了一个方法registerBeanDefinitions
	 * spring 也提供了一个默认实现DefaultBeanDefinitionDocumentReader
	 */
	@Override
	public void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) {
		this.readerContext = readerContext;
		doRegisterBeanDefinitions(doc.getDocumentElement());
	}

	/**
	 * Return the descriptor for the XML resource that this parser works on.
	 */
	protected final XmlReaderContext getReaderContext() {
		Assert.state(this.readerContext != null, "No XmlReaderContext available");
		return this.readerContext;
	}

	/**
	 * Invoke the {@link org.springframework.beans.factory.parsing.SourceExtractor}
	 * to pull the source metadata from the supplied {@link Element}.
	 */
	@Nullable
	protected Object extractSource(Element ele) {
		return getReaderContext().extractSource(ele);
	}


	/**
	 * Register each bean definition within the given root {@code <beans/>} element.
	 * 	 我们看名字就知道，BeanDefinitionParserDelegate 必定是一个重要的类，它负责解析 Bean 定义，
	 * 	 这里为什么要定义一个 parent? 看到后面就知道了，是递归问题，
	 * 	 因为 <beans /> 内部是可以定义 <beans /> 的，所以这个方法的 root 其实不一定就是 xml 的根节点，也可以是嵌套在里面的 <beans /> 节点，从源码分析的角度，我们当做根节点就好了
	 */
	@SuppressWarnings("deprecation")  // for Environment.acceptsProfiles(String...)
	protected void doRegisterBeanDefinitions(Element root) {
		// Any nested <beans> elements will cause recursion in this method. In
		// order to propagate and preserve <beans> default-* attributes correctly,
		// keep track of the current (parent) delegate, which may be null. Create
		// the new (child) delegate with a reference to the parent for fallback purposes,
		// then ultimately reset this.delegate back to its original (parent) reference.
		// this behavior emulates a stack of delegates without actually necessitating one.
		//标签beans可能会存在递归的情况, 每次都创建自己的解析器
		// 1、创建BeanDefinitionParserDelegate对象，用来解析Element元素
		BeanDefinitionParserDelegate parent = this.delegate;
		this.delegate = createDelegate(getReaderContext(), root, parent);
		// 2、解析并验证profile节点，如果配置了profile属性，则验证当前环境是否激活了对应的profile节点，
		// 用于多开发环境配置，该方式在开发中已不多见。
		// 例如：System.setProperty("spring.profiles.active", "dev");
		if (this.delegate.isDefaultNamespace(root)) {
			//获取beans标签的profile属性
			// 这块说的是根节点 <beans ... profile="dev" /> 中的 profile 是否是当前环境需要的，
			// 如果当前环境配置的 profile 不包含此 profile，那就直接 return 了，不对此 <beans /> 解析
			// 不熟悉 profile 为何物，不熟悉怎么配置 profile 读者的请移步附录区
			String profileSpec = root.getAttribute(PROFILE_ATTRIBUTE);
			if (StringUtils.hasText(profileSpec)) {
				String[] specifiedProfiles = StringUtils.tokenizeToStringArray(profileSpec, BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS);
				// We cannot use Profiles.of(...) since profile expressions are not supported in XML config. See SPR-12458 for details.
				//看spring.profiles.active环境变量中是否有该属性，如果没有则不加载下面的标签
				if (!getReaderContext().getEnvironment().acceptsProfiles(specifiedProfiles)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Skipped XML bean definition file due to specified profiles [" + profileSpec + "] not matching: " + getReaderContext().getResource());
					}
					return;
				}
			}
		}
		// 钩子 // 解析前处理、留给子类实现   // 3、解析前置处理，空的模板方法
		preProcessXml(root);
		//解析bean definition // 4、解析并注册BeanDefinition
		parseBeanDefinitions(root, this.delegate);
		// 钩子 // 解析后处理、留给子类实现  // 5、解析后置处理，空的模板方法
		postProcessXml(root);
		this.delegate = parent;
	}

	protected BeanDefinitionParserDelegate createDelegate(XmlReaderContext readerContext, Element root, @Nullable BeanDefinitionParserDelegate parentDelegate) {
		BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(readerContext);
		delegate.initDefaults(root, parentDelegate);
		return delegate;
	}

	/**
	 * Parse the elements at the root level in the document: "import", "alias", "bean".
	 * @param root the DOM root element of the document
	 *   在 Spring 的配置文件中，有两大类bean的声明，
	 *                一个是默认的声明如 <bean>，
	 *                一类是自定义的声明如 <tx:annotation-driver>，所以该方法分为两套解析逻辑
	 *  default namespace 涉及到的就四个标签 <import />、<alias />、<bean /> 和 <beans />， 其他的属于 custom 的
	 */
	protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
		// 1、解析默认命名空间
		// 表示的是默认的节点
		// 判断根节点使用的标签所对应的命名空间是否为Spring提供的默认命名空间，
		// 这里根节点为beans节点，该节点的命名空间通过其xmlns属性进行了定义
		if (delegate.isDefaultNamespace(root)) {
			NodeList nl = root.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if (node instanceof Element) {
					Element ele = (Element) node;
					// 默认命名空间
					if (delegate.isDefaultNamespace(ele)) {
						// 当前标签使用的是默认的命名空间，如bean标签，则按照默认命名空间的逻辑对其进行处理
						// 代表解析的节点是 default namespace 下面的几个元素  <import />、<alias />、<bean />、<beans />
						parseDefaultElement(ele, delegate); // 解析默认的节点
					}else {
						// 解析其他 namespace 的元素  <mvc />、<task />、<context />、<aop />
						// 自定义命名空间
						// 判断当前标签使用的命名空间是自定义的命名空间，如这里 springtag:user 所使用的就是自定义的命名空间，那么就按照定义命名空间逻辑进行处理
						delegate.parseCustomElement(ele);// 解析自定义节点
					}
				}
			}
		}else {
			// 2、解析自定义命名空间
			// 如果根节点使用的命名空间不是默认的命名空间，则按照自定义的命名空间进行处理
			delegate.parseCustomElement(root); // 解析自定义节点
		}
	}

	/**
	 * 在对xml节点的读取的时候，其分为了四种情形：
	 * ①读取import节点所指定的xml文件信息；
	 * ②读取alias节点的信息；
	 * ③读取bean节点指定的信息；
	 * ④读取嵌套bean的信息
	*/
	private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) {
		if (delegate.nodeNameEquals(ele, IMPORT_ELEMENT)) { // 解析 <import>
			//读取import节点所指定的xml文件信息
			importBeanDefinitionResource(ele);
		}else if (delegate.nodeNameEquals(ele, ALIAS_ELEMENT)) {  // 解析 <alias>
			// 读取alias节点指定的信息
			processAliasRegistration(ele);
		}else if (delegate.nodeNameEquals(ele, BEAN_ELEMENT)) {  // 解析 <bean> 重点
			// 读取bean节点指定的信息
			processBeanDefinition(ele, delegate);
		}else if (delegate.nodeNameEquals(ele, NESTED_BEANS_ELEMENT)) {  // 解析 <beans> 需要递归
			// recurse 读取嵌套bean的信息
			doRegisterBeanDefinitions(ele);
		}
	}

	/**
	 * Parse an "import" element and load the bean definitions from the given resource into the bean factory.
	 */
	protected void importBeanDefinitionResource(Element ele) {
		String location = ele.getAttribute(RESOURCE_ATTRIBUTE);
		if (!StringUtils.hasText(location)) {
			getReaderContext().error("Resource location must not be empty", ele);
			return;
		}
		// Resolve system properties: e.g. "${user.dir}"  // 处理import节点指定的路径中的属性占位符，将其替换为属性文件中指定属性值
		location = getReaderContext().getEnvironment().resolveRequiredPlaceholders(location);
		Set<Resource> actualResources = new LinkedHashSet<>(4);
		// Discover whether the location is an absolute or relative URI  // 处理路径信息，判断其为相对路径还是绝对路径
		boolean absoluteLocation = false;
		try {
			absoluteLocation = ResourcePatternUtils.isUrl(location) || ResourceUtils.toURI(location).isAbsolute();
		}catch (URISyntaxException ex) {
			// cannot convert to an URI, considering the location relative
			// unless it is the well-known Spring prefix "classpath*:"
		}
		// Absolute or relative? // 如果是绝对路径，则直接读取该文件
		if (absoluteLocation) {
			try {
				// 递归调用loadBeanDefinitions()方法加载import所指定的文件中的bean信息
				int importCount = getReaderContext().getReader().loadBeanDefinitions(location, actualResources);
				if (logger.isTraceEnabled()) logger.trace("Imported " + importCount + " bean definitions from URL location [" + location + "]");
			}catch (BeanDefinitionStoreException ex) {
				getReaderContext().error("Failed to import bean definitions from URL location [" + location + "]", ele, ex);
			}
		}else {
			// No URL -> considering resource location as relative to the current file.
			try {
				int importCount;
				// 判断是否为相对路径
				Resource relativeResource = getReaderContext().getResource().createRelative(location);
				// 如果是相对路径，则调用loadBeanDefinitions()方法加载该文件中的bean信息
				if (relativeResource.exists()) {
					importCount = getReaderContext().getReader().loadBeanDefinitions(relativeResource);
					actualResources.add(relativeResource);
				}else {
					// 如果相对路径，也不是绝对路径，则将该路径当做一个外部url进行请求读取
					String baseLocation = getReaderContext().getResource().getURL().toString();
					// 继续调用loadBeanDefinitions()方法读取下载得到的xml文件信息
					importCount = getReaderContext().getReader().loadBeanDefinitions(StringUtils.applyRelativePath(baseLocation, location), actualResources);
				}
				if (logger.isTraceEnabled()) {
					logger.trace("Imported " + importCount + " bean definitions from relative location [" + location + "]");
				}
			}catch (IOException ex) {
				getReaderContext().error("Failed to resolve current resource location", ele, ex);
			}
			catch (BeanDefinitionStoreException ex) {
				getReaderContext().error("Failed to import bean definitions from relative location [" + location + "]", ele, ex);
			}
		}
		Resource[] actResArray = actualResources.toArray(new Resource[0]);
		// 调用注册的对import文件读取完成事件的监听器
		getReaderContext().fireImportProcessed(location, actResArray, extractSource(ele));
	}

	/**
	 * Process the given alias element, registering the alias with the registry.
	 */
	protected void processAliasRegistration(Element ele) {
		// 获取name属性的值
		String name = ele.getAttribute(NAME_ATTRIBUTE);
		// 获取alias属性的值
		String alias = ele.getAttribute(ALIAS_ATTRIBUTE);
		boolean valid = true;
		if (!StringUtils.hasText(name)) {
			getReaderContext().error("Name must not be empty", ele);
			valid = false;
		}
		if (!StringUtils.hasText(alias)) {
			getReaderContext().error("Alias must not be empty", ele);
			valid = false;
		}
		if (valid) {
			try {
				// 注册别名信息
				getReaderContext().getRegistry().registerAlias(name, alias);
			}catch (Exception ex) {
				getReaderContext().error("Failed to register alias '" + alias +"' for bean with name '" + name + "'", ele, ex);
			}
			// 激活对alias注册完成进行监听的监听器
			getReaderContext().fireAliasRegistered(name, alias, extractSource(ele));
		}
	}

	/**
	 * Process the given bean element, parsing the bean definition and registering it with the registry.
	 *  解析bean标签将其转换为definition并注册到BeanDefinitionRegistry
	 */
	protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
		// 解析 bean的各种属性 // 对基本的bean标签属性进行解析
		// 对bean标签的默认属性和子标签进行处理，将其封装为一个BeanDefinition对象，并放入BeanDefinitionHolder中
		// 1、将解析的节点信息封装至BeanDefinitionHolder对象  BeanDefinitionHolder-->封装了BeanDefinition,beanName以及aliases
		BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
		if (bdHolder != null) {
			// 2、装饰BeanDefinition
			// 进行自定义属性或自定义子标签的装饰
			// 如果该bean包含自定义的子标签，则对自定义子标签解析 // 对自定义的属性或者自定义的子节点进行解析，以丰富当前的BeanDefinition
			bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
			try {
				// Register the final decorated instance. // 3、执行注册  // 将当前bean注册到BeanDefinitionRegistry中
				BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
			}catch (BeanDefinitionStoreException ex) {
				getReaderContext().error("Failed to register bean definition with name '" + bdHolder.getBeanName() + "'", ele, ex);
			}
			// Send registration event.    // 发消息，可以忽略
			// 4、发送注册事件
			getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
		}
	}


	/**
	 * Allow the XML to be extensible by processing any custom element types first,
	 * before we start to process the bean definitions. This method is a natural
	 * extension point for any other custom pre-processing of the XML.
	 * The default implementation is empty. Subclasses can override this method to
	 * convert custom elements into standard Spring bean definitions, for example.
	 * Implementors have access to the parser's bean definition reader and the
	 * underlying XML resource, through the corresponding accessors.
	 * @see #getReaderContext()
	 */
	protected void preProcessXml(Element root) {
	}

	/**
	 * Allow the XML to be extensible by processing any custom element types last,
	 * after we finished processing the bean definitions. This method is a natural
	 * extension point for any other custom post-processing of the XML.
	 * The default implementation is empty. Subclasses can override this method to
	 * convert custom elements into standard Spring bean definitions, for example.
	 * Implementors have access to the parser's bean definition reader and the
	 * underlying XML resource, through the corresponding accessors.
	 * @see #getReaderContext()
	 */
	protected void postProcessXml(Element root) {
	}

}
