package org.springframework.core.io;

/**
 * Created by Administrator on 2020/4/28.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/28---17:26
 */
public class MyProtocolResolver implements ProtocolResolver {

	private static String CLASS_PATH_PRE="classpath:";

	@Override
	public Resource resolve(String location, ResourceLoader resourceLoader) {
		if(location.startsWith(CLASS_PATH_PRE)) {
			return new ClassPathResource(location.substring(CLASS_PATH_PRE.length()));
		}
		return null;
	}
}
