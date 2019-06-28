

package org.springframework.context.index.sample.type;

import org.springframework.stereotype.Indexed;

/**
 * A sample interface flagged with {@link Indexed} to indicate that a stereotype
 * for all implementations should be added to the index.
 *
 * @author Stephane Nicoll
 */
@Indexed
public interface Repo<T, I> {
}
