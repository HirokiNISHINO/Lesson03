package kut.compiler.cgen.label;

public class WhileLoopLabels {

	private String labelEntry	;
	private String labelExit	;
	
	private static int cnt = 0;
	/**
	 * @return
	 */
	/**
	 * @return
	 */
	public static WhileLoopLabels getLabels() {
		cnt++;
		return new WhileLoopLabels("" + cnt);
	}
	
	/**
	 * @param labelBase
	 */
	private WhileLoopLabels(String labelBase) {
		labelEntry 	= "while_" + labelBase + "_entry";
		labelExit 	= "while_" + labelBase + "_exit";
	}

	/**
	 * @return
	 */
	public String getEntryLabel() {
		return labelEntry;
	}
	
	/**
	 * @return
	 */
	public String getExitLabel() {
		return labelExit;
	}
	
}
