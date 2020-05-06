

package org.springframework.jmx.export.annotation;


public interface IAnnotationTestBean {

	@ManagedAttribute
	String getColour();

	@ManagedAttribute
	void setColour(String colour);

	@ManagedOperation
	void fromInterface();

	@ManagedOperation
	int getExpensiveToCalculate();
}
