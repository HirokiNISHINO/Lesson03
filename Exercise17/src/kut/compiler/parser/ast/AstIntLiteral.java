package kut.compiler.parser.ast;

import kut.compiler.cgen.CodeGenerator;
import kut.compiler.cgen.type.Type;
import kut.compiler.tokenizer.Token;

/**
 * @author hnishino
 *
 */
public class AstIntLiteral extends AstNode
{
	private Token t;
	
	/**
	 * 
	 */
	public AstIntLiteral(Token t) {
		this.t = t;
	}
	
	/**
	 * @return
	 */
	public int getIntValue() {
		return Integer.parseInt(t.getLexeme());
	}
	
	
	/**
	 *
	 */
	@Override
	public String getTreeString(int indent) {
		return this.getIndentedStringWithCR(indent, "IntLiteral:" + this.getIntValue());
	}

	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) {
		gen.appendCode("mov rax, " + this.getIntValue());
	}
	
	/**
	 *
	 */
	@Override
	public Type getType(CodeGenerator gen) {
		return Type.INT;
	}
	

}
