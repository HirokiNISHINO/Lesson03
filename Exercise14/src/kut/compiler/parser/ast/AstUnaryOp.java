package kut.compiler.parser.ast;

import kut.compiler.cgen.CodeGenerator;
import kut.compiler.cgen.type.Type;
import kut.compiler.error.SemanticErrorException;
import kut.compiler.tokenizer.Token;

/**
 * @author hnishino
 *
 */
public class AstUnaryOp extends AstNode
{
	private Token	token	;
	private AstNode expr	;
	
	
	/**
	 * 
	 */
	public AstUnaryOp(Token token, AstNode expr) {
		this.token 	= token;
		this.expr 	= expr;
	}
	

	
	/**
	 *
	 */
	@Override
	public String getTreeString(int indent) {
		String r 	=  this.getIndentedStringWithCR(indent, "AstUnaryOp:" + this.token.getLexeme())
					+ expr.getTreeString(indent + 1);
		
		return r;
	}

	


	@Override
	public void beforeCGEN(CodeGenerator gen) throws SemanticErrorException {
		this.expr.beforeCGEN(gen);
		
		Type etype = expr.getType(gen);
		
		
		switch(token.getKlazz()) {
		case '-':
			if (Type.INT.equals(etype)) {
				return;
			}
			break;
		
		case '!':
			if (Type.BOOLEAN.equals(etype)) {	
				return;
			}
			break;
		}
		
		throw new SemanticErrorException("Invalid Operation. the type:" + etype.getTypeNameString() + " can not be performed the operation: '" + token.getLexeme());
	}



	@Override
	public void cgen(CodeGenerator gen) {
		
		switch(token.getKlazz()) {
		case '-':
			expr.cgen(gen);
			gen.appendCode("neg rax");
			break;
			

		case '!':
			expr.cgen(gen);
			//boolean in this language is always 0 or 1. so this is okay.
			gen.appendCode("xor rax, 0x0000000000000001");
			break;
			
		default:
			break;
		}
		
		return;
	}
	

	
	
	
	
	/**
	 *
	 */
	@Override
	public Type getType(CodeGenerator gen) {
		
		switch(token.getKlazz()) {
		case '-':
			return Type.INT;
			
		case '!':
			return Type.BOOLEAN;
			
		default:
			break;
		}
		
		throw new RuntimeException("a bug. the code shouldn't reach here.");
	}
	
}
