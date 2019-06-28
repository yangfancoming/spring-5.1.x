

package org.springframework.context.index.sample;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.type.Scope;

/**
 * Candidate with no matching annotation.
 *
 * @author Stephane Nicoll
 */
@Scope("None")
@Qualifier("None")
public class SampleNone {
}
