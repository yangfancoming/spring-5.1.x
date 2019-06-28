

package org.springframework.scripting.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * {@code NamespaceHandler} that supports the wiring of
 * objects backed by dynamic languages such as Groovy, JRuby and
 * BeanShell. The following is an example (from the reference
 * documentation) that details the wiring of a Groovy backed bean:
 *
 * <pre class="code">
 * &lt;lang:groovy id="messenger"
 *     refresh-check-delay="5000"
 *     script-source="classpath:Messenger.groovy"&gt;
 * &lt;lang:property name="message" value="I Can Do The Frug"/&gt;
 * &lt;/lang:groovy&gt;
 * </pre>
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @since 2.0
 */
public class LangNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerScriptBeanDefinitionParser("groovy", "org.springframework.scripting.groovy.GroovyScriptFactory");
		registerScriptBeanDefinitionParser("bsh", "org.springframework.scripting.bsh.BshScriptFactory");
		registerScriptBeanDefinitionParser("std", "org.springframework.scripting.support.StandardScriptFactory");
		registerBeanDefinitionParser("defaults", new ScriptingDefaultsParser());
	}

	private void registerScriptBeanDefinitionParser(String key, String scriptFactoryClassName) {
		registerBeanDefinitionParser(key, new ScriptBeanDefinitionParser(scriptFactoryClassName));
	}

}
