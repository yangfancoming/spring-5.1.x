package item01;

/**
 * Created by Administrator on 2020/5/8.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/5/8---17:38
 */
public class SimpleAliasRegistry implements AliasRegistry {

	@Override
	public String[] getAliases(String name) {
		return new String[]{"1","2"};
	}
}
