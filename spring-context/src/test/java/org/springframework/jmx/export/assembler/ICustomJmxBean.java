

package org.springframework.jmx.export.assembler;

/**
 * @author Rob Harrop
 */
public interface ICustomJmxBean extends ICustomBase {

	String getName();

	void setName(String name);

	int getAge();

}
