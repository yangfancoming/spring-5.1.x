

package org.springframework.test.context.testng.transaction.ejb;

import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

/**
 * Extension of {@link CommitForRequiredEjbTxDaoTestNGTests} which sets the default
 * rollback semantics for the {@link TransactionalTestExecutionListener} to
 * {@code true}. The transaction managed by the TestContext framework will be
 * rolled back after each test method. Consequently, any work performed in
 * transactional methods that participate in the test-managed transaction will
 * be rolled back automatically.
 *
 * @author Sam Brannen
 * @since 4.0.1
 */
@Test(suiteName = "Rollback for REQUIRED")
@Rollback
public class RollbackForRequiredEjbTxDaoTestNGTests extends CommitForRequiredEjbTxDaoTestNGTests {

	/**
	 * Overrides parent implementation in order to change expectations to align with
	 * behavior associated with "required" transactions on repositories/DAOs and
	 * default rollback semantics for transactions managed by the TestContext
	 * framework.
	 */
	@Test(dependsOnMethods = "test2IncrementCount1")
	@Override
	public void test3IncrementCount2() {
		int count = dao.getCount(TEST_NAME);
		// Expecting count=0 after test2IncrementCount1() since REQUIRED transactions
		// participate in the existing transaction (if present), which in this case is the
		// transaction managed by the TestContext framework which will be rolled back
		// after each test method.
		assertEquals("Expected count=0 after test2IncrementCount1().", 0, count);

		count = dao.incrementCount(TEST_NAME);
		assertEquals("Expected count=1 now.", 1, count);
	}

}
