

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * One-way PropertyEditor which can convert from a text String to a
 * {@code java.io.InputStream}, interpreting the given String as a
 * Spring resource location (e.g. a URL String).
 *
 * Supports Spring-style URL notation: any fully qualified standard URL
 * ("file:", "http:", etc.) and Spring's special "classpath:" pseudo-URL.
 *
 * Note that such streams usually do not get closed by Spring itself!
 *

 * @since 1.0.1
 * @see java.io.InputStream
 * @see org.springframework.core.io.ResourceEditor
 * @see org.springframework.core.io.ResourceLoader
 * @see URLEditor
 * @see FileEditor
 */
public class InputStreamEditor extends PropertyEditorSupport {

	private final ResourceEditor resourceEditor;


	/**
	 * Create a new InputStreamEditor, using the default ResourceEditor underneath.
	 */
	public InputStreamEditor() {
		this.resourceEditor = new ResourceEditor();
	}

	/**
	 * Create a new InputStreamEditor, using the given ResourceEditor underneath.
	 * @param resourceEditor the ResourceEditor to use
	 */
	public InputStreamEditor(ResourceEditor resourceEditor) {
		Assert.notNull(resourceEditor, "ResourceEditor must not be null");
		this.resourceEditor = resourceEditor;
	}


	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		this.resourceEditor.setAsText(text);
		Resource resource = (Resource) this.resourceEditor.getValue();
		try {
			setValue(resource != null ? resource.getInputStream() : null);
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Failed to retrieve InputStream for " + resource, ex);
		}
	}

	/**
	 * This implementation returns {@code null} to indicate that
	 * there is no appropriate text representation.
	 */
	@Override
	@Nullable
	public String getAsText() {
		return null;
	}

}
