

package org.springframework.core.codec;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

/**
 * Abstract base class for {@link Decoder} implementations.
 *
 * @author Sebastien Deleuze
 * @author Arjen Poutsma
 * @since 5.0
 * @param <T> the element type
 */
public abstract class AbstractEncoder<T> implements Encoder<T> {

	private final List<MimeType> encodableMimeTypes;

	protected Log logger = LogFactory.getLog(getClass());


	protected AbstractEncoder(MimeType... supportedMimeTypes) {
		this.encodableMimeTypes = Arrays.asList(supportedMimeTypes);
	}


	/**
	 * Set an alternative logger to use than the one based on the class name.
	 * @param logger the logger to use
	 * @since 5.1
	 */
	public void setLogger(Log logger) {
		this.logger = logger;
	}

	/**
	 * Return the currently configured Logger.
	 * @since 5.1
	 */
	public Log getLogger() {
		return logger;
	}


	@Override
	public List<MimeType> getEncodableMimeTypes() {
		return this.encodableMimeTypes;
	}

	@Override
	public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
		if (mimeType == null) {
			return true;
		}
		for (MimeType candidate : this.encodableMimeTypes) {
			if (candidate.isCompatibleWith(mimeType)) {
				return true;
			}
		}
		return false;
	}

}
