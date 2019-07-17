

package org.springframework.jmx;

import java.io.IOException;

/**
 * @@org.springframework.jmx.export.metadata.ManagedResource
 *    (description="My Managed Bean", objectName="spring:bean=test",
 *    log=true, logFile="jmx.log", currencyTimeLimit=15, persistPolicy="OnUpdate",
 *    persistPeriod=200, persistLocation="./foo", persistName="bar.jmx")
 * @@org.springframework.jmx.export.metadata.ManagedNotification
 *    (name="My Notification", description="A Notification", notificationType="type.foo,type.bar")
 * @author Rob Harrop

 */
public class JmxTestBean implements IJmxTestBean {

	private String name;

	private String nickName;

	private int age;

	private boolean isSuperman;


	/**
	 * @@org.springframework.jmx.export.metadata.ManagedAttribute
	 *   (description="The Age Attribute", currencyTimeLimit=15)
	 */
	@Override
	public int getAge() {
		return age;
	}

	@Override
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @@org.springframework.jmx.export.metadata.ManagedOperation(currencyTimeLimit=30)
	 */
	@Override
	public long myOperation() {
		return 1L;
	}

	/**
	 * @@org.springframework.jmx.export.metadata.ManagedAttribute
	 *  (description="The Name Attribute",  currencyTimeLimit=20,
	 *   defaultValue="bar", persistPolicy="OnUpdate")
	 */
	@Override
	public void setName(String name) throws Exception {
		if ("Juergen".equals(name)) {
			throw new IllegalArgumentException("Juergen");
		}
		if ("Juergen Class".equals(name)) {
			throw new ClassNotFoundException("Juergen");
		}
		if ("Juergen IO".equals(name)) {
			throw new IOException("Juergen");
		}
		this.name = name;
	}

	/**
	 * @@org.springframework.jmx.export.metadata.ManagedAttribute
	 *   (defaultValue="foo", persistPeriod=300)
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @@org.springframework.jmx.export.metadata.ManagedAttribute(description="The Nick
	 *                                                                              Name
	 *                                                                              Attribute")
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getNickName() {
		return this.nickName;
	}

	public void setSuperman(boolean superman) {
		this.isSuperman = superman;
	}

	/**
	 * @@org.springframework.jmx.export.metadata.ManagedAttribute(description="The Is
	 *                                                                              Superman
	 *                                                                              Attribute")
	 */
	public boolean isSuperman() {
		return isSuperman;
	}

	/**
	 * @@org.springframework.jmx.export.metadata.ManagedOperation(description="Add Two
	 *                                                                              Numbers
	 *                                                                              Together")
	 * @@org.springframework.jmx.export.metadata.ManagedOperationParameter(index=0, name="x", description="Left operand")
	 * @@org.springframework.jmx.export.metadata.ManagedOperationParameter(index=1, name="y", description="Right operand")
	 */
	@Override
	public int add(int x, int y) {
		return x + y;
	}

	/**
	 * Test method that is not exposed by the MetadataAssembler.
	 */
	@Override
	public void dontExposeMe() {
		throw new RuntimeException();
	}

	protected void someProtectedMethod() {
	}

	@SuppressWarnings("unused")
	private void somePrivateMethod() {
	}

	protected void getSomething() {
	}

	@SuppressWarnings("unused")
	private void getSomethingElse() {
	}

}
