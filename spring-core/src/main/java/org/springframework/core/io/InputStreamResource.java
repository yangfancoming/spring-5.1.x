

package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link Resource} implementation for a given {@link InputStream}.
 * Should only be used if no other specific {@code Resource} implementation is applicable.
 * In particular, prefer {@link ByteArrayResource} or any of the file-based {@code Resource} implementations where possible.
 *
 * In contrast to other {@code Resource} implementations, this is a descriptor for an <i>already opened</i> resource - therefore returning {@code true} from {@link #isOpen()}.
 * Do not use an {@code InputStreamResource} if you need to keep the resource descriptor somewhere, or if you need to read from a stream multiple times.
 * @since 28.12.2003
 * @see ByteArrayResource
 * @see ClassPathResource
 * @see FileSystemResource
 * @see UrlResource
 */
public class InputStreamResource extends AbstractResource {

	private final InputStream inputStream;

	private final String description;

	private boolean read = false;

	/**
	 * Create a new InputStreamResource.
	 * @param inputStream the InputStream to use
	 */
	public InputStreamResource(InputStream inputStream) {
		this(inputStream, "resource loaded through InputStream");
	}

	/**
	 * Create a new InputStreamResource.
	 * @param inputStream the InputStream to use
	 * @param description where the InputStream comes from
	 */
	public InputStreamResource(InputStream inputStream, @Nullable String description) {
		Assert.notNull(inputStream, "InputStream must not be null");
		this.inputStream = inputStream;
		this.description = (description != null ? description : "");
	}

	//---------------------------------------------------------------------
	// Implementation of 【InputStreamSource】 interface
	//---------------------------------------------------------------------
	// This implementation throws IllegalStateException if attempting to read the underlying stream multiple times.
	@Override
	public InputStream getInputStream() throws IOException, IllegalStateException {
		if (read) {
			throw new IllegalStateException("InputStream has already been read - do not use InputStreamResource if a stream needs to be read multiple times");
		}
		read = true;
		return inputStream;
	}

	//---------------------------------------------------------------------
	// Implementation of 【Resource】 interface
	//---------------------------------------------------------------------
	// This implementation returns a description that includes the passed-in description, if any.
	@Override
	public String getDescription() {
		return "InputStream resource [" + description + "]";
	}

	//---------------------------------------------------------------------
	// Implementation of 【AbstractResource】 class
	//---------------------------------------------------------------------
	//  This implementation always returns {@code true}.
	@Override
	public boolean exists() {
		return true;
	}

	// This implementation always returns {@code true}.
	@Override
	public boolean isOpen() {
		return true;
	}

	// This implementation compares the underlying InputStream.
	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof InputStreamResource && ((InputStreamResource) other).inputStream.equals(inputStream)));
	}

	// This implementation returns the hash code of the underlying InputStream.
	@Override
	public int hashCode() {
		return inputStream.hashCode();
	}

}
