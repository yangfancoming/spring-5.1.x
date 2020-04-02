

package org.springframework.jmx.export.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;

/**
 * Convenient subclass of Spring's standard {@link MBeanExporter},
 * activating Java 5 annotation usage for JMX exposure of Spring beans:
 * {@link ManagedResource}, {@link ManagedAttribute}, {@link ManagedOperation}, etc.
 *
 * Sets a {@link MetadataNamingStrategy} and a {@link MetadataMBeanInfoAssembler}
 * with an {@link AnnotationJmxAttributeSource}, and activates the
 * {@link #AUTODETECT_ALL} mode by default.
 *

 * @since 2.5
 */
public class AnnotationMBeanExporter extends MBeanExporter {

	private final AnnotationJmxAttributeSource annotationSource =
			new AnnotationJmxAttributeSource();

	private final MetadataNamingStrategy metadataNamingStrategy =
			new MetadataNamingStrategy(this.annotationSource);

	private final MetadataMBeanInfoAssembler metadataAssembler =
			new MetadataMBeanInfoAssembler(this.annotationSource);


	public AnnotationMBeanExporter() {
		setNamingStrategy(this.metadataNamingStrategy);
		setAssembler(this.metadataAssembler);
		setAutodetectMode(AUTODETECT_ALL);
	}


	/**
	 * Specify the default domain to be used for generating ObjectNames
	 * when no source-level metadata has been specified.
	 * The default is to use the domain specified in the bean name
	 * (if the bean name follows the JMX ObjectName syntax); else,
	 * the package name of the managed bean class.
	 * @see MetadataNamingStrategy#setDefaultDomain
	 */
	public void setDefaultDomain(String defaultDomain) {
		this.metadataNamingStrategy.setDefaultDomain(defaultDomain);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		super.setBeanFactory(beanFactory);
		this.annotationSource.setBeanFactory(beanFactory);
	}

}
