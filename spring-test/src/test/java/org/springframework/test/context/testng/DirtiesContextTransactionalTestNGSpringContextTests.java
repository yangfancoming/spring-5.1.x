

package org.springframework.test.context.testng;

import org.testng.annotations.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

import static org.springframework.test.transaction.TransactionTestUtils.*;
import static org.testng.Assert.*;

/**
 * <p>
 * TestNG based integration test to assess the claim in <a
 * href="https://opensource.atlassian.com/projects/spring/browse/SPR-3880"
 * target="_blank">SPR-3880</a> that a &quot;context marked dirty using
 * {@link DirtiesContext &#064;DirtiesContext} in [a] TestNG based test is not
 * reloaded in subsequent tests&quot;.
 * </p>
 * <p>
 * After careful analysis, it turns out that the {@link ApplicationContext} was
 * in fact reloaded; however, due to how the test instance was instrumented with
 * the {@link TestContextManager} in {@link AbstractTestNGSpringContextTests},
 * dependency injection was not being performed on the test instance between
 * individual tests. DirtiesContextTransactionalTestNGSpringContextTests
 * therefore verifies the expected behavior and correct semantics.
 * </p>
 *
 * @author Sam Brannen
 * @since 2.5
 */
@ContextConfiguration
public class DirtiesContextTransactionalTestNGSpringContextTests extends AbstractTransactionalTestNGSpringContextTests {

	private ApplicationContext dirtiedApplicationContext;


	private void performCommonAssertions() {
		assertInTransaction(true);
		assertNotNull(super.applicationContext,
			"The application context should have been set due to ApplicationContextAware semantics.");
		assertNotNull(super.jdbcTemplate,
			"The JdbcTemplate should have been created in setDataSource() via DI for the DataSource.");
	}

	@Test
	@DirtiesContext
	public void dirtyContext() {
		performCommonAssertions();
		this.dirtiedApplicationContext = super.applicationContext;
	}

	@Test(dependsOnMethods = { "dirtyContext" })
	public void verifyContextWasDirtied() {
		performCommonAssertions();
		assertNotSame(super.applicationContext, this.dirtiedApplicationContext,
			"The application context should have been 'dirtied'.");
		this.dirtiedApplicationContext = super.applicationContext;
	}

	@Test(dependsOnMethods = { "verifyContextWasDirtied" })
	public void verifyContextWasNotDirtied() {
		assertSame(this.applicationContext, this.dirtiedApplicationContext,
			"The application context should NOT have been 'dirtied'.");
	}

}
