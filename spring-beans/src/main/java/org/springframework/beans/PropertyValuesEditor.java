

package org.springframework.beans;

import java.beans.PropertyEditorSupport;
import java.util.Properties;

import org.springframework.beans.propertyeditors.PropertiesEditor;

/**
 * {@link java.beans.PropertyEditor Editor} for a {@link PropertyValues} object.
 * The required format is defined in the {@link java.util.Properties}
 * documentation. Each property must be on a new line.
 * The present implementation relies on a
 * {@link org.springframework.beans.propertyeditors.PropertiesEditor} underneath.
 */
public class PropertyValuesEditor extends PropertyEditorSupport {

	private final PropertiesEditor propertiesEditor = new PropertiesEditor();

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		this.propertiesEditor.setAsText(text);
		Properties props = (Properties) this.propertiesEditor.getValue();
		setValue(new MutablePropertyValues(props));
	}

}

