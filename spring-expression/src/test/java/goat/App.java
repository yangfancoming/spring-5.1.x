package goat;

import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Created by Administrator on 2020/5/11.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/5/11---16:43
 * 给一个字符串最终解析成一个值，这中间至少得经历：
 * 字符串 -> 语法分析 -> 生成表达式对象 -> （添加执行上下文） -> 执行此表达式对象 -> 返回结果
 *
 * 关于SpEL的几个概念：
 *
 * 1.表达式（“干什么”）：SpEL的核心，所以表达式语言都是围绕表达式进行的
 * 2.解析器（“谁来干”）：用于将字符串表达式解析为表达式对象
 * 3.上下文（“在哪干”）：表达式对象执行的环境，该环境可能定义变量、定义自定义函数、提供类型转换等等
 * 4.root根对象及活动上下文对象（“对谁干”）：root根对象是默认的活动上下文对象，活动上下文对象表示了当前表达式操作的对象
 */
public class App {

	@Test
	public void test(){
		String expressionStr = "1 + 2";
		ExpressionParser parpser = new SpelExpressionParser(); //SpelExpressionParser是Spring内部对ExpressionParser的唯一最终实现类
		Expression exp = parpser.parseExpression(expressionStr); //把该表达式，解析成一个Expression对象：SpelExpression

		// 方式一：直接计算
		Object value = exp.getValue();
		System.out.println(value.toString()); //3
		// 若你在@Value中或者xml使用此表达式，请使用#{}包裹~~~~~~~~~~~~~~~~~
		System.out.println(parpser.parseExpression("T(System).getProperty('user.dir')").getValue()); //E:\work\remotegitcheckoutproject\myprojects\java\demo-war
		System.out.println(parpser.parseExpression("T(java.lang.Math).random() * 100.0").getValue()); //27.38227555400853

		// 方式二：定义环境变量，在环境内计算拿值
		// 环境变量可设置多个值：比如BeanFactoryResolver、PropertyAccessor、TypeLocator等~~~
		// 有环境变量，就有能力处理里面的占位符 ${}
		EvaluationContext context = new StandardEvaluationContext();
		System.out.println(exp.getValue(context)); //3
	}
}
