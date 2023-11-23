package kut.compiler.parser.ast;

import kut.compiler.cgen.CodeGenerator;
import kut.compiler.cgen.label.WhileLoopLabels;
import kut.compiler.cgen.type.Type;
import kut.compiler.error.SemanticErrorException;
import kut.compiler.tokenizer.Token;

/**
 * @author hnishino
 *
 */
public class AstWhile extends AstNode
{
	private Token	token;
	private AstNode cond;
	private AstNode loopBody;
	
	
	/**
	 * 
	 */
	public AstWhile(Token token, AstNode cond, AstNode loopBody) {
		this.token 		= token;
		this.cond 		= cond;
		this.loopBody	= loopBody;
	}
	

	
	/**
	 *
	 */
	@Override
	public String getTreeString(int indent) {
		String r 	=  this.getIndentedStringWithCR(indent, "While:" + this.token.getLexeme())
					+ cond.getTreeString(indent + 1) 
					+ loopBody.getTreeString(indent + 1);
		
		return r;
	}

	


	@Override
	public void beforeCGEN(CodeGenerator gen) throws SemanticErrorException 
	{
		//TODO: AstIfElseを参考に意味解析を行え。
		throw new SemanticErrorException("The condition for a while statement must be boolean, but passed : '" + ctype.getTypeNameString() + ": " + token);
	}



	@Override
	public void cgen(CodeGenerator gen) {
		
		WhileLoopLabels lbls = WhileLoopLabels.getLabels();
		//TODO: lbls.getEntryLabel() と　lbls.getExitLabel()を使用して
		//TODO: コード生成せよ。
		
		
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
