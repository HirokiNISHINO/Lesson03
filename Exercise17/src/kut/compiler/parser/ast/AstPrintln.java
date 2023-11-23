package kut.compiler.parser.ast;

import java.util.Vector;

import kut.compiler.cgen.CodeGenerator;
import kut.compiler.cgen.type.Type;
import kut.compiler.error.SemanticErrorException;
import kut.compiler.tokenizer.Token;

/**
 * @author hnishino
 *
 */
public class AstPrintln extends AstNode
{
	Token token = null;
	private Vector<AstNode> parameters = null;
	
	/**
	 * 
	 */
	public AstPrintln(Token token) {
		this.token = token;
		this.parameters = new Vector<AstNode>();
	}
	
	/**
	 * @param param
	 */
	public void addParam(AstNode param) {
		parameters.add(param);
	}

	
	
	/**
	 *
	 */
	@Override
	public String getTreeString(int indent) {
		StringBuffer buf = new StringBuffer();
		buf.append(this.getIndentedStringWithCR(indent, "PrintLn:"));
		for (AstNode n: parameters) {
			buf.append(n.getTreeString(indent + 1));
		}
		return buf.toString();
	}
	
	@Override
	public void beforeCGEN(CodeGenerator gen) throws SemanticErrorException {
		for (AstNode n: parameters) {
			n.beforeCGEN(gen);
		}
	}


	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) {

		gen.appendCode("mov rax, 0");
		for (AstNode n: parameters) {
			gen.appendCode("push rax");			
			n.cgen(gen);
			if (Type.INT.equals(n.getType(gen))) {
				gen.appendCode("call " + gen.getPrintIntLabel());		
			}
			else if (Type.BOOLEAN.equals(n.getType(gen))) {
				gen.appendCode("call " + gen.getPrintBooleanLabel());
			}
			gen.appendCode("pop rbx");
			gen.appendCode("add rax, rbx");
		}		
		gen.appendCode("call " + gen.getPrintCRLabel());	
	}
	
	/**
	 *
	 */
	@Override
	public Type getType(CodeGenerator gen) {
		return Type.VOID;
	}
}
