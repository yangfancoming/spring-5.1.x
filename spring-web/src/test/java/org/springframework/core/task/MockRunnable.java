

package org.springframework.core.task;


public class MockRunnable implements Runnable {

	private boolean executed = false;

	@Override
	public void run() {
		this.executed = true;
	}

	public boolean wasExecuted() {
		return this.executed;
	}

}
