package kut.compiler.cgen.label;

public class CondBranchLabels {

	private String labelTrue;
	private String labelFalse;
	private String labelEnd;
	
	private static int cnt = 0;
	/**
	 * @return
	 */
	/**
	 * @return
	 */
	public static CondBranchLabels getLabels() {
		cnt++;
		return new CondBranchLabels("" + cnt);
	}
	
	/**
	 * @param labelBase
	 */
	private CondBranchLabels(String labelBase) {
		labelTrue 	= "cond_" + labelBase + "_true";
		labelFalse 	= "cond_" + labelBase + "_false";
		labelEnd 	= "cond_" + labelBase + "_end"; 
	}

	/**
	 * @return
	 */
	public String getTrueBranchLabel() {
		return labelTrue;
	}
	
	/**
	 * @return
	 */
	public String getFalseBranchLabel() {
		return labelFalse;
	}
	
	/**
	 * @return
	 */
	public String getEndCondLabel() {
		return labelEnd;
	}
}
