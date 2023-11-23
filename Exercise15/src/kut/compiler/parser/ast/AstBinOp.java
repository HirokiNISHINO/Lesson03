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
public class AstBinOp extends AstNode
{
	private Token	token;
	private AstNode lhs;
	private AstNode rhs;
	
	
	/**
	 * 
	 */
	public AstBinOp(Token token, AstNode lhs, AstNode rhs) {
		this.token 	= token;
		this.lhs 	= lhs;
		this.rhs 	= rhs;
	}
	

	
	/**
	 *
	 */
	@Override
	public String getTreeString(int indent) {
		String r 	=  this.getIndentedStringWithCR(indent, "AstBinOp:" + this.token.getLexeme())
					+ lhs.getTreeString(indent + 1) 
					+ rhs.getTreeString(indent + 1);
		
		return r;
	}

	


	@Override
	public void beforeCGEN(CodeGenerator gen) throws SemanticErrorException {
		this.lhs.beforeCGEN(gen);
		this.rhs.beforeCGEN(gen);
		
		Type ltype = lhs.getType(gen);
		Type rtype = rhs.getType(gen);
		
		
		switch(token.getKlazz()) {
		case '+':
		case '-':
		case '*':
		case '/':
		case '%':
		case '<':
		case '>':
		case Token.LESS_THAN_OR_EQUAL_TO:
		case Token.GREATER_THAN_OR_EQUAL_TO:		
			if (Type.INT.equals(ltype) && Type.INT.equals(rtype)) {
				return;
			}
			break;
		
		case Token.EQUAL_TO:
		case Token.NOT_EQUAL_TO:
			if ((Type.BOOLEAN.equals(ltype) && Type.BOOLEAN.equals(rtype)) ||
					(Type.INT.equals(ltype)		&& Type.INT.equals(rtype))) {	
				return;
			}
			break;
			
		case Token.LOR:
		case Token.LAND:
			if (Type.BOOLEAN.equals(ltype) && Type.BOOLEAN.equals(rtype)) {	
				return;
			}
	
			break;
		}
		
		throw new SemanticErrorException("Invalid Operation. The operation: '" + token.getLexeme() + "' cannot be applied to the types: " + ltype.getTypeNameString() + " and " + rtype.getTypeNameString() + ": " + token);
	}



	@Override
	public void cgen(CodeGenerator gen) {
		
		switch(token.getKlazz()) {
		case '+':
		case '-':
		case '*':
		case '/':
		case '%':
			cgenArithmeticExpr(gen);
			break;
			
		case '<':
		case '>':
		case Token.LESS_THAN_OR_EQUAL_TO:
		case Token.GREATER_THAN_OR_EQUAL_TO:
			cgenRelExpr(gen);
			break;
			
		case Token.EQUAL_TO:
		case Token.NOT_EQUAL_TO:
			cgenEquExpr(gen);
			break;
		
		default:
			break;
		}
		
		return;
	}
	
	
	/**
	 * @param gen
	 */
	public void cgenLorLand(CodeGenerator gen) {
		
		CondBranchLabels lbls = CondBranchLabels.getLabels();
		
		// ||
		if (token.getKlazz() == Token.LOR) {
			lhs.cgen(gen);
			gen.appendCode("cmp rax, 0");
			gen.appendCode("jne " + lbls.getTrueBranchLabel());
			
			rhs.cgen(gen);
			gen.appendCode("cmp rax, 0");
			gen.appendCode("jne " + lbls.getTrueBranchLabel());
			
			gen.appendCode(lbls.getFalseBranchLabel() + ":", 0);
			gen.appendCode("mov rax, 0");
			gen.appendCode("jmp " + lbls.getEndCondLabel());
			
			gen.appendCode(lbls.getTrueBranchLabel() + ":", 0);
			gen.appendCode("mov rax, 1");
			gen.appendCode(lbls.getEndCondLabel() + ":", 0);
			
			return;
		}
		
		// &&
		if (token.getKlazz() == Token.LAND) {
			lhs.cgen(gen);
			gen.appendCode("cmp rax, 0");
			gen.appendCode("je " + lbls.getFalseBranchLabel());
			
			rhs.cgen(gen);
			gen.appendCode("cmp rax, 0");
			gen.appendCode("je " + lbls.getFalseBranchLabel());
			
			gen.appendCode(lbls.getTrueBranchLabel() + ":", 0);
			gen.appendCode("mov rax, 1");
			gen.appendCode("jmp " + lbls.getEndCondLabel());

			gen.appendCode(lbls.getFalseBranchLabel() + ":", 0);
			gen.appendCode("mov rax, 0");
			gen.appendCode(lbls.getEndCondLabel() + ":", 0);	
			
			return;
		}
		
	
		throw new RuntimeException("a bug. the code shouldn't reach here.");
	}
	
	/**
	 * @param gen
	 */
	public void cgenArithmeticExpr(CodeGenerator gen) {
		switch(token.getKlazz()) {
		case '+':
			this.lhs.cgen(gen);
			gen.appendCode("push rax");
			this.rhs.cgen(gen);
			gen.appendCode("pop rbx");
			gen.appendCode("add rax, rbx");
			break;

		case '-':
			this.lhs.cgen(gen);
			gen.appendCode("push rax");
			this.rhs.cgen(gen);
			gen.appendCode("mov rbx, rax");
			gen.appendCode("pop rax");
			gen.appendCode("sub rax, rbx");
			break;

		case '*':
			this.lhs.cgen(gen);
			gen.appendCode("push rax");
			this.rhs.cgen(gen);
			gen.appendCode("pop rbx");
			gen.appendCode("imul rax, rbx");
			break;

		case '/':
			this.lhs.cgen(gen);
			gen.appendCode("push rax");
			this.rhs.cgen(gen);
			gen.appendCode("mov rbx, rax");
			gen.appendCode("pop rax");
			gen.appendCode("mov rdx, 0");
			gen.appendCode("idiv rbx");
			break;
		}
		return;
	}
	
	
	/**
	 * @param gen
	 */
	public void cgenEquExpr(CodeGenerator gen) {
		
		CondBranchLabels lbls = CondBranchLabels.getLabels();

		this.lhs.cgen(gen);
		gen.appendCode("push rax");
		this.rhs.cgen(gen);
		gen.appendCode("mov rbx, rax");
		gen.appendCode("pop rax");
		gen.appendCode("cmp rax, rbx");

		switch(token.getKlazz()){
		case Token.EQUAL_TO:
			gen.appendCode("je " + lbls.getTrueBranchLabel());			
			break;

		case Token.NOT_EQUAL_TO:
			gen.appendCode("jne " + lbls.getTrueBranchLabel());			
			break;

		}

		gen.appendCode(lbls.getFalseBranchLabel() + ":", 0);
		gen.appendCode("mov rax, 0");
		gen.appendCode("jmp " + lbls.getEndCondLabel());
		
		gen.appendCode(lbls.getTrueBranchLabel() + ":", 0);
		gen.appendCode("mov rax, 1");
		
		gen.appendCode(lbls.getEndCondLabel() + ":", 0);
		
		return;
	}
	
	/**
	 * @param gen
	 */
	public void cgenRelExpr(CodeGenerator gen) {
		
		CondBranchLabels lbls = CondBranchLabels.getLabels();

		this.lhs.cgen(gen);
		gen.appendCode("push rax");
		this.rhs.cgen(gen);
		gen.appendCode("mov rbx, rax");
		gen.appendCode("pop rax");
		gen.appendCode("cmp rax, rbx");

		switch(token.getKlazz()){
		case '<':
			gen.appendCode("jl " + lbls.getTrueBranchLabel());			
			break;

		case Token.LESS_THAN_OR_EQUAL_TO:
			gen.appendCode("jle " + lbls.getTrueBranchLabel());			
			break;

		case '>':
			gen.appendCode("jg " + lbls.getTrueBranchLabel());			
			break;

		case Token.GREATER_THAN_OR_EQUAL_TO:
			gen.appendCode("jge " + lbls.getTrueBranchLabel());			
			break;

		}

		gen.appendCode(lbls.getFalseBranchLabel() + ":", 0);
		gen.appendCode("mov rax, 0");
		gen.appendCode("jmp " + lbls.getEndCondLabel());
		
		gen.appendCode(lbls.getTrueBranchLabel() + ":", 0);
		gen.appendCode("mov rax, 1");
		
		gen.appendCode(lbls.getEndCondLabel() + ":", 0);

		return;
	}
	
	/**
	 *
	 */
	@Override
	public Type getType(CodeGenerator gen) {
		
		switch(token.getKlazz()) {
		case '+':
		case '-':
		case '*':
		case '/':
		case '%':
			return Type.INT;
			
		case '<':
		case '>':
		case Token.LESS_THAN_OR_EQUAL_TO:
		case Token.GREATER_THAN_OR_EQUAL_TO:
		case Token.EQUAL_TO:
		case Token.NOT_EQUAL_TO:
			return Type.BOOLEAN;
			
		case Token.LOR:
		case Token.LAND:
			return Type.BOOLEAN;
			
		default:
			break;
		}
		
		throw new RuntimeException("a bug. the code shouldn't reach here.");
	}
	
}
