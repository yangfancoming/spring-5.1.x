

package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;

/**
 * Expression language AST node that represents a long integer literal.
 *
 * @author Andy Clement
 * @since 3.0
 */
public class LongLiteral extends Literal {

	private final TypedValue value;


	public LongLiteral(String payload, int pos, long value) {
		super(payload, pos);
		this.value = new TypedValue(value);
		this.exitTypeDescriptor = "J";
	}


	@Override
	public TypedValue getLiteralValue() {
		return this.value;
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
