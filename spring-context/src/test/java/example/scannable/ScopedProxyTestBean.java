

package example.scannable;

import java.util.concurrent.Future;

import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Mark Fisher

 */
@Scope("myScope")
public class ScopedProxyTestBean implements FooService {

	@Override
	public String foo(int id) {
		return "bar";
	}

	@Override
	public Future<String> asyncFoo(int id) {
		return new AsyncResult<>("bar");
	}

	@Override
	public boolean isInitCalled() {
		return false;
	}

}
