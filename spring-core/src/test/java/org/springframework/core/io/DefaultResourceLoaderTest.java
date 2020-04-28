package org.springframework.core.io;

import org.junit.Test;

/**
 * Created by Administrator on 2020/4/28.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/28---17:24
 */
public class DefaultResourceLoaderTest {

	@Test
	public void test(){
		DefaultResourceLoader bf = new DefaultResourceLoader();
		bf.getProtocolResolvers().add(new MyProtocolResolver());
		try {
			Resource resource = bf.getResource("classpath:log4j2-test.xml");
			System.out.println(resource.getURI().toURL());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
