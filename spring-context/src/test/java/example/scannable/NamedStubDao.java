

package example.scannable;

import org.springframework.stereotype.Repository;

/**
 * @author Juergen Hoeller
 */
@Repository("myNamedDao")
public class NamedStubDao {

	public String find(int id) {
		return "bar";
	}

}
