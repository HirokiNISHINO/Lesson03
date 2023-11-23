package kut.compiler.tokenizer;

public class Token
{
	public static final int	EOF	 			= -1;
	public static final int	ERROR 			= -2;
	
	public static final int	INT_LITERAL		= -3;
	public static final int BOOLEAN_LITERAL = -4;
	
	public static final int IDENTIFIER 	= -100;
	
	public static final int PRINT		= -201;
	public static final int PRINTLN		= -202;
	
	public static final int GLOBAL		= -300;
	
	public static final int INT			= -400;
	public static final int BOOLEAN		= -401;
	
	
	public static final int GREATER_THAN_OR_EQUAL_TO 	= -500;
	public static final int LESS_THAN_OR_EQUAL_TO		= -501;
	public static final int EQUAL_TO					= -502;
	public static final int NOT_EQUAL_TO				= -503;
	public static final int LOR							= -504;
	public static final int LAND						= -505;
		
	
	private int 	klazz	; // トークン・クラス
	private String	lexeme	; // 語彙素
	
	private int		line	;
	private int		pos		;
	/**
	 * @param klazz
	 * @param lexeme
	 */
	public Token(int klazz, String lexeme, int line, int pos)
	{
		this.klazz 	= klazz	;
		this.lexeme = lexeme;
		this.line	= line	;
		this.pos	= pos	;
	}
	

	
	/**
	 * @return トークン・クラスを返す。
	 */
	public int getKlazz() {
		return this.klazz;
	}
	
	/**
	 * @param Klazz
	 */
	public void setKlazz(int klazz) {
		this.klazz = klazz;
	}
	
	/**
	 * @return 語彙素を返す。
	 */
	public String getLexeme() {
		return this.lexeme;
	}
	
	/**
	 * @param lexeme
	 * @return
	 */
	public void setLexeme(String lexeme) {
		this.lexeme = lexeme;
	}
	
	/**
	 * @return
	 */
	public int getLine() {
		return line;
	}



	/**
	 * @return
	 */
	public int getPos() {
		return pos;
	}



	/**
	 *
	 */
	public String toString() {
		return this.getTokenClassString() + ":" + this.lexeme + "@line:" + this.line + ", pos:" + this.pos;
	}
	
	/**
	 * @return
	 */
	private String getTokenClassString() {
		
		String s;
		switch(this.klazz) {
		case EOF:
			s = "EOF";
			break;
			
		case ERROR:
			s = "ERROR";
			break;
			
		case INT_LITERAL:
			s = "INT_LITERAL";
			break;
			
		case IDENTIFIER:
			s = "IDENTIFIER";
			break;
			
		case PRINT:
			s = "PRINT";
			break;
			
		case PRINTLN:
			s = "PRINTLN";
			break;
			
		case GLOBAL:
			s = "GLOBAL";
			break;
			
		case INT:
			s = "INT";
			break;
			
		case BOOLEAN:
			s = "BOOLEAN";
			break;
		
		case BOOLEAN_LITERAL:
			s = "BOOLEAN_LITERAL";
			break;
		
		default:
			s = "" + (char)this.klazz;
			break;
		}
		
		return s;
	}
}
