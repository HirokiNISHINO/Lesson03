package kut.compiler.parser.ast;

import kut.compiler.cgen.CodeGenerator;
import kut.compiler.cgen.type.Type;
import kut.compiler.error.SemanticErrorException;
import kut.compiler.tokenizer.Token;

/**
 * @author hnishino
 *
 */
public class AstAssignment extends AstNode
{
	private Token	t;
	private AstNode lhs;
	private AstNode rhs;
	
	/**
	 * 
	 */
	public AstAssignment(Token t, AstNode lhs, AstNode rhs) {
		this.t = t;
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	
	/**
	 *
	 */
	@Override
	public String getTreeString(int indent) {
		String node = this.getIndentedStringWithCR(indent, "Assignment:");
		String lhs  = this.lhs.getTreeString(indent + 1);
		String rhs  = this.rhs.getTreeString(indent + 1);
		return node + lhs + rhs;
	}

	/**
	 * @param gen
	 */
	public void beforeCGEN(CodeGenerator gen) throws SemanticErrorException {
		
		if (lhs instanceof AstIdentifier == false) {
			throw new SemanticErrorException("The lefthand-side of an assignment expression must be an identifier: " + t);
		}
		
		this.rhs.beforeCGEN(gen);
		this.lhs.beforeCGEN(gen);
		
		
		Type lt = this.lhs.getType(gen);
		Type rt = this.rhs.getType(gen);
		
		if (lt.equals(rt)) {
			return;
		}
		throw new SemanticErrorException("The type of the right-hand side:" + rt.getTypeNameString() + " does not match the type of left-hand side: " + lt.getTypeNameString() + ". :"+ t);
	}
	
	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) {
		
		this.rhs.cgen(gen);

		AstIdentifier id = (AstIdentifier)lhs;
		
		String label = gen.getGlobalVariableMemoryAddressLabel(id.getIdentifier());
		gen.appendCode("mov [ rel " + label + "], rax");
	}


	/**
	 *
	 */
	@Override
	public Type getType(CodeGenerator gen) {
		return lhs.getType(gen);
	}


	
	
}
