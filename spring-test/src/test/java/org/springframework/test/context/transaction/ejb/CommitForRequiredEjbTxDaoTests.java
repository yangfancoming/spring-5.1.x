

package org.springframework.test.context.transaction.ejb;

import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.transaction.ejb.dao.RequiredEjbTxTestEntityDao;

/**
 * Concrete subclass of {@link AbstractEjbTxDaoTests} which uses the
 * {@link RequiredEjbTxTestEntityDao} and sets the default rollback semantics
 * for the {@link TransactionalTestExecutionListener} to {@code false} (i.e.,
 * <em>commit</em>).
 *
 * @author Sam Brannen
 * @since 4.0.1
 */
@ContextConfiguration("required-tx-config.xml")
@Commit
public class CommitForRequiredEjbTxDaoTests extends AbstractEjbTxDaoTests {

	/* test methods in superclass */

}
