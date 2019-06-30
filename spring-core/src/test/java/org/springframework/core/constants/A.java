package org.springframework.core.constants;

/**
 * Created by 64274 on 2019/6/30.
 *
 * @ Description:  常量必须是 public static final 修饰的，否则使用asXX方法取出的时候抛exception
 * @ Description:  常量命名 必须是大写  否则 使用 Constants 遍历时 将无效  不会对小写属性进行遍历
 * @ author  山羊来了
 * @ date 2019/6/30---16:38
 */
public class A {

	public static final int DOG = 0;
	public static final int CAT = 66;
	public static final String S1 = "";

	public static final int PREFIX_NO = 1;
	public static final int PREFIX_YES = 2;

	public static final int MY_PROPERTY_NO = 1;
	public static final int MY_PROPERTY_YES = 2;

	public static final int NO_PROPERTY = 3;
	public static final int YES_PROPERTY = 4;

	/** ignore these */
	protected static final int P = -1;
	protected boolean f;
	static final Object o = new Object();
}
