

package org.springframework.context.annotation2;

import org.springframework.stereotype.Repository;


@Repository("myNamedDao")
public class NamedStubDao2 {

	public String find(int id) {
		return "bar";
	}

}
