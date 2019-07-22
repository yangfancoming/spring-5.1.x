

package org.springframework.transaction.jta;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.transaction.Synchronization;

import com.ibm.wsspi.uow.UOWAction;
import com.ibm.wsspi.uow.UOWActionException;
import com.ibm.wsspi.uow.UOWException;
import com.ibm.wsspi.uow.UOWManager;


public class MockUOWManager implements UOWManager {

	private int type = UOW_TYPE_GLOBAL_TRANSACTION;

	private boolean joined;

	private int timeout;

	private boolean rollbackOnly;

	private int status = UOW_STATUS_NONE;

	private final Map<Object, Object> resources = new HashMap<>();

	private final List<Synchronization> synchronizations = new LinkedList<>();


	@Override
	public void runUnderUOW(int type, boolean join, UOWAction action) throws UOWActionException, UOWException {
		this.type = type;
		this.joined = join;
		try {
			this.status = UOW_STATUS_ACTIVE;
			action.run();
			this.status = (this.rollbackOnly ? UOW_STATUS_ROLLEDBACK : UOW_STATUS_COMMITTED);
		}
		catch (Error | RuntimeException ex) {
			this.status = UOW_STATUS_ROLLEDBACK;
			throw ex;
		} catch (Exception ex) {
			this.status = UOW_STATUS_ROLLEDBACK;
			throw new UOWActionException(ex);
		}
	}

	@Override
	public int getUOWType() {
		return this.type;
	}

	public boolean getJoined() {
		return this.joined;
	}

	@Override
	public long getLocalUOWId() {
		return 0;
	}

	@Override
	public void setUOWTimeout(int uowType, int timeout) {
		this.timeout = timeout;
	}

	@Override
	public int getUOWTimeout() {
		return this.timeout;
	}

	@Override
	public void setRollbackOnly() {
		this.rollbackOnly = true;
	}

	@Override
	public boolean getRollbackOnly() {
		return this.rollbackOnly;
	}

	public void setUOWStatus(int status) {
		this.status = status;
	}

	@Override
	public int getUOWStatus() {
		return this.status;
	}

	@Override
	public void putResource(Object key, Object value) {
		this.resources.put(key, value);
	}

	@Override
	public Object getResource(Object key) throws NullPointerException {
		return this.resources.get(key);
	}

	@Override
	public void registerInterposedSynchronization(Synchronization sync) {
		this.synchronizations.add(sync);
	}

	public List<Synchronization> getSynchronizations() {
		return this.synchronizations;
	}

}
