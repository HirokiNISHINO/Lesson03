package kut.compiler.parser.ast;

import kut.compiler.cgen.CodeGenerator;
import kut.compiler.cgen.label.CondBranchLabels;
import kut.compiler.cgen.type.Type;
import kut.compiler.error.SemanticErrorException;
import kut.compiler.tokenizer.Token;

/**
 * @author hnishino
 *
 */
public class AstIfElse extends AstNode
{
	private Token	token;
	private AstNode cond;
	private AstNode thenClause;
	private AstNode elseClause;
	
	
	/**
	 * 
	 */
	public AstIfElse(Token token, AstNode cond, AstNode thenClause, AstNode elseClause) {
		this.token 		= token;
		this.cond 		= cond;
		this.thenClause = thenClause;
		this.elseClause	= elseClause;
	}
	

	
	/**
	 *
	 */
	@Override
	public String getTreeString(int indent) {
		String r 	=  this.getIndentedStringWithCR(indent, "IfElse:" + this.token.getLexeme())
					+ cond.getTreeString(indent + 1) 
					+ thenClause.getTreeString(indent + 1);
		if (this.elseClause != null) {
			r += elseClause.getTreeString(indent + 1);
		}
		
		return r;
	}

	


	@Override
	public void beforeCGEN(CodeGenerator gen) throws SemanticErrorException {
		

		this.cond.beforeCGEN(gen);
		this.thenClause.beforeCGEN(gen);
		if (this.elseClause != null) {
			this.elseClause.beforeCGEN(gen);
		}
		
		Type ctype = cond.getType(gen);
		if (Type.BOOLEAN.equals(ctype)) {
			return;
		}
		
		throw new SemanticErrorException("The condition for a if/if-else statement must be boolean, but passed : '" + ctype.getTypeNameString() + ": " + token);
	}



	@Override
	public void cgen(CodeGenerator gen) {
		
		CondBranchLabels lbls = CondBranchLabels.getLabels();
		
		cond.cgen(gen);
		gen.appendCode("cmp rax, 0");
		gen.appendCode("je " + lbls.getFalseBranchLabel());
		
		gen.appendCode(lbls.getTrueBranchLabel() + ":", 0);
		thenClause.cgen(gen);
		gen.appendCode("jmp " + lbls.getEndCondLabel());
		
		gen.appendCode(lbls.getFalseBranchLabel() + ":", 0);
		if (elseClause != null) {
			elseClause.cgen(gen);
		}
		gen.appendCode(lbls.getEndCondLabel() + ":", 0);
		
		
		return;
	}
	
	
	
	/**
	 *
	 */
	@Override
	public Type getType(CodeGenerator gen) {
		return Type.VOID;
	}
	
}
