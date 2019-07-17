

package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.util.StringUtils;

/**
 * Expression language AST node that represents a string literal.
 *
 * @author Andy Clement

 * @since 3.0
 */
public class StringLiteral extends Literal {

	private final TypedValue value;


	public StringLiteral(String payload, int pos, String value) {
		super(payload, pos);

		String valueWithinQuotes = value.substring(1, value.length() - 1);
		valueWithinQuotes = StringUtils.replace(valueWithinQuotes, "''", "'");
		valueWithinQuotes = StringUtils.replace(valueWithinQuotes, "\"\"", "\"");

		this.value = new TypedValue(valueWithinQuotes);
		this.exitTypeDescriptor = "Ljava/lang/String";
	}


	@Override
	public TypedValue getLiteralValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return "'" + getLiteralValue().getValue() + "'";
	}

	@Override
	public boolean isCompilable() {
		return true;
	}

	@Override
	public void generateCode(MethodVisitor mv, CodeFlow cf) {
		mv.visitLdcInsn(this.value.getValue());
		cf.pushDescriptor(this.exitTypeDescriptor);
	}

}
