

package org.springframework.jdbc.config;

import org.w3c.dom.Element;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * {@link org.springframework.beans.factory.xml.BeanDefinitionParser} that parses an {@code initialize-database}
 * element and creates a {@link BeanDefinition} of type {@link DataSourceInitializer}. Picks up nested
 * {@code script} elements and configures a {@link ResourceDatabasePopulator} for them.
 *
 * @author Dave Syer

 * @since 3.0
 */
class InitializeDatabaseBeanDefinitionParser extends AbstractBeanDefinitionParser {

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceInitializer.class);
		builder.addPropertyReference("dataSource", element.getAttribute("data-source"));
		builder.addPropertyValue("enabled", element.getAttribute("enabled"));
		DatabasePopulatorConfigUtils.setDatabasePopulator(element, builder);
		builder.getRawBeanDefinition().setSource(parserContext.extractSource(element));
		return builder.getBeanDefinition();
	}

	@Override
	protected boolean shouldGenerateId() {
		return true;
	}

}
