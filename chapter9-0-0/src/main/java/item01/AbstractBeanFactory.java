

package item01;


public class AbstractBeanFactory extends SimpleAliasRegistry implements BeanFactory {

	@Override
	public String[] getAliases(String name) {
		return new String[]{"3","4"};
	}

	public String[] test(String name) {
		return super.getAliases(name);
	}

}
