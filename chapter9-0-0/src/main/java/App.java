import org.junit.Test;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2019/11/27.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/11/27---20:12
 */
public class App {

	@Test
	public void test(){
		List<String> list = Arrays.asList("1","2","3");
		String s = StringUtils.collectionToDelimitedString(list, "*");
		System.out.println(s);
	}
}
