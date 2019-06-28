

package org.springframework.context.index.sample.type;

import org.springframework.stereotype.Indexed;

/**
 * A {@link Repo} that requires an extra stereotype.
 *
 * @author Stephane Nicoll
 */
@Indexed
public interface SmartRepo<T, I> extends Repo<T, I> {
}
