

package org.springframework.test.context.transaction.ejb;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static org.junit.Assert.*;

/**
 * Extension of {@link CommitForRequiredEjbTxDaoTests} which sets the default
 * rollback semantics for the {@link TransactionalTestExecutionListener} to
 * {@code true}. The transaction managed by the TestContext framework will be
 * rolled back after each test method. Consequently, any work performed in
 * transactional methods that participate in the test-managed transaction will
 * be rolled back automatically.
 *
 * @author Sam Brannen
 * @since 4.0.1
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Rollback
public class RollbackForRequiredEjbTxDaoTests extends CommitForRequiredEjbTxDaoTests {

	/**
	 * Redeclared to ensure test method execution order. Simply delegates to super.
	 */
	@Test
	@Override
	public void test1InitialState() {
		super.test1InitialState();
	}

	/**
	 * Redeclared to ensure test method execution order. Simply delegates to super.
	 */
	@Test
	@Override
	public void test2IncrementCount1() {
		super.test2IncrementCount1();
	}

	/**
	 * Overrides parent implementation in order to change expectations to align with
	 * behavior associated with "required" transactions on repositories/DAOs and
	 * default rollback semantics for transactions managed by the TestContext
	 * framework.
	 */
	@Test
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
