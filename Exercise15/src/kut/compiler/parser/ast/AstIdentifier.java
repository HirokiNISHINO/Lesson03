package kut.compiler.parser.ast;

import kut.compiler.cgen.CodeGenerator;
import kut.compiler.cgen.symboltable.TypeOfId;
import kut.compiler.cgen.type.Type;
import kut.compiler.error.SemanticErrorException;
import kut.compiler.tokenizer.Token;

/**
 * @author hnishino
 *
 */
public class AstIdentifier extends AstNode
{
	private Token t;
	
	/**
	 * 
	 */
	public AstIdentifier(Token t) {
		this.t = t;
	}
	
	/**
	 * @return
	 */
	public String getIdentifier() {
		return t.getLexeme();
	}
	
	
	/**
	 *
	 */
	@Override
	public String getTreeString(int indent) {
		return this.getIndentedStringWithCR(indent, "Identifier:" + this.getIdentifier());
	}

	/**
	 * @param gen
	 */
	public void beforeCGEN(CodeGenerator gen) throws SemanticErrorException {
		TypeOfId ti = gen.getTypeOfId(getIdentifier());
		if (ti != TypeOfId.GlobalVariable) {
			throw new SemanticErrorException("an undefined identifier found: " + t);
		}
	}
	
	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) {
		String label = gen.getGlobalVariableMemoryAddressLabel(getIdentifier());
		
		gen.appendCode("mov rax, [ rel " + label + "]");
	}
	/**
	 *
	 */
	@Override
	public Type getType(CodeGenerator gen) {
		TypeOfId tid = gen.getTypeOfId(getIdentifier());
		if (tid != TypeOfId.GlobalVariable) {
			throw new RuntimeException("a bug. the code shouldn't reach here.");
		}
		
		return gen.getTypeOfGlobalVariable(getIdentifier());
	}

	
	

}
