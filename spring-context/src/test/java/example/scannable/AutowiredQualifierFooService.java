

package example.scannable;

import java.util.concurrent.Future;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Mark Fisher

 */
@Lazy
public class AutowiredQualifierFooService implements FooService {

	@Autowired
	@Qualifier("testing")
	private FooDao fooDao;

	private boolean initCalled = false;

	@PostConstruct
	private void init() {
		if (this.initCalled) {
			throw new IllegalStateException("Init already called");
		}
		this.initCalled = true;
	}

	@Override
	public String foo(int id) {
		return this.fooDao.findFoo(id);
	}

	@Override
	public Future<String> asyncFoo(int id) {
		return new AsyncResult<>(this.fooDao.findFoo(id));
	}

	@Override
	public boolean isInitCalled() {
		return this.initCalled;
	}

}
