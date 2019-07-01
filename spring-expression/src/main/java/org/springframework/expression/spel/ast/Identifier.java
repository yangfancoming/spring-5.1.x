

package org.springframework.expression.spel.ast;

import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelNode;

/**
 * An 'identifier' {@link SpelNode}.
 *
 * @author Andy Clement
 * @since 3.0
 */
public class Identifier extends SpelNodeImpl {

	private final TypedValue id;


	public Identifier(String payload, int pos) {
		super(pos);
		this.id = new TypedValue(payload);
	}


	@Override
	public String toStringAST() {
		return String.valueOf(this.id.getValue());
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) {
		return this.id;
	}

}
