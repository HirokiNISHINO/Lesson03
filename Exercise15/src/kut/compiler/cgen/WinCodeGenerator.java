package kut.compiler.cgen;

class WinCodeGenerator extends CodeGenerator {
	
	/**
	 * 
	 */
	public WinCodeGenerator() {
		super();
	}
	
	/**
	 *
	 */
	public String getEntryPointLabelName()
	{
		return "_start";
	}

	/**
	 *
	 */
	public String getExternalFunctionName(String funcname)
	{
		return funcname;
	}
	
	/**
	 *
	 */
	public String getExitSysCallNum()
	{
		return "0x60";
	}
}


