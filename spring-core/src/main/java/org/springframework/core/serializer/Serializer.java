

package org.springframework.core.serializer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A strategy interface for streaming an object to an OutputStream.
 *
 * @author Gary Russell
 * @author Mark Fisher
 * @since 3.0.5
 * @param <T> the object type
 */
@FunctionalInterface
public interface Serializer<T> {

	/**
	 * Write an object of type T to the given OutputStream.
	 * Note: Implementations should not close the given OutputStream
	 * (or any decorators of that OutputStream) but rather leave this up
	 * to the caller.
	 * @param object the object to serialize
	 * @param outputStream the output stream
	 * @throws IOException in case of errors writing to the stream
	 */
	void serialize(T object, OutputStream outputStream) throws IOException;

}
