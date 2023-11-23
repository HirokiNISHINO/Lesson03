package kut.compiler.parser.ast;

import kut.compiler.cgen.CodeGenerator;
import kut.compiler.cgen.type.Type;
import kut.compiler.error.SemanticErrorException;

/**
 * @author hnishino
 *
 */
public class AstNode 
{

	/**
	 * 
	 */
	public AstNode() {
		//do nothing here.
	}
	
	
	
	/**
	 * @param indent 
	 * @return
	 */
	public String getTreeString(int indent){
		throw new RuntimeException("not implemented yet! : " + this.getClass().getCanonicalName());
	}
	
	/**
	 * @param gen
	 */
	public void cgen(CodeGenerator gen) {
		throw new RuntimeException("not implemented yet! : " + this.getClass().getCanonicalName());
	}
	
	
	/**
	 * @param indent
	 * @param s
	 * @return
	 */
	public String getIndentedStringWithCR(int indent, String s) {
		
		for (int i = 0; i < indent; i++) {
			s = "  " + s;
		}
				
		s += "\n";
		return s;
	}
	
	/**
	 * 
	 */
	public Type getType(CodeGenerator gen) {
		throw new RuntimeException("not implemented yet! : " + this.getClass().getCanonicalName());
	}
	
	/**
	 * @param gen
	 */
	public void beforeCGEN(CodeGenerator gen) throws SemanticErrorException {
		//do nothing here.
	}
	

}
