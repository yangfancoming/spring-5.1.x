

package org.springframework.expression;


// TODO Is the resolver/executor model too pervasive in this package?
/**
 * Executors are built by resolvers and can be cached by the infrastructure to repeat an
 * operation quickly without going back to the resolvers. For example, the particular
 * constructor to run on a class may be discovered by the reflection constructor resolver
 * - it will then build a ConstructorExecutor that executes that constructor and the
 * ConstructorExecutor can be reused without needing to go back to the resolver to
 * discover the constructor again.
 *
 * <p>They can become stale, and in that case should throw an AccessException - this will
 * cause the infrastructure to go back to the resolvers to ask for a new one.
 *
 * @author Andy Clement
 * @since 3.0
 */
public interface ConstructorExecutor {

	/**
	 * Execute a constructor in the specified context using the specified arguments.
	 * @param context the evaluation context in which the command is being executed
	 * @param arguments the arguments to the constructor call, should match (in terms
	 * of number and type) whatever the command will need to run
	 * @return the new object
	 * @throws AccessException if there is a problem executing the command or the
	 * CommandExecutor is no longer valid
	 */
	TypedValue execute(EvaluationContext context, Object... arguments) throws AccessException;

}