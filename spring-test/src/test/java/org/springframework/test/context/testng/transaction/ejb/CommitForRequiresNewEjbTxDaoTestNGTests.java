

package org.springframework.test.context.testng.transaction.ejb;

import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.transaction.ejb.dao.RequiresNewEjbTxTestEntityDao;

import org.testng.annotations.Test;

/**
 * Concrete subclass of {@link AbstractEjbTxDaoTestNGTests} which uses the
 * {@link RequiresNewEjbTxTestEntityDao} and sets the default rollback semantics
 * for the {@link TransactionalTestExecutionListener} to {@code false} (i.e.,
 * <em>commit</em>).
 *
 * @author Sam Brannen
 * @since 4.0.1
 */
@Test(suiteName = "Commit for REQUIRES_NEW")
@ContextConfiguration("/org/springframework/test/context/transaction/ejb/requires-new-tx-config.xml")
@Commit
public class CommitForRequiresNewEjbTxDaoTestNGTests extends AbstractEjbTxDaoTestNGTests {

	/* test methods in superclass */

}
