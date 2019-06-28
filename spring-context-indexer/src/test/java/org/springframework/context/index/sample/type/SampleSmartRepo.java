

package org.springframework.context.index.sample.type;

/**
 * A sample that implements both interface used to demonstrate that no
 * duplicate stereotypes are generated.
 *
 * @author Stephane Nicoll
 */
public class SampleSmartRepo
		implements SmartRepo<SampleEntity, Long>, Repo<SampleEntity, Long> {
}
