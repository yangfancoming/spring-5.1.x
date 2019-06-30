

package org.springframework.test.context.testng.transaction.ejb;

import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.transaction.ejb.dao.RequiredEjbTxTestEntityDao;

import org.testng.annotations.Test;

/**
 * Concrete subclass of {@link AbstractEjbTxDaoTestNGTests} which uses the
 * {@link RequiredEjbTxTestEntityDao} and sets the default rollback semantics
 * for the {@link TransactionalTestExecutionListener} to {@code false} (i.e.,
 * <em>commit</em>).
 *
 * @author Sam Brannen
 * @since 4.0.1
 */
@Test(suiteName = "Commit for REQUIRED")
@ContextConfiguration("/org/springframework/test/context/transaction/ejb/required-tx-config.xml")
@Commit
public class CommitForRequiredEjbTxDaoTestNGTests extends AbstractEjbTxDaoTestNGTests {

	/* test methods in superclass */

}
