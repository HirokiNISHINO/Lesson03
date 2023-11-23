package kut.compiler.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;

import org.junit.jupiter.api.Test;

import kut.compiler.cgen.CodeGenerator;
import kut.compiler.parser.Parser;
import kut.compiler.parser.ast.AstNode;
import kut.compiler.test.util.CheckOutput;
import kut.compiler.tokenizer.Tokenizer;

/**
 * @author hnishino
 *
 */
class TestCases {

	@Test
	void parser1700() throws Exception {
		String testname = Thread.currentThread().getStackTrace()[1].getMethodName();
		testParser(testname, "testcode1700");
	}
	
	@Test
	void cgen1700() throws Exception {
		String testname = Thread.currentThread().getStackTrace()[1].getMethodName();
		testCompiler(testname, "testcode1700");
	}


	/**
	 * @param testname
	 * @param codeFilename
	 * @throws Exception 
	 */
	void testParser(String testname, String codeFilename) throws Exception {
		String baseFilename = codeFilename + ".";
		String minExt = "min";
		String prsExt = "txt";
		String ansExt = "txt";
		
		String minDir = "src/TestCaseCode/";
		String prsDir = "src/ParserOutput/";
		String ansDir = "src/ParserOutput/ans/";


		String minFilename = minDir + baseFilename + minExt;
		String prsFilename = prsDir + baseFilename + prsExt;
		String ansFilename = ansDir + baseFilename + ansExt;

		Tokenizer tokenizer = new Tokenizer(minFilename);
		
		Parser parser = new Parser(tokenizer);
		parser.parse();
		
		String tree = parser.getTreeString();
		
		File f = new File(prsFilename);
		FileWriter fw = new FileWriter(f);
		fw.write(tree);
		fw.flush();
		fw.close();
		
		boolean ret = CheckOutput.getDiffString(testname, prsFilename, ansFilename);
		
		if (ret == false) {
			fail("the output parser tree doesn't match the answer.");
		}

	}


	/**
	 * @param testname
	 * @param codeFilename
	 * @throws Exception
	 */
	void testCompiler(String testname, String codeFilename) throws Exception {
		
		String baseFilename = codeFilename + ".";
		String minExt = "min";
		String asmExt = "asm";
		String ansExt = "asm";
		
		String minDir = "src/TestCaseCode/";
		String asmDir = "src/CompilerOutput/";
		String ansDir = "src/CompilerOutput/ans/";
		
		if (CodeGenerator.isMac() == true) {
			ansDir += "mac/";
		}
		else if (CodeGenerator.isWindows() == true) {
			ansDir += "win/";
		}

		String minFilename = minDir + baseFilename + minExt;
		String asmFilename = asmDir + baseFilename + asmExt;
		String ansFilename = ansDir + baseFilename + ansExt;
		
		Tokenizer tokenizer = new Tokenizer(minFilename);
		
		Parser parser = new Parser(tokenizer);
		parser.parse();
		
		CodeGenerator generator = CodeGenerator.getCodeGenerator();
		
		AstNode root = parser.getRootNode();
		generator.beforeCGEN(root);
		generator.cgen(root);
		generator.write(asmFilename);
		
		boolean ret = CheckOutput.getDiffString(testname, asmFilename, ansFilename);
		
		if (ret == false) {
			fail("the output asm code doesn't match the answer.");
		}
	}
	
}
