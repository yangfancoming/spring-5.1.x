

package org.springframework.oxm.castor;

/**
 * Represents a POJO used by {@link CastorMarshallerTests} for testing the marshaller output.
 *
 * @author Jakub Narloch
 */
public class CastorObject {

	private String name;

	private Integer value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
}
