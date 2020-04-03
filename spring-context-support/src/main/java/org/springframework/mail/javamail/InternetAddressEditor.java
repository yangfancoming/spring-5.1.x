

package org.springframework.mail.javamail;

import java.beans.PropertyEditorSupport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.springframework.util.StringUtils;

/**
 * Editor for {@code java.mail.internet.InternetAddress},
 * to directly populate an InternetAddress property.
 *
 * xmlBeanDefinitionReaderExpects the same syntax as InternetAddress's constructor with
 * a String argument. Converts empty Strings into null values.
 *

 * @since 1.2.3
 * @see javax.mail.internet.InternetAddress
 */
public class InternetAddressEditor extends PropertyEditorSupport {

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			try {
				setValue(new InternetAddress(text));
			}
			catch (AddressException ex) {
				throw new IllegalArgumentException("Could not parse mail address: " + ex.getMessage());
			}
		}
		else {
			setValue(null);
		}
	}

	@Override
	public String getAsText() {
		InternetAddress value = (InternetAddress) getValue();
		return (value != null ? value.toUnicodeString() : "");
	}

}
