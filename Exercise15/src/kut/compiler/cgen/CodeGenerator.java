package kut.compiler.cgen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import kut.compiler.cgen.symboltable.SymbolTable;
import kut.compiler.cgen.symboltable.TypeOfId;
import kut.compiler.cgen.type.Type;
import kut.compiler.error.SemanticErrorException;
import kut.compiler.parser.ast.AstNode;


/**
 * @author hnishino
 *
 */
public abstract class CodeGenerator 
{
	protected StringBuffer 	sb;
	protected SymbolTable	symbolTable;
	
	
	/**
	 * @return
	 */
	public static boolean isMac() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac");
	}
	
	/**
	 * @return
	 */
	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().startsWith("windows");
	}

	
	/**
	 * @return
	 */
	public static CodeGenerator getCodeGenerator()
	{
		if (isMac()) {
			return new MacCodeGenerator();
		}
		else if (isWindows()) {
			return new WinCodeGenerator();
		}
		
		throw new RuntimeException("This OS is not supported.");
	}



	/**
	 * 
	 */
	protected CodeGenerator() {
		this.sb = new StringBuffer();
		this.symbolTable = new SymbolTable();
	}
	
	/**
	 * @return
	 */
	public abstract String getEntryPointLabelName();
	
	/**
	 * @return
	 */
	public abstract String getExitSysCallNum();
	
	/**
	 * @param funcname
	 * @return
	 */
	public abstract String getExternalFunctionName(String funcname);
	
	/**
	 * @return
	 */
	public String getExitSysCallLabel()
	{
		return "exit_program#";
	}
	
	/**
	 * @param code
	 */
	public void appendCode(String code) 
	{
		this.appendCode(code, 1);
	}
	
	/**
	 * @param code
	 * @param indent
	 */
	public void appendCode(String code, int indent)
	{
		for (int i = 0; i < indent; i++) {
			sb.append("\t");			
		}
		sb.append(code);
		sb.append("\n");
	}
	
	/**
	 * 
	 */
	public void appendCode()
	{
		sb.append("\n");
	}
	
	/**
	 * @param label
	 */
	public void appendLabel(String label)
	{
		sb.append(label);
		sb.append(":\n");
	}
	
	
	/**
	 * @param fname
	 * @throws IOException
	 */
	public void write(String fname) throws IOException
	{
		File f = new File(fname);
		FileOutputStream fos = new FileOutputStream(f);
		PrintWriter	pw = new PrintWriter(fos);
		
		pw.write(sb.toString());
		
		pw.close();
		
	}
	
	/**
	 * 
	 */
	public void print() {
		System.out.print(sb);
	}
	
	/**
	 * @param program
	 */
	public void beforeCGEN(AstNode program) throws SemanticErrorException
	{
		program.beforeCGEN(this);
	}
	
	/**
	 * @param program
	 */
	public void cgen(AstNode program) 
	{		
		this.cgenPrologue();
		program.cgen(this);
		this.cgenEpilogue();
		return;
	}
	
	/**
	 * @return
	 */
	public String getPrintIntLabel()
	{
		return "print_int#";
	}
	
	/**
	 * @return
	 */
	public String getPrintStringLabel()
	{
		return "print_string#";
	}

	/**
	 * @return
	 */
	public String getPrintDoubleLabel()
	{
		return "print_double#";
	}
	/**
	 * @return
	 */
	public String getPrintBooleanLabel()
	{
		return "print_boolean#";
	}
	
	/**
	 * @return
	 */
	public String getPrintCRLabel()
	{
		return "print_CR#";
	}
	
	
	/**
	 * @param id
	 */
	public void declareGlobalVariable(String id, Type type) throws SemanticErrorException {
		this.symbolTable.declareGlobalVariable(id, type);
	}
	
	/**
	 * @param id
	 * @return
	 */
	public String getGlobalVariableMemoryAddressLabel(String id) {
		return this.symbolTable.getMemorryAddressLabelOfGlobalVariable(id);
	}
	
	/**
	 * @param id
	 * @return
	 */
	public TypeOfId getTypeOfId(String id) {
		return this.symbolTable.getTypeOfId(id);
	}
	
	/**
	 * @param id
	 * @return
	 */
	public Type getTypeOfGlobalVariable(String id) {
		return this.symbolTable.getTypeOfGlobalVariable(id);
	}
	
	
	/**
	 * 
	 */
	public void cgenDataSectionForGlobalVariables() {
		//以下のような形式のコードを生成する。times 8 db 0は0初期化された8バイト分のデータ。
		//本演習のプログラミング言語では全ての値が8バイトの大きさ
		//グローバル変数のラベル　: times 8 db 0  
		Vector<String> labels = this.symbolTable.getAllMemoryAddressLabels();
		
		for (String l: labels) {
			this.appendCode(l + " : times 8 db 0");
		}
		return;
	}
	/**
	 * 
	 */
	public void cgenPrologue()
	{
		//--------------------------------------
		//extern 
		this.appendCode		("; 64 bit code.", 0);
		this.appendCode		("bits 64", 0);
		

		//--------------------------------------
		//extern 
		this.appendCode("; to use the printf() and strcmp functions.");

		this.appendCode	("extern " + this.getExternalFunctionName("printf"), 0);
		this.appendCode	("extern " + this.getExternalFunctionName("strcmp"), 0);
		this.appendCode	("extern " + this.getExternalFunctionName("fmod"), 0);
		this.appendCode	();


		//--------------------------------------
		//data section
		this.appendCode("; data section.", 0);
		this.appendCode("section .data", 0);
		
		//exit message format string.
		this.appendCode	(	"exit_fmt#:    db \"exit code:%d\", 10, 0 ; the format string for the exit message.");
		this.appendCode	();
		
		//print format strings.
		this.appendCode	(	"print_int_fmt#:    db \"%d\", 0 ; the format string for the print int.");
		this.appendCode	(	"print_string_fmt#:    db \"%s\", 0 ; the format string for the print string.");
		this.appendCode	(	"print_double_fmt#:    db \"%lf\", 0 ; the format string for the print double.");
		this.appendCode	(	"print_CR_fmt#:    db 10, 0 ; the format string for the print LF (\\n).");
		this.appendCode();
		this.appendCode	(	"print_boolean_string_true#:    db \"true\", 0 ; the format string for the print double.");
		this.appendCode	(	"print_boolean_string_false#:    db \"false\", 0 ; the format string for the print double.");
		this.appendCode();

		//memory spaces for global variables.
		this.cgenDataSectionForGlobalVariables();
		this.appendCode();
		
		
		//--------------------------------------
		//text section
		this.appendCode("; text section", 0);
		this.appendCode("section .text", 0);
		this.appendCode(	"global " + this.getEntryPointLabelName() + " ; the entry point.");
		this.appendCode();
		
		
		//the exit_program subroutine.		
		this.appendCode("; the subroutine for sys-exit. rax will be the exit code.", 0);
		this.appendLabel(this.getExitSysCallLabel());				// where we exit the program.
		
		this.appendCode("and rsp, 0xFFFFFFFFFFFFFFF0 ; stack must be 16 bytes aligned to call a C function.");
		this.appendCode("push rax ; we need to preserve rax here.");
		this.appendCode("push rax ; pushing twice for 16 byte alignment. We'll discard this later. ");
		this.appendCode		();
		this.appendCode		("; call printf to print out the exit code.");
		this.appendCode		("lea rdi, [rel exit_fmt#] ; the format string");
		this.appendCode		("mov rsi, rax			; the exit code ");
		this.appendCode		("mov rax, 0			; no xmm register is used.");
		this.appendCode		("call " + this.getExternalFunctionName("printf"));
		this.appendCode		();
		this.appendCode		("pop rax ; this value will be discared (as we did 'push rax' twice for 16 bytes alignment.");
		this.appendCode		();
		this.appendCode		("mov rax, "+ this.getExitSysCallNum() + "; specify the exit sys call.");
		this.appendCode		("pop rdi ; this is the rax value we pushed at the entry of this sub routine");
		this.appendCode		("syscall ; exit!");
		this.appendCode		();
		
		
		//the print_int function.		
		this.appendCode("; the function for print(int).", 0);
		this.appendLabel(getPrintIntLabel());
		
		this.appendCode("push rbp 		; store the current base pointer.");
		this.appendCode("mov  rbp, rsp 	; move the base pointer to the new stack frame.");
		this.appendCode("and  rsp, 0xFFFFFFFFFFFFFFF0	; to make stack 16 byte aligned (ABI requires this!).");
		this.appendCode();
		this.appendCode("lea  rdi, [rel print_int_fmt#]");
		this.appendCode("mov  rsi, rax");
		this.appendCode("mov  rax, 0");
		this.appendCode("call " + this.getExternalFunctionName("printf"));
		this.appendCode();
		this.appendCode("mov  rsp, rbp");;
		this.appendCode("pop  rbp");
		this.appendCode("ret");
		this.appendCode();

		this.appendCode("; the function for print(string).", 0);
		this.appendLabel(getPrintStringLabel());
		
		this.appendCode("push rbp 		; store the current base pointer.");
		this.appendCode("mov  rbp, rsp 	; move the base pointer to the new stack frame.");
		this.appendCode("and  rsp, 0xFFFFFFFFFFFFFFF0	; to make stack 16 byte aligned (ABI requires this!).");
		this.appendCode();
		this.appendCode("lea  rdi, [rel print_string_fmt#]");
		this.appendCode("mov  rsi, rax");
		this.appendCode("mov  rax, 0");
		this.appendCode("call " + this.getExternalFunctionName("printf"));
		this.appendCode();
		this.appendCode("mov  rsp, rbp");;
		this.appendCode("pop  rbp");
		this.appendCode("ret");
		this.appendCode();

		this.appendCode("; the function for print(double).", 0);
		this.appendLabel(getPrintDoubleLabel());
		
		this.appendCode("push rbp 		; store the current base pointer.");
		this.appendCode("mov  rbp, rsp 	; move the base pointer to the new stack frame.");
		this.appendCode("and  rsp, 0xFFFFFFFFFFFFFFF0	; to make stack 16 byte aligned (ABI requires this!).");
		this.appendCode();
		this.appendCode("lea  rdi, [rel print_double_fmt#]");
		this.appendCode("movq xmm0, rax");
		this.appendCode("mov  rax, 1");
		this.appendCode("call " + this.getExternalFunctionName("printf"));
		this.appendCode();
		this.appendCode("mov  rsp, rbp");;
		this.appendCode("pop  rbp");
		this.appendCode("ret");
		this.appendCode();

		this.appendCode("; the function for print(boolean).", 0);
		this.appendLabel(getPrintBooleanLabel());
		
		this.appendCode("push rbp 		; store the current base pointer.");
		this.appendCode("mov  rbp, rsp 	; move the base pointer to the new stack frame.");
		this.appendCode("and  rsp, 0xFFFFFFFFFFFFFFF0	; to make stack 16 byte aligned (ABI requires this!).");
		this.appendCode();
		this.appendCode("cmp rax, 0");
		this.appendCode("je .print_boolean_false#");
		this.appendCode();
		this.appendCode(".print_boolean_true#:");
		this.appendCode("lea rsi, [rel print_boolean_string_true#]");
		this.appendCode("jmp .print_boolean_print#");
		this.appendCode();
		this.appendCode(".print_boolean_false#:");
		this.appendCode("lea rsi, [rel print_boolean_string_false#]");
		this.appendCode();
		
		this.appendCode(".print_boolean_print#:");
		this.appendCode("lea rdi, [rel print_string_fmt#]");
		this.appendCode("mov rax, 0");
		this.appendCode("call " + this.getExternalFunctionName("printf"));
		this.appendCode();
		this.appendCode("mov  rsp, rbp");;
		this.appendCode("pop  rbp");
		this.appendCode("ret");
		this.appendCode();
		
		//the printCR function.		
		this.appendCode("; the function for printCR.", 0);
		this.appendLabel(getPrintCRLabel());
		
		this.appendCode("push rbp 		; store the current base pointer.");
		this.appendCode("mov  rbp, rsp 	; move the base pointer to the new stack frame.");
		this.appendCode("and  rsp, 0xFFFFFFFFFFFFFFF0	; to make stack 16 byte aligned (ABI requires this!).");
		this.appendCode();
		this.appendCode("lea  rdi, [rel print_CR_fmt#]");
		this.appendCode("mov  rax, 0");
		this.appendCode("call " + this.getExternalFunctionName("printf"));
		this.appendCode();
		this.appendCode("mov  rsp, rbp");;
		this.appendCode("pop  rbp");
		this.appendCode("ret");
		this.appendCode();

		//main function
		this.appendLabel	(this.getEntryPointLabelName());
		this.appendCode		("mov rax, 0 ; initialize the accumulator register.");
		
	}
	
	
	/**
	 * 
	 */
	public void cgenEpilogue()
	{
		this.appendCode		();
		this.appendCode		(	"jmp " + this.getExitSysCallLabel() + " ; exit the program, rax should hold the exit code.");		
	}
	
	
}
