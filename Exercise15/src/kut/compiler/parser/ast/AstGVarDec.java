package kut.compiler.parser.ast;

import kut.compiler.cgen.CodeGenerator;
import kut.compiler.cgen.type.Type;
import kut.compiler.error.SemanticErrorException;
import kut.compiler.tokenizer.Token;

/**
 * @author hnishino
 *
 */
public class AstGVarDec extends AstNode
{
	@SuppressWarnings("unused")
	private Token token	;
	private Token type	;
	private Token id	;
	
	/**
	 * 
	 */
	public AstGVarDec(Token token, Token type, Token id) {
		this.token  = token;
		this.type 	= type;
		this.id 	= id;
	}
	
	
	/**
	 * @return
	 */
	public String getId() {
		return this.id.getLexeme();
	}
	
	/**
	 *
	 */
	@Override
	public Type getType(CodeGenerator gen) {
		return Type.VOID;
	}

	/**
	 *
	 */
	@Override
	public String getTreeString(int indent) {
		return this.getIndentedStringWithCR(indent, "GVarDec: " + this.getId() + "(" + type.getLexeme() +")");
	}
	
	/**
	 * @param gen
	 */
	public void beforeCGEN(CodeGenerator gen) throws SemanticErrorException {
		Type t = Type.convertTypeNameToType(type.getLexeme());
		gen.declareGlobalVariable(getId(), t);
	}
	
	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) {
		//do nothing here.
	}
		
}
