

package org.springframework.beans.factory.xml;

import java.io.StringReader;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.parsing.ReaderContext;
import org.springframework.beans.factory.parsing.ReaderEventListener;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

/**
 * Extension of {@link org.springframework.beans.factory.parsing.ReaderContext},specific to use with an {@link XmlBeanDefinitionReader}.
 * Provides access to the {@link NamespaceHandlerResolver} configured in the {@link XmlBeanDefinitionReader}.
 * @since 2.0
 */
public class XmlReaderContext extends ReaderContext {

	private final XmlBeanDefinitionReader reader;

	private final NamespaceHandlerResolver namespaceHandlerResolver;

	/**
	 * Construct a new {@code XmlReaderContext}.
	 * @param resource the XML bean definition resource
	 * @param problemReporter the problem reporter in use
	 * @param eventListener the event listener in use
	 * @param sourceExtractor the source extractor in use
	 * @param reader the XML bean definition reader in use
	 * @param namespaceHandlerResolver the XML namespace resolver
	 */
	public XmlReaderContext(Resource resource, ProblemReporter problemReporter,ReaderEventListener eventListener, SourceExtractor sourceExtractor,XmlBeanDefinitionReader reader, NamespaceHandlerResolver namespaceHandlerResolver) {
		super(resource, problemReporter, eventListener, sourceExtractor);
		this.reader = reader;
		this.namespaceHandlerResolver = namespaceHandlerResolver;
	}

	/**
	 * Return the XML bean definition reader in use.
	 */
	public final XmlBeanDefinitionReader getReader() {
		return reader;
	}

	/**
	 * Return the bean definition registry to use.
	 * @see XmlBeanDefinitionReader#XmlBeanDefinitionReader(BeanDefinitionRegistry)
	 */
	public final BeanDefinitionRegistry getRegistry() {
		return reader.getRegistry();
	}

	/**
	 * Return the resource loader to use, if any.
	 * This will be non-null in regular scenarios,also allowing access to the resource class loader.
	 * @see XmlBeanDefinitionReader#setResourceLoader
	 * @see ResourceLoader#getClassLoader()
	 */
	@Nullable
	public final ResourceLoader getResourceLoader() {
		return reader.getResourceLoader();
	}

	/**
	 * Return the bean class loader to use, if any.
	 * Note that this will be null in regular scenarios,as an indication to lazily resolve bean classes.
	 * @see XmlBeanDefinitionReader#setBeanClassLoader
	 */
	@Nullable
	public final ClassLoader getBeanClassLoader() {
		return reader.getBeanClassLoader();
	}

	/**
	 * Return the environment to use.
	 * @see XmlBeanDefinitionReader#setEnvironment
	 */
	public final Environment getEnvironment() {
		return reader.getEnvironment();
	}

	/**
	 * Return the namespace resolver.
	 * @see XmlBeanDefinitionReader#setNamespaceHandlerResolver
	 */
	public final NamespaceHandlerResolver getNamespaceHandlerResolver() {
		return namespaceHandlerResolver;
	}


	// Convenience methods to delegate to
	/**
	 * Call the bean name generator for the given bean definition.
	 * @see XmlBeanDefinitionReader#getBeanNameGenerator()
	 * @see org.springframework.beans.factory.support.BeanNameGenerator#generateBeanName
	 */
	public String generateBeanName(BeanDefinition beanDefinition) {
		return reader.getBeanNameGenerator().generateBeanName(beanDefinition, getRegistry());
	}

	/**
	 * Call the bean name generator for the given bean definition and register the bean definition under the generated name.
	 * @see XmlBeanDefinitionReader#getBeanNameGenerator()
	 * @see org.springframework.beans.factory.support.BeanNameGenerator#generateBeanName
	 * @see BeanDefinitionRegistry#registerBeanDefinition
	 */
	public String registerWithGeneratedName(BeanDefinition beanDefinition) {
		String generatedName = generateBeanName(beanDefinition);
		getRegistry().registerBeanDefinition(generatedName, beanDefinition);
		return generatedName;
	}

	/**
	 * Read an XML document from the given String.
	 * @see #getReader()
	 */
	public Document readDocumentFromString(String documentContent) {
		InputSource is = new InputSource(new StringReader(documentContent));
		try {
			return reader.doLoadDocument(is, getResource());
		}catch (Exception ex) {
			throw new BeanDefinitionStoreException("Failed to read XML document", ex);
		}
	}

}
