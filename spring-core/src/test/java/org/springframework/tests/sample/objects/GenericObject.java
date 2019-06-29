

package org.springframework.tests.sample.objects;

import java.util.List;

import org.springframework.core.io.Resource;

public class GenericObject<T> {

	private List<Resource> resourceList;

	public List<Resource> getResourceList() {
		return this.resourceList;
	}

	public void setResourceList(List<Resource> resourceList) {
		this.resourceList = resourceList;
	}

}
