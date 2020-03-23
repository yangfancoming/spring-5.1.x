

package org.springframework.web.reactive.resource;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/**
 * An extension of {@link ByteArrayResource} that a {@link ResourceTransformer}
 * can use to represent an original resource preserving all other information
 * except the content.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public class TransformedResource extends ByteArrayResource {

	@Nullable
	private final String filename;

	private final long lastModified;


	public TransformedResource(Resource original, byte[] transformedContent) {
		super(transformedContent);
		this.filename = original.getFilename();
		try {
			this.lastModified = original.lastModified();
		}
		catch (IOException ex) {
			// should never happen
			throw new IllegalArgumentException(ex);
		}
	}


	@Override
	@Nullable
	public String getFilename() {
		return this.filename;
	}

	@Override
	public long lastModified() {
		return this.lastModified;
	}

}
