

package org.springframework.jmx.export.assembler;


public interface ICustomJmxBean extends ICustomBase {

	String getName();

	void setName(String name);

	int getAge();

}
