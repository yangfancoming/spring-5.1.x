

package org.springframework.oxm.jaxb;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Airplane {

	private String name;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
