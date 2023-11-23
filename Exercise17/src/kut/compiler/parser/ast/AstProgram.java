package kut.compiler.parser.ast;

import java.util.Vector;

import kut.compiler.cgen.CodeGenerator;
import kut.compiler.cgen.type.Type;
import kut.compiler.error.SemanticErrorException;

/**
 * @author hnishino
 *
 */
public class AstProgram extends AstNode
{
	private Vector<AstNode> statements = null;
	
	/**
	 * 
	 */
	public AstProgram() {
		this.statements = new Vector<AstNode>();
	}
	
	/**
	 * @param param
	 */
	public void addStatement(AstNode statement) {
		statements.add(statement);
	}

	
	/**
	 *
	 */
	@Override
	public String getTreeString(int indent) {
		StringBuffer buf = new StringBuffer();
		buf.append(this.getIndentedStringWithCR(indent, "Program:"));
		for (AstNode n: statements) {
			buf.append(n.getTreeString(indent + 1));
		}
		return buf.toString();
	}

	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) {
		for (AstNode n: statements) {
			n.cgen(gen);
		}
	}
	
	/**
	 * @param gen
	 */
	public void beforeCGEN(CodeGenerator gen) throws SemanticErrorException {
		for (AstNode n: statements) {
			n.beforeCGEN(gen);
		}
	}
	
	/**
	 *
	 */
	@Override
	public Type getType(CodeGenerator gen) {
		return Type.INT;
	}
	
}
