

package org.springframework.jndi;

import java.beans.PropertyEditorSupport;
import java.util.Properties;

import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.lang.Nullable;

/**
 * Properties editor for JndiTemplate objects. Allows properties of type
 * JndiTemplate to be populated with a properties-format string.
 *
 * @author Rod Johnson
 * @since 09.05.2003
 */
public class JndiTemplateEditor extends PropertyEditorSupport {

	private final PropertiesEditor propertiesEditor = new PropertiesEditor();

	@Override
	public void setAsText(@Nullable String text) throws IllegalArgumentException {
		if (text == null) {
			throw new IllegalArgumentException("JndiTemplate cannot be created from null string");
		}
		if ("".equals(text)) {
			// empty environment
			setValue(new JndiTemplate());
		}
		else {
			// we have a non-empty properties string
			this.propertiesEditor.setAsText(text);
			Properties props = (Properties) this.propertiesEditor.getValue();
			setValue(new JndiTemplate(props));
		}
	}

}
