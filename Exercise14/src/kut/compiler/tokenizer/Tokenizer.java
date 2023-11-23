package kut.compiler.tokenizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * @author hnishino
 * 字句解析を行う。
 * 結果はVector<Token>として保持する。
 */
public class Tokenizer {
	
	Vector<Character>	inputProgram;	//解析するプログラム
	int					ridx		;	//現在読んでいる文字の位置
	
	Vector<Token> 		tokens		;	//トークン列（内部保持）
	int					tokenIndex	;	//次に読みだすトークンの位置
	
	int	tokenLine	= 0;
	int tokenPos	= 0;
	
	int line = 0;
	int pos  = 0;
	
	/**
	 * 
	 */
	public void reset()
	{
		inputProgram = new Vector<Character>();
		ridx = 0;
		
		tokens = new Vector<Token>();
		tokenIndex = 0;
		
		tokenLine = 1;
		tokenPos  = 1;

		line = 1;
		pos  = 1;

		return;		
	}
	
	/**
	 * @return
	 */
	public int getTokenLine() {
		return tokenLine;
	}

	/**
	 * @param tokenLine
	 */
	public void setTokenLine(int tokenLine) {
		this.tokenLine = tokenLine;
	}

	/**
	 * @return
	 */
	public int getTokenPos() {
		return tokenPos;
	}

	/**
	 * @param tokenPos
	 */
	public void setTokenPos(int tokenPos) {
		this.tokenPos = tokenPos;
	}

	/**
	 * @return
	 */
	public int getLine() {
		return line;
	}

	/**
	 * @param line
	 */
	public void setLine(int line) {
		this.line = line;
	}

	/**
	 * @return
	 */
	public int getPos() {
		return pos;
	}

	/**
	 * @param pos
	 */
	public void setPos(int pos) {
		this.pos = pos;
	}

	/**
	 * @param fliename
	 * @throws IOException
	 */
	public Tokenizer(String fliename) throws IOException
	{
		this(new File(fliename));
	}
	/**
	 * 
	 */
	public Tokenizer(File file) throws IOException
	{
		this.reset();
		//check if the file exists
		if (file.exists() == false) {
			throw new FileNotFoundException("input file can't be found. filename:" + file.getAbsolutePath() );
		}
		
		FileReader fis = new FileReader(file);
		
		while(true) {
			int c = fis.read();
			if (c == -1) {
				break;
			}
			inputProgram.add((char)c);
		}
		
		fis.close();
		
		return;
	}
	
	
	/**
	 * 
	 */
	public void tokenize()
	{
		//字句解析のループ
		while(true) {
			
			//現在の文字を読む
			int c = this.peekChar();
			
			//空白文字だったらスキップ
			if (Character.isWhitespace(c)) {
				this.consumeChar(); //現在の文字を消費
				continue;
			}
				
			//入力文字がもうなかったらbreakしておしまい
			if (c == -1) {
				break;
			}

			//トークンのファイル上での位置を保存
			this.setTokenLine	(this.getLine());
			this.setTokenPos	(this.getPos());
			
			//もし現在の文字が数字だったら、整数トークンとして処理
			if (Character.isDigit(c)) {
				//整数トークンを作る。
				Token t = tokenizeIntLiteral();
				tokens.add(t);
				
				//ループ先頭に戻る
				continue;
			}
			
			// '=' and '=='
			if (c == '=') {
				this.consumeChar();
				c = this.peekChar();
				//'=='ではなかったら'='である。
				if (c != '=') {
					Token t = new Token('=', "=", this.getTokenLine(), this.getTokenPos());
					tokens.add(t);
					continue;
				}
				// '=='だった
				this.consumeChar();
				Token t = new Token(Token.EQUAL_TO, "==", this.getTokenLine(), this.getPos());
				tokens.add(t);
				continue;
			}
			
			// '<' and '<='
			if (c == '<') {
				this.consumeChar();
				c = this.peekChar();
				// '<=' ではなかったら'<'である。
				if (c != '=') {
					Token t = new Token('<', "<", this.getTokenLine(), this.getTokenPos());
					tokens.add(t);
					continue;
				}
				// '<='だった
				this.consumeChar();
				Token t = new Token(Token.LESS_THAN_OR_EQUAL_TO, "<=", this.getTokenLine(), this.getPos());
				tokens.add(t);
				continue;
			}
			
			// '>' and '>='
			if (c == '>') {
				this.consumeChar();
				c = this.peekChar();
				// '>=' ではなかったら'>'である。
				if (c != '=') {
					Token t = new Token('>', ">", this.getTokenLine(), this.getTokenPos());
					tokens.add(t);
					continue;
				}
				// '>='だった
				this.consumeChar();
				Token t = new Token(Token.GREATER_THAN_OR_EQUAL_TO, ">=", this.getTokenLine(), this.getPos());
				tokens.add(t);
				continue;
			}
			
			// '!' and '!='
			if (c == '!') {
				this.consumeChar();
				c = this.peekChar();
				// '!=' ではなかったら'!'である。
				if (c != '=') {
					Token t = new Token('!', "!", this.getTokenLine(), this.getTokenPos());
					tokens.add(t);
					continue;
				}
				// '!='だった
				this.consumeChar();
				Token t = new Token(Token.NOT_EQUAL_TO, "!=", this.getTokenLine(), this.getPos());
				tokens.add(t);
				continue;
			}
			
			// '&' and '&&'
			if (c == '&') {
				this.consumeChar();
				c = this.peekChar();
				// '&&' ではなかったら'&'である。
				if (c != '&') {
					Token t = new Token('&', "&", this.getTokenLine(), this.getTokenPos());
					tokens.add(t);
					continue;
				}
				// '&&'だった
				this.consumeChar();
				Token t = new Token(Token.LAND, "&&", this.getTokenLine(), this.getPos());
				tokens.add(t);
				continue;
			}
			
			// '|' and '||'
			if (c == '|') {
				this.consumeChar();
				c = this.peekChar();
				// '||' ではなかったら'|'である。
				if (c != '|') {
					Token t = new Token('|', "|", this.getTokenLine(), this.getTokenPos());
					tokens.add(t);
					continue;
				}
				// '&&'だった
				this.consumeChar();
				Token t = new Token(Token.LOR, "||", this.getTokenLine(), this.getPos());
				tokens.add(t);
				continue;
			}
			
			if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '(' || c == ')' || c == ',' || c == ';' ) {
				//記号一文字はそのアスキーコードをトークン種別とする
				Token t = new Token(c, "" + (char)c, this.getTokenLine(), this.getTokenPos());
				tokens.add(t);
				this.consumeChar();
				continue;
			}
			
			//識別子や予約語
			//とりあえず識別子としてトークナイズし、予約語と一致すれば差し替える。
			if (Character.isJavaIdentifierStart(c)) {
				Token t = this.tokenizerIdentifier();
				if ("print".equals(t.getLexeme())) {
					t.setKlazz(Token.PRINT);
				}
				else if ("println".equals(t.getLexeme())){
					t.setKlazz(Token.PRINTLN);
				}
				else if ("global".equals(t.getLexeme())) {
					t.setKlazz(Token.GLOBAL);
				}
				else if ("int".equals(t.getLexeme())) {
					t.setKlazz(Token.INT);
				}
				else if ("boolean".equals(t.getLexeme())) {
					t.setKlazz(Token.BOOLEAN);
				}
				else if ("true".equals(t.getLexeme())) {
					t.setKlazz(Token.BOOLEAN_LITERAL);
				}
				else if ("false".equals(t.getLexeme())) {
					t.setKlazz(Token.BOOLEAN_LITERAL);
				}
				tokens.add(t);
				continue;
			}
			

			//ここにきてたらErrorトークン。
			Token t = new Token(Token.ERROR, "" + (char)c, this.getTokenLine(), this.getTokenPos());
			tokens.add(t);
			this.consumeChar(); //現在の文字を消費
		}
		
		return;
	}
	
	/**
	 * @return
	 */
	public Token tokenizerIdentifier() 
	{
		//既にチェックしているので本来は不要。
		int c = this.peekChar();
		if (Character.isJavaIdentifierStart(c) == false){
			throw new RuntimeException("a bug in the tokenizer. the code should not reach here.");
		}
			
		StringBuffer buf = new StringBuffer();
		buf.append("" + (char) c);
		this.consumeChar();
		
		while(true) {
			c = this.peekChar();
			if (c == -1 || Character.isJavaIdentifierPart(c) == false) {
				break;
			}
			buf.append("" + (char)c);
			this.consumeChar();
		}
		return new Token(Token.IDENTIFIER, buf.toString(), this.getTokenLine(), this.getTokenPos());
	}

	/**
	 * 整数トークンを作る
	 * @return　整数トークン
	 */
	public Token tokenizeIntLiteral() {
	
		StringBuffer buf = new StringBuffer();
		while(true) {
			//現在の文字を読む
			int c = this.peekChar();
			
			//EOFであれば打ち切り
			if (c == -1) {
				break;
			}
			
			//数字以外であれば打ち切り
			if (Character.isDigit(c) == false){
				break;
			}
			
			//読み込んだ数字をバッファに追加
			buf.append((char)c);
			this.consumeChar(); //現在の文字を消費
		}
		
		return new Token(Token.INT_LITERAL, buf.toString(), this.getTokenLine(), this.getTokenPos());
	}

	
	/**
	 * @return
	 */
	private int peekChar() {
		if (ridx >= inputProgram.size()) {
			return -1;
		}
		
		int c = inputProgram.elementAt(ridx);
		
		return c;
	}
	
	/**
	 * 
	 */
	private void consumeChar() {
		if (ridx >= inputProgram.size()) {
			return;
		}
		
		this.pos++;
		
		int c = peekChar();
		if (c == '\n') {
			this.line++;
			this.pos = 1;
		}
		
		ridx++;
		
		return;
	}
	
	/**
	 * @return the current token.
	 */
	public Token peekToken()
	{
		//もうトークンがなかったらEOFトークンを返す
		if (tokenIndex >= tokens.size()) {
			return new Token(Token.EOF, null, this.getTokenLine(), this.getTokenPos());
		}
		
		//次のトークンを返す
		Token token = tokens.elementAt(tokenIndex);
		return token;
	}
	
	/**
	 * consume token.
	 */
	public void consumeToken()
	{
		if (tokenIndex >= tokens.size()) {
			return;
		}
		tokenIndex++;
		
		return;
	}

}
