import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2019/11/27.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/11/27---20:12
 */
public class App {

	@Test
	public void tes1t(){
		GenericApplicationContext temp = new GenericApplicationContext();
		Assert.assertTrue(temp instanceof BeanFactory);
		Assert.assertTrue(temp instanceof ResourceLoader);
		Assert.assertTrue(temp instanceof ApplicationContext);
		Assert.assertTrue(temp instanceof EnvironmentCapable);
		Assert.assertTrue(temp instanceof AbstractApplicationContext);
	}


	@Test
	public void test(){
		List<String> list = Arrays.asList("1","2","3");
		String s = StringUtils.collectionToDelimitedString(list, "*");
		System.out.println(s);
	}

	private final Map<String, String> aliasMap = new ConcurrentHashMap<>(16);

	@Test
	public void test1(){
		aliasMap.put("1","11");
		aliasMap.put("2","2");
		Assert.assertEquals(2,aliasMap.size());

		aliasMap.remove("1");
		Assert.assertEquals(1,aliasMap.size());

		Assert.assertEquals(null,aliasMap.remove("3"));
	}
}
