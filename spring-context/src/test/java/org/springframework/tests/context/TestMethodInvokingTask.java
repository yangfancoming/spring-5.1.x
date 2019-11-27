

package org.springframework.tests.context;

/**
 * @since 09.10.2004
 */
public class TestMethodInvokingTask {

	public int counter = 0;

	private Object lock = new Object();

	public void doSomething() {
		this.counter++;
	}

	public void doWait() {
		this.counter++;
		// wait until stop is called
		synchronized (this.lock) {
			try {
				this.lock.wait();
			}
			catch (InterruptedException e) {
				// fall through
			}
		}
	}

	public void stop() {
		synchronized(this.lock) {
			this.lock.notify();
		}
	}

}
