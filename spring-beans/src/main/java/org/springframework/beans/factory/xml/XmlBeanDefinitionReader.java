

package org.springframework.beans.factory.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.parsing.EmptyReaderEventListener;
import org.springframework.beans.factory.parsing.FailFastProblemReporter;
import org.springframework.beans.factory.parsing.NullSourceExtractor;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.parsing.ReaderEventListener;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.Constants;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.xml.SimpleSaxErrorHandler;
import org.springframework.util.xml.XmlValidationModeDetector;

/**
 * Bean definition reader for XML bean definitions.
 * Delegates the actual XML document reading to an implementation of the {@link BeanDefinitionDocumentReader} interface.
 * Typically applied to a {@link org.springframework.beans.factory.support.DefaultListableBeanFactory} or a {@link org.springframework.context.support.GenericApplicationContext}.
 * This class loads a DOM document and applies the BeanDefinitionDocumentReader to it.
 * The document reader will register each bean definition with the given bean factory,talking to the latter's implementation of the
 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistry} interface.
 * @since 26.11.2003
 * @see #setDocumentReaderClass
 * @see BeanDefinitionDocumentReader
 * @see DefaultBeanDefinitionDocumentReader
 * @see BeanDefinitionRegistry
 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory
 * @see org.springframework.context.support.GenericApplicationContext
 *
 * （1）通过继承自AbstractBeanDefinitionReader中的方法，来使用ResourceLoader将资源文件路径转换为对应的Resource文件
 * （2）通过DocumentLoader对Resource文件进行转换，将Resource文件转换为Document文件
 * （3）通过实现接口BeanDefinitionDocumentReader的DefaultBeanDefinitionDocumentReader类对Document进行解析，并使用BeanDefinitionParserDelegate对Element进行解析。
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

	// Indicates that the validation should be disabled.
	public static final int VALIDATION_NONE = XmlValidationModeDetector.VALIDATION_NONE;

	// Indicates that the validation mode should be detected automatically.
	public static final int VALIDATION_AUTO = XmlValidationModeDetector.VALIDATION_AUTO;

	// Indicates that DTD validation should be used.
	public static final int VALIDATION_DTD = XmlValidationModeDetector.VALIDATION_DTD;

	// Indicates that XSD validation should be used.
	public static final int VALIDATION_XSD = XmlValidationModeDetector.VALIDATION_XSD;

	/** Constants instance for this class. */
	private static final Constants constants = new Constants(XmlBeanDefinitionReader.class);

	private int validationMode = VALIDATION_AUTO;

	private boolean namespaceAware = false;

	private Class<? extends BeanDefinitionDocumentReader> documentReaderClass = DefaultBeanDefinitionDocumentReader.class;

	private ProblemReporter problemReporter = new FailFastProblemReporter();

	private ReaderEventListener eventListener = new EmptyReaderEventListener();

	private SourceExtractor sourceExtractor = new NullSourceExtractor();

	@Nullable
	private NamespaceHandlerResolver namespaceHandlerResolver;

	private DocumentLoader documentLoader = new DefaultDocumentLoader();

	@Nullable
	private EntityResolver entityResolver;

	private ErrorHandler errorHandler = new SimpleSaxErrorHandler(logger);

	private final XmlValidationModeDetector validationModeDetector = new XmlValidationModeDetector();

	private final ThreadLocal<Set<EncodedResource>> resourcesCurrentlyBeingLoaded = new NamedThreadLocal<>("XML bean definition resources currently being loaded");

	/**
	 * Create new XmlBeanDefinitionReader for the given bean factory.
	 * @param registry the BeanFactory to load bean definitions into,in the form of a BeanDefinitionRegistry
	 */
	public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
		super(registry);
	}

	/**
	 * Set whether to use XML validation. Default is {@code true}.
	 * This method switches namespace awareness on if validation is turned off,in order to still process schema namespaces properly in such a scenario.
	 * @see #setValidationMode
	 * @see #setNamespaceAware
	 */
	public void setValidating(boolean validating) {
		validationMode = (validating ? VALIDATION_AUTO : VALIDATION_NONE);
		namespaceAware = !validating;
	}

	/**
	 * Set the validation mode to use by name. Defaults to {@link #VALIDATION_AUTO}.
	 * @see #setValidationMode
	 */
	public void setValidationModeName(String validationModeName) {
		setValidationMode(constants.asNumber(validationModeName).intValue());
	}

	/**
	 * Set the validation mode to use. Defaults to {@link #VALIDATION_AUTO}.
	 * Note that this only activates or deactivates validation itself.
	 * If you are switching validation off for schema files, you might need to  activate schema namespace support explicitly: see {@link #setNamespaceAware}.
	 */
	public void setValidationMode(int validationMode) {
		this.validationMode = validationMode;
	}

	// Return the validation mode to use.
	public int getValidationMode() {
		return validationMode;
	}

	/**
	 * Set whether or not the XML parser should be XML namespace aware. Default is "false".
	 * This is typically not needed when schema validation is active.
	 * However, without validation, this has to be switched to "true" in order to properly process schema namespaces.
	 */
	public void setNamespaceAware(boolean namespaceAware) {
		this.namespaceAware = namespaceAware;
	}

	// Return whether or not the XML parser should be XML namespace aware.
	public boolean isNamespaceAware() {
		return namespaceAware;
	}

	/**
	 * Specify which {@link org.springframework.beans.factory.parsing.ProblemReporter} to use.
	 * The default implementation is {@link org.springframework.beans.factory.parsing.FailFastProblemReporter} which exhibits fail fast behaviour.
	 * External tools can provide an alternative implementation  that collates errors and warnings for display in the tool UI.
	 */
	public void setProblemReporter(@Nullable ProblemReporter problemReporter) {
		this.problemReporter = (problemReporter != null ? problemReporter : new FailFastProblemReporter());
	}

	/**
	 * Specify which {@link ReaderEventListener} to use.
	 * The default implementation is EmptyReaderEventListener which discards every event notification.
	 * External tools can provide an alternative implementation to monitor the components being
	 * registered in the BeanFactory.
	 */
	public void setEventListener(@Nullable ReaderEventListener eventListener) {
		this.eventListener = (eventListener != null ? eventListener : new EmptyReaderEventListener());
	}

	/**
	 * Specify the {@link SourceExtractor} to use.
	 * The default implementation is {@link NullSourceExtractor} which simply returns {@code null} as the source object.
	 * This means that - during normal runtime execution - no additional source metadata is attached to the bean configuration metadata.
	 */
	public void setSourceExtractor(@Nullable SourceExtractor sourceExtractor) {
		this.sourceExtractor = (sourceExtractor != null ? sourceExtractor : new NullSourceExtractor());
	}

	/**
	 * Specify the {@link NamespaceHandlerResolver} to use.
	 * If none is specified, a default instance will be created through {@link #createDefaultNamespaceHandlerResolver()}.
	 */
	public void setNamespaceHandlerResolver(@Nullable NamespaceHandlerResolver namespaceHandlerResolver) {
		this.namespaceHandlerResolver = namespaceHandlerResolver;
	}

	/**
	 * Specify the {@link DocumentLoader} to use.
	 * The default implementation is {@link DefaultDocumentLoader} which loads {@link Document} instances using JAXP.
	 */
	public void setDocumentLoader(@Nullable DocumentLoader documentLoader) {
		this.documentLoader = (documentLoader != null ? documentLoader : new DefaultDocumentLoader());
	}

	/**
	 * Set a SAX entity resolver to be used for parsing.By default, {@link ResourceEntityResolver} will be used.
	 * Can be overridden  for custom entity resolution, for example relative to some specific base path.
	 */
	public void setEntityResolver(@Nullable EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}

	// Return the EntityResolver to use, building a default resolver  if none specified.
	protected EntityResolver getEntityResolver() {
		if (entityResolver == null) {
			// Determine default EntityResolver to use.
			ResourceLoader resourceLoader = getResourceLoader();
			if (resourceLoader != null) {
				entityResolver = new ResourceEntityResolver(resourceLoader);
			}else {
				entityResolver = new DelegatingEntityResolver(getBeanClassLoader());
			}
		}
		return entityResolver;
	}

	/**
	 * Set an implementation of the {@code org.xml.sax.ErrorHandler} interface for custom handling of XML parsing errors and warnings.
	 * If not set, a default SimpleSaxErrorHandler is used that simply
	 * logs warnings using the logger instance of the view class,and rethrows errors to discontinue the XML transformation.
	 * @see SimpleSaxErrorHandler
	 */
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	/**
	 * Specify the {@link BeanDefinitionDocumentReader} implementation to use,responsible for the actual reading of the XML bean definition document.
	 * The default is {@link DefaultBeanDefinitionDocumentReader}.
	 * @param documentReaderClass the desired BeanDefinitionDocumentReader implementation class
	 */
	public void setDocumentReaderClass(Class<? extends BeanDefinitionDocumentReader> documentReaderClass) {
		this.documentReaderClass = documentReaderClass;
	}

	/**
	 * Load bean definitions from the specified XML file.
	 * @param encodedResource the resource descriptor for the XML file,allowing to specify an encoding to use for parsing the file
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
		Assert.notNull(encodedResource, "EncodedResource must not be null");
		if (logger.isTraceEnabled()) logger.trace("Loading XML bean definitions from " + encodedResource);
		/**
		 *  获得正在加载的资源文件
		 * 	1、使用ThreadLocal防止资源文件循环加载  //通过属性来记录已经加载的资源
		 * 	线程安全 ，但这里 currentResources应该本来就是线程安全的，所以推测不是为了线程安全
		 * 	应该是为了线程能使用同一个 currentResources  ，从这里可以看出作者对 ThreadLocal 的理解深刻
		*/
		Set<EncodedResource> currentResources = resourcesCurrentlyBeingLoaded.get();
		// 第一次为空，初始化
		if (currentResources == null) {
			currentResources = new HashSet<>(4);
			resourcesCurrentlyBeingLoaded.set(currentResources);
		}
		// 这里其实就是为了避免循环加载，如果重复加载了相同的文件就会抛出异常   看了半天才明白这个set的意图，蛋疼啊
		// 如果添加当前资源文件不成功，因为是Set（不可重复集合），代表已经有了这个文件  抛出异常（循环加载）
		if (!currentResources.add(encodedResource)) {
			throw new BeanDefinitionStoreException("Detected cyclic loading of " + encodedResource + " - check your import definitions!");
		}
		try {
			// 2、加载BeanDefinition
			InputStream inputStream = encodedResource.getResource().getInputStream();
			try {
				InputSource inputSource = new InputSource(inputStream);
				if (encodedResource.getEncoding() != null) {
					inputSource.setEncoding(encodedResource.getEncoding());
				}
				// 核心部分是这里，往下面看 //这里 真正进入了逻辑核心部分
				// 这个才是主要的逻辑，spring的源码风格，一般以do开头的才是主要做事的。
				return doLoadBeanDefinitions(inputSource, encodedResource.getResource());
			}finally {
				inputStream.close();
			}
		} catch (IOException ex) {
			throw new BeanDefinitionStoreException("IOException parsing XML document from " + encodedResource.getResource(), ex);
		} finally {
			currentResources.remove(encodedResource);
			if (currentResources.isEmpty()) {
				resourcesCurrentlyBeingLoaded.remove();
			}
		}
	}

	/**
	 * Load bean definitions from the specified XML file.
	 * @param inputSource the SAX InputSource to read from
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	public int loadBeanDefinitions(InputSource inputSource) throws BeanDefinitionStoreException {
		return loadBeanDefinitions(inputSource, "resource loaded through SAX InputSource");
	}

	/**
	 * Load bean definitions from the specified XML file.
	 * @param inputSource the SAX InputSource to read from
	 * @param resourceDescription a description of the resource (can be {@code null} or empty)
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	public int loadBeanDefinitions(InputSource inputSource, @Nullable String resourceDescription) throws BeanDefinitionStoreException {
		return doLoadBeanDefinitions(inputSource, new DescriptiveResource(resourceDescription));
	}

	/**
	 * Actually load bean definitions from the specified XML file.
	 * @param inputSource the SAX InputSource to read from
	 * @param resource the resource descriptor for the XML file
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 * @see #doLoadDocument
	 * @see #registerBeanDefinitions
	 */
	protected int doLoadBeanDefinitions(InputSource inputSource, Resource resource) throws BeanDefinitionStoreException {
		try {
			// 加载xml文件并解析得到对应的Document对象
			Document doc = doLoadDocument(inputSource, resource);
			// 根据返回的Document对象  注册bean信息（注册BeanDefinitions）
			int count = registerBeanDefinitions(doc, resource);
			if (logger.isDebugEnabled()) logger.debug("Loaded " + count + " bean definitions from " + resource);
			return count;
		}catch (BeanDefinitionStoreException ex) {
			throw ex;
		} catch (SAXParseException ex) {
			throw new XmlBeanDefinitionStoreException(resource.getDescription(),"Line " + ex.getLineNumber() + " in XML document from " + resource + " is invalid", ex);
		} catch (SAXException ex) {
			throw new XmlBeanDefinitionStoreException(resource.getDescription(),"XML document from " + resource + " is invalid", ex);
		} catch (ParserConfigurationException ex) {
			throw new BeanDefinitionStoreException(resource.getDescription(),"Parser configuration exception parsing XML from " + resource, ex);
		} catch (IOException ex) {
			throw new BeanDefinitionStoreException(resource.getDescription(),"IOException parsing XML document from " + resource, ex);
		} catch (Throwable ex) {
			throw new BeanDefinitionStoreException(resource.getDescription(),"Unexpected exception parsing XML document from " + resource, ex);
		}
	}

	/**
	 * Actually load the specified document using the configured DocumentLoader.
	 * @param inputSource the SAX InputSource to read from
	 * @param resource the resource descriptor for the XML file
	 * @return the DOM Document
	 * @throws Exception when thrown from the DocumentLoader
	 * @see #setDocumentLoader
	 * @see DocumentLoader#loadDocument
	 */
	protected Document doLoadDocument(InputSource inputSource, Resource resource) throws Exception {
		return documentLoader.loadDocument(inputSource, getEntityResolver(), errorHandler,getValidationModeForResource(resource), isNamespaceAware());
	}

	/**
	 * Determine the validation mode for the specified {@link Resource}.
	 * If no explicit validation mode has been configured, then the validation mode gets {@link #detectValidationMode detected} from the given resource.
	 * Override this method if you would like full control over the validation mode, even when something other than {@link #VALIDATION_AUTO} was set.
	 * 判断xml文件是DTD还是XSD样式,如果没定义将使用XSD
	 * @see #detectValidationMode
	 */
	protected int getValidationModeForResource(Resource resource) {
		int validationModeToUse = getValidationMode();
		if (validationModeToUse != VALIDATION_AUTO) return validationModeToUse;
		int detectedMode = detectValidationMode(resource);
		if (detectedMode != VALIDATION_AUTO) return detectedMode;
		// Hmm, we didn't get a clear indication... Let's assume XSD,
		// since apparently no DTD declaration has been found up until
		// detection stopped (before finding the document's root tag).
		return VALIDATION_XSD;
	}

	/**
	 * Detect which kind of validation to perform on the XML file identified by the supplied {@link Resource}.
	 * If the file has a {@code DOCTYPE} definition then DTD validation is used otherwise XSD validation is assumed.
	 * Override this method if you would like to customize resolution of the {@link #VALIDATION_AUTO} mode.
	 * isFile() 函数
	 * 再 Resource 和 AbstractResource 顶层接口和抽象类中 返回 false
	 * 再 InputStreamResource 和 MultipartFileResource 实现类中返回 true
	 */
	protected int detectValidationMode(Resource resource) {
		if (resource.isOpen()) {
			throw new BeanDefinitionStoreException(
					"Passed-in Resource [" + resource + "] contains an open stream: cannot determine validation mode automatically. Either pass in a Resource " +
					"that is able to create fresh streams, or explicitly specify the validationMode on your XmlBeanDefinitionReader instance.");
		}
		InputStream inputStream;
		try {
			inputStream = resource.getInputStream();
		}catch (IOException ex) {
			throw new BeanDefinitionStoreException("Unable to determine validation mode for [" + resource + "]: cannot open InputStream. " +
					"Did you attempt to load directly from a SAX InputSource without specifying the validationMode on your XmlBeanDefinitionReader instance?", ex);
		}
		try {
			return validationModeDetector.detectValidationMode(inputStream);
		}catch (IOException ex) {
			throw new BeanDefinitionStoreException("Unable to determine validation mode for [" + resource + "]: an error occurred whilst reading from the InputStream.", ex);
		}
	}

	/**
	 * Register the bean definitions contained in the given DOM document. Called by {@code loadBeanDefinitions}.
	 * Creates a new instance of the parser class and invokes {@code registerBeanDefinitions} on it.
	 * @param doc the DOM document
	 * @param resource the resource descriptor (for context information)
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of parsing errors
	 * @see #loadBeanDefinitions
	 * @see #setDocumentReaderClass
	 * @see BeanDefinitionDocumentReader#registerBeanDefinitions
	 */
	public int registerBeanDefinitions(Document doc, Resource resource) throws BeanDefinitionStoreException {
		// 委托设计模式： 将bean的注册功能，委托给了 BeanDefinitionDocumentReader
		// 1、创建documentReader对象
		BeanDefinitionDocumentReader documentReader = createBeanDefinitionDocumentReader();
		// 2、读取已经注册的bean个数 // 记录统计前 BeanDefinition 的加载个数
		int countBefore = getRegistry().getBeanDefinitionCount();
		// 3、解析Document并注册BeanDefinition // 加载 和 注册 bean
		documentReader.registerBeanDefinitions(doc, createReaderContext(resource));
		// 4、返回本次注册的bean个数
		return getRegistry().getBeanDefinitionCount() - countBefore;
	}

	/**
	 * Create the {@link BeanDefinitionDocumentReader} to use for actually reading bean definitions from an XML document.
	 * The default implementation instantiates the specified "documentReaderClass".
	 * @see #setDocumentReaderClass
	 */
	protected BeanDefinitionDocumentReader createBeanDefinitionDocumentReader() {
		return BeanUtils.instantiateClass(documentReaderClass);
	}

	/**
	 * Create the {@link XmlReaderContext} to pass over to the document reader.
	 */
	public XmlReaderContext createReaderContext(Resource resource) {
		return new XmlReaderContext(resource, problemReporter, eventListener,sourceExtractor, this, getNamespaceHandlerResolver());
	}

	/**
	 * Lazily create a default NamespaceHandlerResolver, if not set before.
	 * @see #createDefaultNamespaceHandlerResolver()
	 */
	public NamespaceHandlerResolver getNamespaceHandlerResolver() {
		if (namespaceHandlerResolver == null) {
			namespaceHandlerResolver = createDefaultNamespaceHandlerResolver();
		}
		return namespaceHandlerResolver;
	}

	/**
	 * Create the default implementation of {@link NamespaceHandlerResolver} used if none is specified.
	 * The default implementation returns an instance of {@link DefaultNamespaceHandlerResolver}.
	 * @see DefaultNamespaceHandlerResolver#DefaultNamespaceHandlerResolver(ClassLoader)
	 */
	protected NamespaceHandlerResolver createDefaultNamespaceHandlerResolver() {
		ClassLoader cl = (getResourceLoader() != null ? getResourceLoader().getClassLoader() : getBeanClassLoader());
		return new DefaultNamespaceHandlerResolver(cl);
	}

	//---------------------------------------------------------------------
	// Implementation of 【BeanDefinitionReader】 interface
	//---------------------------------------------------------------------
	/**
	 * Load bean definitions from the specified XML file. 从指定的xml配置文件中加载bean
	 * @param resource the resource descriptor for the XML file
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	@Override
	public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
		return loadBeanDefinitions(new EncodedResource(resource));
	}
}
