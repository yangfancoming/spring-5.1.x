

package org.springframework.beans.factory.xml;

import java.util.Collection;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.core.Conventions;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * Simple {@code NamespaceHandler} implementation that maps custom attributes directly through to bean properties.
 * An important point to note is  that this {@code NamespaceHandler} does not have a corresponding schema
 * since there is no way to know in advance all possible attribute names.
 * An example of the usage of this {@code NamespaceHandler} is shown below:
 * &lt;bean id=&quot;author&quot; class=&quot;..TestBean&quot; c:name=&quot;Enescu&quot; c:work-ref=&quot;compositions&quot;/&gt;
 * Here the '{@code c:name}' corresponds directly to the '{@code name} ' argument declared on the constructor of class '{@code TestBean}'.
 * The '{@code c:work-ref}' attributes corresponds to the '{@code work}' argument and,
 * rather than being the concrete value, it contains the name of the bean that will be considered as a parameter.
 * <b>Note</b>: This implementation supports only named parameters - there is no support for indexes or types.
 * Further more, the names are used as hints by the container which, by default, does type introspection.
 * @since 3.1
 * @see SimplePropertyNamespaceHandler
 */
public class SimpleConstructorNamespaceHandler implements NamespaceHandler {

	private static final String REF_SUFFIX = "-ref";

	private static final String DELIMITER_PREFIX = "_";

	@Override
	public void init() {}

	@Override
	@Nullable
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		parserContext.getReaderContext().error("Class [" + getClass().getName() + "] does not support custom elements.", element);
		return null;
	}

	@Override
	public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition, ParserContext parserContext) {
		if (node instanceof Attr) {
			Attr attr = (Attr) node;
			String argName = StringUtils.trimWhitespace(parserContext.getDelegate().getLocalName(attr));
			String argValue = StringUtils.trimWhitespace(attr.getValue());
			ConstructorArgumentValues cvs = definition.getBeanDefinition().getConstructorArgumentValues();
			boolean ref = false;

			// handle -ref arguments
			if (argName.endsWith(REF_SUFFIX)) {
				ref = true;
				argName = argName.substring(0, argName.length() - REF_SUFFIX.length());
			}

			ValueHolder valueHolder = new ValueHolder(ref ? new RuntimeBeanReference(argValue) : argValue);
			valueHolder.setSource(parserContext.getReaderContext().extractSource(attr));

			// handle "escaped"/"_" arguments
			if (argName.startsWith(DELIMITER_PREFIX)) {
				String arg = argName.substring(1).trim();
				// fast default check
				if (!StringUtils.hasText(arg)) {
					cvs.addGenericArgumentValue(valueHolder);
				}else {
				// assume an index otherwise
					int index = -1;
					try {
						index = Integer.parseInt(arg);
					}catch (NumberFormatException ex) {
						parserContext.getReaderContext().error("Constructor argument '" + argName + "' specifies an invalid integer", attr);
					}
					if (index < 0) {
						parserContext.getReaderContext().error("Constructor argument '" + argName + "' specifies a negative index", attr);
					}
					if (cvs.hasIndexedArgumentValue(index)) {
						parserContext.getReaderContext().error("Constructor argument '" + argName + "' with index "+ index+" already defined using <constructor-arg>. Only one approach may be used per argument.", attr);
					}
					cvs.addIndexedArgumentValue(index, valueHolder);
				}
			}else {
			// no escaping -> ctr name
				String name = Conventions.attributeNameToPropertyName(argName);
				if (containsArgWithName(name, cvs)) {
					parserContext.getReaderContext().error("Constructor argument '" + argName + "' already defined using <constructor-arg>. Only one approach may be used per argument.", attr);
				}
				valueHolder.setName(Conventions.attributeNameToPropertyName(argName));
				cvs.addGenericArgumentValue(valueHolder);
			}
		}
		return definition;
	}

	private boolean containsArgWithName(String name, ConstructorArgumentValues cvs) {
		return (checkName(name, cvs.getGenericArgumentValues()) || checkName(name, cvs.getIndexedArgumentValues().values()));
	}

	private boolean checkName(String name, Collection<ValueHolder> values) {
		for (ValueHolder holder : values) {
			if (name.equals(holder.getName())) {
				return true;
			}
		}
		return false;
	}

}
