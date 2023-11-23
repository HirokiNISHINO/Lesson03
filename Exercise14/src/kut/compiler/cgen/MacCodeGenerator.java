package kut.compiler.cgen;


class MacCodeGenerator extends CodeGenerator {
	
	/**
	 * 
	 */
	public MacCodeGenerator() {
		super();
	}
	

	/**
	 *
	 */
	public String getEntryPointLabelName()
	{
		return "_main";
	}

	/**
	 *
	 */
	public String getExternalFunctionName(String funcname)
	{
		return "_" + funcname;
	}
	
	/**
	 *
	 */
	public String getExitSysCallNum()
	{
		return "0x2000001";
	}
}
