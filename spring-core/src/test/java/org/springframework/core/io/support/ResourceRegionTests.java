

package org.springframework.core.io.support;

import org.junit.Test;

import org.springframework.core.io.Resource;

import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link ResourceRegion} class.
 *
 * @author Brian Clozel
 */
public class ResourceRegionTests {

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWithNullResource() {
		new ResourceRegion(null, 0, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionForNegativePosition() {
		new ResourceRegion(mock(Resource.class), -1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionForNegativeCount() {
		new ResourceRegion(mock(Resource.class), 0, -1);
	}

}
