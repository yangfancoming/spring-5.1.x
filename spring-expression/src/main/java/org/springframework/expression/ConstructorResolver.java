

package org.springframework.expression;

import java.util.List;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

/**
 * A constructor resolver attempts locate a constructor and returns a ConstructorExecutor
 * that can be used to invoke that constructor. The ConstructorExecutor will be cached but
 * if it 'goes stale' the resolvers will be called again.
 *
 * @author Andy Clement
 * @since 3.0
 */
@FunctionalInterface
public interface ConstructorResolver {

	/**
	 * Within the supplied context determine a suitable constructor on the supplied type
	 * that can handle the specified arguments. Return a ConstructorExecutor that can be
	 * used to invoke that constructor (or {@code null} if no constructor could be found).
	 * @param context the current evaluation context
	 * @param typeName the type upon which to look for the constructor
	 * @param argumentTypes the arguments that the constructor must be able to handle
	 * @return a ConstructorExecutor that can invoke the constructor, or null if non found
	 */
	@Nullable
	ConstructorExecutor resolve(EvaluationContext context, String typeName, List<TypeDescriptor> argumentTypes)
			throws AccessException;

}
