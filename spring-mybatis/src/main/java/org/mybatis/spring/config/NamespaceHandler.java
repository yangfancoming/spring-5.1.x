
package org.mybatis.spring.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Namespace handler for the MyBatis namespace.
 * @see MapperScannerBeanDefinitionParser
 * @since 1.2.0
 */
public class NamespaceHandler extends NamespaceHandlerSupport {

  @Override
  public void init() {
    registerBeanDefinitionParser("scan", new MapperScannerBeanDefinitionParser());
  }
}
