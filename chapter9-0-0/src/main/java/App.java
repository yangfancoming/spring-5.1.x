import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.ResourceLoader;
import temp.Test1;


import java.lang.reflect.Constructor;

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
	public void tes1t1(){

		System.out.println(judge(Test1.class));
	}



	public String judge(Class<?> clazz) {
		Constructor<?>[] constructors = clazz.getConstructors();
		for (Constructor<?> constructor : constructors) {
			// 一旦发现有 无参构造函数 则使用 按类型注入
			if (constructor.getParameterCount() == 0) {
				return "发现无参构造函数";
			}
		}
		return "没有无参构造函数";
	}

}
