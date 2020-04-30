

package org.springframework.web.reactive.resource;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link FixedVersionStrategy}.
 *
 * @author Brian Clozel
 */
public class FixedVersionStrategyTests {

	private static final String VERSION = "1df341f";

	private static final String PATH = "js/foo.js";


	private FixedVersionStrategy strategy;


	@Before
	public void setup() {
		this.strategy = new FixedVersionStrategy(VERSION);
	}


	@Test(expected = IllegalArgumentException.class)
	public void emptyPrefixVersion() {
		new FixedVersionStrategy("  ");
	}

	@Test
	public void extractVersion() {
		assertEquals(VERSION, this.strategy.extractVersion(VERSION + "/" + PATH));
		assertNull(this.strategy.extractVersion(PATH));
	}

	@Test
	public void removeVersion() {
		assertEquals("/" + PATH, this.strategy.removeVersion(VERSION + "/" + PATH, VERSION));
	}

	@Test
	public void addVersion() {
		assertEquals(VERSION + "/" + PATH, this.strategy.addVersion("/" + PATH, VERSION));
	}

	@Test  // SPR-13727
	public void addVersionRelativePath() {
		String relativePath = "../" + PATH;
		assertEquals(relativePath, this.strategy.addVersion(relativePath, VERSION));
	}

}
