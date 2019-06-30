

package org.springframework.test.context.testng.transaction.ejb;

import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import org.testng.annotations.Test;

/**
 * Extension of {@link CommitForRequiresNewEjbTxDaoTestNGTests} which sets the default
 * rollback semantics for the {@link TransactionalTestExecutionListener} to
 * {@code true}. The transaction managed by the TestContext framework will be
 * rolled back after each test method. Consequently, any work performed in
 * transactional methods that participate in the test-managed transaction will
 * be rolled back automatically. On the other hand, any work performed in
 * transactional methods that do <strong>not</strong> participate in the
 * test-managed transaction will not be affected by the rollback of the
 * test-managed transaction. For example, such work may in fact be committed
 * outside the scope of the test-managed transaction.
 *
 * @author Sam Brannen
 * @since 4.0.1
 */
@Test(suiteName = "Rollback for REQUIRES_NEW")
@Rollback
public class RollbackForRequiresNewEjbTxDaoTestNGTests extends CommitForRequiresNewEjbTxDaoTestNGTests {

	/* test methods in superclass */

}
