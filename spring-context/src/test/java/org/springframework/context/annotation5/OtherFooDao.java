

package org.springframework.context.annotation5;

import example.scannable.FooDao;


@MyRepository
public class OtherFooDao implements FooDao {

	@Override
	public String findFoo(int id) {
		return "other";
	}

}
