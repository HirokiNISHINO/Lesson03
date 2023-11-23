package kut.compiler.parser.ast;

import kut.compiler.cgen.CodeGenerator;
import kut.compiler.cgen.type.Type;
import kut.compiler.tokenizer.Token;

/**
 * @author hnishino
 *
 */
public class AstBooleanLiteral extends AstNode
{
	private Token t;
	
	/**
	 * 
	 */
	public AstBooleanLiteral(Token t) {
		this.t = t;
	}
	
	/**
	 * @return
	 */
	public boolean getBooleanValue() {
		if ("true".equals(t.getLexeme())) {
			return true;
		}
		return false;
	}
	
	
	/**
	 *
	 */
	@Override
	public String getTreeString(int indent) {
		return this.getIndentedStringWithCR(indent, "BooleanLiteral:" + t.getLexeme());
	}

	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) {
		gen.appendCode("mov rax, " + (this.getBooleanValue() ? "1" : "0"));	
	}
	
	/**
	 *
	 */
	@Override
	public Type getType(CodeGenerator gen) {
		return Type.BOOLEAN;
	}
	

}
