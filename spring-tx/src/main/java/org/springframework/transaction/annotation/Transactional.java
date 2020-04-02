

package org.springframework.transaction.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.TransactionDefinition;

/**
 * Describes a transaction attribute on an individual method or on a class.
 *
 * At the class level, this annotation applies as a default to all methods of
 * the declaring class and its subclasses. Note that it does not apply to ancestor
 * classes up the class hierarchy; methods need to be locally redeclared in order
 * to participate in a subclass-level annotation.
 *
 * This annotation type is generally directly comparable to Spring's
 * {@link org.springframework.transaction.interceptor.RuleBasedTransactionAttribute}
 * class, and in fact {@link AnnotationTransactionAttributeSource} will directly
 * convert the data to the latter class, so that Spring's transaction support code
 * does not have to know about annotations. If no rules are relevant to the exception, it will be treated like
 *
 * {@link org.springframework.transaction.interceptor.DefaultTransactionAttribute}
 * (rolling back on {@link RuntimeException} and {@link Error} but not on checked exceptions).
 *
 * For specific information about the semantics of this annotation's attributes,
 * consult the {@link org.springframework.transaction.TransactionDefinition} and
 * {@link org.springframework.transaction.interceptor.TransactionAttribute} javadocs.

 * @since 1.2
 * @see org.springframework.transaction.interceptor.TransactionAttribute
 * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute
 * @see org.springframework.transaction.interceptor.RuleBasedTransactionAttribute
 */
// @Transactional注解可以加在类或方法上，加在类上时是对该类的所有public方法开启事务。加在方法上时也是只对public方法起作用。
// 另外@Transactional注解也可以加在接口上，但只有在设置了基于接口的代理时才会生效，因为注解不能继承。所以该注解最好是加在类的实现上。
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Transactional {

	/**
	 * Alias for {@link #transactionManager}.
	 * @see #transactionManager
	 */
	@AliasFor("transactionManager")
	String value() default "";

	/**
	 * A <em>qualifier</em> value for the specified transaction.
	 * May be used to determine the target transaction manager,  matching the qualifier value (or the bean name) of a specific
	 * {@link org.springframework.transaction.PlatformTransactionManager} bean definition.
	 * @since 4.2
	 * @see #value
	 */
	@AliasFor("value")
	String transactionManager() default "";

	/**
	 * The transaction propagation type.
	 * Defaults to {@link Propagation#REQUIRED}.
	 * @see org.springframework.transaction.interceptor.TransactionAttribute#getPropagationBehavior()
	 */
	Propagation propagation() default Propagation.REQUIRED;

	/**
	 * isolation属性是用来设置事务的隔离级别，数据库有四种隔离级别：读未提交、读已提交、可重复读、可串行化。MySQL的默认隔离级别是可重复读。
	 * The transaction isolation level. Defaults to {@link Isolation#DEFAULT}.
	 * Exclusively designed for use with {@link Propagation#REQUIRED} or
	 * {@link Propagation#REQUIRES_NEW} since it only applies to newly started
	 * transactions. Consider switching the "validateExistingTransactions" flag to
	 * "true" on your transaction manager if you'd like isolation level declarations
	 * to get rejected when participating in an existing transaction with a different isolation level.
	 * @see org.springframework.transaction.interceptor.TransactionAttribute#getIsolationLevel()
	 * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setValidateExistingTransaction
	 */
	Isolation isolation() default Isolation.DEFAULT;

	/**
	 * The timeout for this transaction (in seconds). timtout是用来设置事务的超时时间，可以看到默认为-1，不会超时
	 * Defaults to the default timeout of the underlying transaction system.
	 * Exclusively designed for use with {@link Propagation#REQUIRED} or
	 * {@link Propagation#REQUIRES_NEW} since it only applies to newly started transactions.
	 * @see org.springframework.transaction.interceptor.TransactionAttribute#getTimeout()
	 */
	int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;

	/**
	 * A boolean flag that can be set to {@code true} if the transaction is
	 * effectively read-only, allowing for corresponding optimizations at runtime.
	 * Defaults to {@code false}.
	 * This just serves as a hint for the actual transaction subsystem;
	 * it will <i>not necessarily</i> cause failure of write access attempts.
	 * A transaction manager which cannot interpret the read-only hint will
	 * <i>not</i> throw an exception when asked for a read-only transaction
	 * but rather silently ignore the hint.
	 * @see org.springframework.transaction.interceptor.TransactionAttribute#isReadOnly()
	 * @see org.springframework.transaction.support.TransactionSynchronizationManager#isCurrentTransactionReadOnly()
	 */
	boolean readOnly() default false;

	/**
	 * 用来判断在什么异常下会进行回滚的，当方法内抛出指定的异常时，进行事务回滚
	 * Defines zero (0) or more exception {@link Class classes}, which must be subclasses of {@link Throwable},
	 * indicating which exception types must cause a transaction rollback.
	 * By default, a transaction will be rolling back on {@link RuntimeException}
	 * and {@link Error} but not on checked exceptions (business exceptions). See
	 * {@link org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)}  for a detailed explanation.
	 * This is the preferred way to construct a rollback rule (in contrast to
	 * {@link #rollbackForClassName}), matching the exception class and its subclasses.
	 * Similar to {@link org.springframework.transaction.interceptor.RollbackRuleAttribute#RollbackRuleAttribute(Class clazz)}.
	 * @see #rollbackForClassName
	 * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)
	 */
	Class<? extends Throwable>[] rollbackFor() default {};

	/**
	 * Defines zero (0) or more exception names (for exceptions which must be a
	 * subclass of {@link Throwable}), indicating which exception types must cause  a transaction rollback.
	 * This can be a substring of a fully qualified class name, with no wildcard
	 * support at present. For example, a value of {@code "ServletException"} would
	 * match {@code javax.servlet.ServletException} and its subclasses.
	 * <b>NB:</b> Consider carefully how specific the pattern is and whether
	 * to include package information (which isn't mandatory). For example,
	 * {@code "Exception"} will match nearly anything and will probably hide other
	 * rules. {@code "java.lang.Exception"} would be correct if {@code "Exception"}
	 * were meant to define a rule for all checked exceptions. With more unusual
	 * {@link Exception} names such as {@code "BaseBusinessException"} there is no
	 * need to use a FQN.
	 * Similar to {@link org.springframework.transaction.interceptor.RollbackRuleAttribute#RollbackRuleAttribute(String exceptionName)}.
	 * @see #rollbackFor
	 * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)
	 */
	String[] rollbackForClassName() default {};

	/**
	 * 这个和上面正好相反，用来设置出现指定的异常时，不进行回滚。
	 * Defines zero (0) or more exception {@link Class Classes}, which must be
	 * subclasses of {@link Throwable}, indicating which exception types must
	 * <b>not</b> cause a transaction rollback.
	 * This is the preferred way to construct a rollback rule (in contrast
	 * to {@link #noRollbackForClassName}), matching the exception class and its subclasses.
	 * Similar to {@link org.springframework.transaction.interceptor.NoRollbackRuleAttribute#NoRollbackRuleAttribute(Class clazz)}.
	 * @see #noRollbackForClassName
	 * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)
	 */
	Class<? extends Throwable>[] noRollbackFor() default {};

	/**
	 * Defines zero (0) or more exception names (for exceptions which must be a
	 * subclass of {@link Throwable}) indicating which exception types must <b>not</b> cause a transaction rollback.
	 * See the description of {@link #rollbackForClassName} for further
	 * information on how the specified names are treated.
	 * Similar to {@link org.springframework.transaction.interceptor.NoRollbackRuleAttribute#NoRollbackRuleAttribute(String exceptionName)}.
	 * @see #noRollbackFor
	 * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)
	 */
	String[] noRollbackForClassName() default {};

}
