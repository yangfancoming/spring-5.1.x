

package example.scannable;

import org.springframework.stereotype.Repository;


@Repository("myNamedDao")
public class NamedStubDao {

	public String find(int id) {
		return "bar";
	}

}
