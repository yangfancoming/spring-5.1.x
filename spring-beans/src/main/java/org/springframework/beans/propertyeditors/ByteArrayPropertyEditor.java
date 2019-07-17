

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.lang.Nullable;

/**
 * Editor for byte arrays. Strings will simply be converted to
 * their corresponding byte representations.
 *

 * @since 1.0.1
 * @see java.lang.String#getBytes
 */
public class ByteArrayPropertyEditor extends PropertyEditorSupport {

	@Override
	public void setAsText(@Nullable String text) {
		setValue(text != null ? text.getBytes() : null);
	}

	@Override
	public String getAsText() {
		byte[] value = (byte[]) getValue();
		return (value != null ? new String(value) : "");
	}

}
