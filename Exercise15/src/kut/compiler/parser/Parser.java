package kut.compiler.parser;

import kut.compiler.cgen.type.TypeChecker;
import kut.compiler.error.SyntaxErrorException;
import kut.compiler.parser.ast.AstAssignment;
import kut.compiler.parser.ast.AstBinOp;
import kut.compiler.parser.ast.AstBooleanLiteral;
import kut.compiler.parser.ast.AstCompoundStatement;
import kut.compiler.parser.ast.AstGVarDec;
import kut.compiler.parser.ast.AstIdentifier;
import kut.compiler.parser.ast.AstIntLiteral;
import kut.compiler.parser.ast.AstNode;
import kut.compiler.parser.ast.AstPrint;
import kut.compiler.parser.ast.AstPrintln;
import kut.compiler.parser.ast.AstProgram;
import kut.compiler.parser.ast.AstUnaryOp;
import kut.compiler.tokenizer.Token;
import kut.compiler.tokenizer.Tokenizer;

/**
 * @author hnishino
 *
 */
/**
 * @author hnishino
 *
 */
/**
 * @author hnishino
 *
 */
public class Parser {

	AstNode 	rootNode	;
	Tokenizer	tokenizer	;

	/**
	 * 
	 */
	public Parser(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
		this.rootNode = null;
	}

	/**
	 * @throws SyntaxErrorException
	 */
	public void parse() throws SyntaxErrorException
	{
		tokenizer.tokenize();
		this.rootNode = parseProgram();
	}
	
	/**
	 * @throws SyntaxErrorException
	 */
	public AstNode parseProgram() throws SyntaxErrorException
	{
		AstProgram program = new AstProgram();
		
		while(true) {
			Token token = this.tokenizer.peekToken();
			if (token.getKlazz() == Token.EOF) {
				break;
			}
			AstNode stmt = this.parseStmt();	
			program.addStatement(stmt);
		}
		
		Token token = this.tokenizer.peekToken();
		if (token.getKlazz() != Token.EOF) {
			throw new SyntaxErrorException("expecting an EOF token, but found :" + token);
		}
				
		return program;
	}
	
	
	/**
	 * 
	 */
	public AstNode parseStmt() throws SyntaxErrorException 
	{
		AstNode stmt = null;
		
		Token token = this.tokenizer.peekToken();
		
		if (token.getKlazz() == Token.PRINT) {
			stmt = this.parsePrintStmt();
		}
		else if (token.getKlazz() == Token.PRINTLN) {
			stmt = this.parsePrintlnStmt();
		}
		else if (token.getKlazz() == Token.GLOBAL) {
			stmt = this.parseGlobalVarDecStmt();
		}
		else if (token.getKlazz() == '{') {
			stmt = this.parseCompoundStatement();
		}
		//上に該当しなかったら式文として扱う。
		else {
			stmt = this.parseExprStmt();
		}
		
		return stmt;
	}
	
	/**
	 * @return
	 * @throws SyntaxErrorException
	 */
	public AstNode parseCompoundStatement() throws SyntaxErrorException
	{
		Token t = this.tokenizer.peekToken();
		if (t.getKlazz() != '{') {
			throw new SyntaxErrorException("expecting a '{' token, but found :" + t);
		}
		this.tokenizer.consumeToken();
		
		AstCompoundStatement cs = new AstCompoundStatement();
		
		while(true) {
			t = this.tokenizer.peekToken();
			if (t.getKlazz() == '}') {
				this.tokenizer.consumeToken();
				break;
			}
			AstNode s = parseStmt();
			cs.addStatement(s);
		}
		
		return cs;
	}
	
	
	/**
	 * @return
	 * @throws SyntaxErrorException
	 */
	public AstNode parseExprStmt() throws SyntaxErrorException
	{
		AstNode expr = this.parseExpr();
		
		Token t = this.tokenizer.peekToken();
		if (t.getKlazz() != ';') {
			throw new SyntaxErrorException("expecting a ';' token, but found :" + t);
		}
		this.tokenizer.consumeToken();
		
		return expr;
	}
	
	/**
	 * @return
	 */
	public String getTreeString() {
		if (this.rootNode == null) {
			return "";
		}
		return this.rootNode.getTreeString(0);
	}
	
	
	/**
	 * @return
	 */
	public AstNode getRootNode() {
		return this.rootNode;
	}
	
	/**
	 * @return
	 * @throws SyntaxErrorException
	 */
	public AstNode parseGlobalVarDecStmt() throws SyntaxErrorException
	{
		Token t = this.tokenizer.peekToken();
		if (t.getKlazz() != Token.GLOBAL) {
			throw new SyntaxErrorException("expecting a 'global' token, but found :" + t);
		}
		this.tokenizer.consumeToken();
		
		Token type = this.tokenizer.peekToken();
		if (TypeChecker.isValidType(type.getLexeme()) == false) {
			throw new SyntaxErrorException("the given type is not valid :" + type);			
		}
		this.tokenizer.consumeToken();
		
		Token id = this.tokenizer.peekToken();
		if (id.getKlazz() != Token.IDENTIFIER) {
			throw new SyntaxErrorException("expecting an identifier, but found :" + t);
		}
		this.tokenizer.consumeToken();
		
		Token t2 = this.tokenizer.peekToken();
		if (t2.getKlazz() != ';') {
			throw new SyntaxErrorException("expecting a ';' token, but found :" + t2);
		}
		this.tokenizer.consumeToken();
		
		return new AstGVarDec(t, type, id);
	}

	/**
	 * @return
	 * @throws SyntaxErrorException
	 */
	public AstNode parsePrintlnStmt() throws SyntaxErrorException
	{
		Token t = this.tokenizer.peekToken();
		if (t.getKlazz() != Token.PRINTLN) {
			throw new SyntaxErrorException("expecting a 'println' token, but found :" + t);
		}
		AstPrintln printlnNode = new AstPrintln(t);
		this.tokenizer.consumeToken();
		
		t = this.tokenizer.peekToken();
		if (t.getKlazz() != '(') {
			throw new SyntaxErrorException("expecting a '(' token, but found :" + t);
		}
		this.tokenizer.consumeToken();
		
		
		t = this.tokenizer.peekToken();
		if (t.getKlazz() != ')') {
			while(true) {
				t = this.tokenizer.peekToken();

				AstNode n = parseExpr();
				printlnNode.addParam(n);

				t = this.tokenizer.peekToken();
				if (t.getKlazz() == ')') {
					break;
				}
				if (t.getKlazz() != ',') {
					throw new SyntaxErrorException("expecting a ')' | ',' token, but found :" + t);
				}
				this.tokenizer.consumeToken();
			}
		}
		t = this.tokenizer.peekToken();
		if (t.getKlazz() != ')') {
			throw new SyntaxErrorException("expecting a ')' token, but found :" + t);
		}
		this.tokenizer.consumeToken();
		
		t = this.tokenizer.peekToken();
		if (t.getKlazz() != ';') {
			throw new SyntaxErrorException("expecting a ';' token, but found :" + t);
		}
		this.tokenizer.consumeToken();
		
		return printlnNode;
	}
	
	/**
	 * @return
	 * @throws SyntaxErrorException
	 */
	public AstNode parsePrintStmt() throws SyntaxErrorException
	{
		Token t = this.tokenizer.peekToken();
		if (t.getKlazz() != Token.PRINT) {
			throw new SyntaxErrorException("expecting a 'print' token, but found :" + t);
		}
		AstPrint printNode = new AstPrint(t);
		this.tokenizer.consumeToken();
		
		t = this.tokenizer.peekToken();
		if (t.getKlazz() != '(') {
			throw new SyntaxErrorException("expecting a '(' token, but found :" + t);
		}
		this.tokenizer.consumeToken();
		
		
		t = this.tokenizer.peekToken();
		if (t.getKlazz() != ')') {
			while(true) {
				t = this.tokenizer.peekToken();

				AstNode n = parseExpr();
				printNode.addParam(n);

				t = this.tokenizer.peekToken();
				if (t.getKlazz() == ')') {
					break;
				}
				if (t.getKlazz() != ',') {
					throw new SyntaxErrorException("expecting a ')' | ',' token, but found :" + t);
				}
				this.tokenizer.consumeToken();
			}
		}
		t = this.tokenizer.peekToken();
		if (t.getKlazz() != ')') {
			throw new SyntaxErrorException("expecting a ')' token, but found :" + t);
		}
		this.tokenizer.consumeToken();
		
		t = this.tokenizer.peekToken();
		if (t.getKlazz() != ';') {
			throw new SyntaxErrorException("expecting a ';' token, but found :" + t);
		}
		this.tokenizer.consumeToken();
		
		return printNode;
	}
	/**
	 * @return
	 * @throws SyntaxErrorException
	 */
	public AstNode parseExpr() throws SyntaxErrorException
	{
		return this.parseLorExpr();
	}
	
	/**
	 * @return
	 * @throws SyntaxErrorException
	 */
	public AstNode parseLorExpr() throws SyntaxErrorException
	{
		AstNode lhs = parseLandExpr();
		
		while (true) {
			Token token = this.tokenizer.peekToken();
			if (token.getKlazz() != Token.LOR) {
				break;
			}
			this.tokenizer.consumeToken();
			
			AstNode rhs = this.parseLandExpr();
			
			lhs = new AstBinOp(token, lhs, rhs);
		}
		return lhs;
	}
	

	/**
	 * @return
	 * @throws SyntaxErrorException
	 */
	public AstNode parseLandExpr() throws SyntaxErrorException
	{
		AstNode lhs = parseEqualityExpr();
		
		while (true) {
			Token token = this.tokenizer.peekToken();
			if (token.getKlazz() != Token.LAND) {
				break;
			}
			this.tokenizer.consumeToken();
			
			AstNode rhs = this.parseEqualityExpr();
			
			lhs = new AstBinOp(token, lhs, rhs);
		}
		return lhs;
	}
	
	/**
	 * @return
	 * @throws SyntaxErrorException
	 */
	public AstNode parseEqualityExpr() throws SyntaxErrorException
	{
		AstNode lhs = parseRelationalExpr();

		while (true) {
			Token token = this.tokenizer.peekToken();
			if (token.getKlazz() != Token.EQUAL_TO && token.getKlazz() != Token.NOT_EQUAL_TO) {
				break;
			}
			this.tokenizer.consumeToken();

			AstNode rhs = this.parseRelationalExpr();
			
			lhs = new AstBinOp(token, lhs, rhs);
		}

		return lhs;
	}
	
	/**
	 * @return
	 * @throws SyntaxErrorException
	 */
	public AstNode parseRelationalExpr() throws SyntaxErrorException
	{
		AstNode lhs = parseAdditiveExpr();
		
		while(true) {
			Token token = this.tokenizer.peekToken();
			if (token.getKlazz() != '<' && token.getKlazz() != Token.LESS_THAN_OR_EQUAL_TO &&
				token.getKlazz() != '>' && token.getKlazz() != Token.GREATER_THAN_OR_EQUAL_TO ) {
				break;
			}
			this.tokenizer.consumeToken();
		
			AstNode rhs = parseAdditiveExpr();

			lhs = new AstBinOp(token, lhs, rhs);
		}
		return lhs;
	}
	
	/**
	 * @return
	 * @throws SyntaxErrorException
	 */
	public AstNode parseAdditiveExpr() throws SyntaxErrorException
	{
		AstNode lhs = this.parseMultiplicativeExpr();
		
		while(true) {
			Token token = this.tokenizer.peekToken();

			if (token.getKlazz() != '+' && token.getKlazz() != '-') {
				break;		
			}
			this.tokenizer.consumeToken();
			
			AstNode rhs = this.parseMultiplicativeExpr();
			lhs = new AstBinOp(token, lhs, rhs);
		}
		
		return lhs;
	}
	
	/**
	 * @return
	 * @throws SyntaxErrorException
	 */
	public AstNode parseMultiplicativeExpr() throws SyntaxErrorException
	{
		AstNode lhs = this.parseUnaryExpr();
		
		while(true) {
			Token token = this.tokenizer.peekToken();

			if (token.getKlazz() != '*' && token.getKlazz() != '/' && token.getKlazz() != '%') {
				break;		
			}
			this.tokenizer.consumeToken();
			
			AstNode rhs = this.parseUnaryExpr();
			lhs = new AstBinOp(token, lhs, rhs);
		}
		
		return lhs;
	}
	
	/**
	 * @return
	 * @throws SyntaxErrorException
	 */
	public AstNode parsePrimaryExpression() throws SyntaxErrorException {
		Token token = this.tokenizer.peekToken();
		
		if (token.getKlazz() == '(') {
			return this.parseParenthesesExpr();
		}
		
		if (token.getKlazz() == Token.IDENTIFIER) {
			return this.parseIdentifierOrAssignmentExpr();
		}
		
		if (token.getKlazz() == Token.BOOLEAN_LITERAL) {
			return this.parseBooleanLiteral();
		}
		
		if (token.getKlazz() == '-' || token.getKlazz() == '!') {
			
		}
		
		return this.parseIntLiteral();
	}
	
	/**
	 * @return
	 * @throws SyntaxErrorException
	 */
	public AstNode parseUnaryExpr()  throws SyntaxErrorException {
		Token token = this.tokenizer.peekToken();
		
		if (token.getKlazz() == '-' || token.getKlazz() == '!') {
			this.tokenizer.consumeToken();
			AstNode expr = parsePrimaryExpression();
			AstNode unary = new AstUnaryOp(token, expr);
			return unary;
		}
		
		return this.parsePrimaryExpression();
	}
	
	/**
	 * @return
	 * @throws SyntaxErrorException
	 */
	public AstNode parseIdentifierOrAssignmentExpr() throws SyntaxErrorException {
		AstNode id = this.parseIdentifier();
		
		Token t = this.tokenizer.peekToken();
		if (t.getKlazz() != '=') {
			return id;
		}
		this.tokenizer.consumeToken();
		
		AstNode expr = this.parseExpr();
		return new AstAssignment(t, id, expr);
	}
	
	/**
	 * @return
	 */
	public AstNode parseIdentifier() throws SyntaxErrorException {
		Token t = this.tokenizer.peekToken();
		if (t.getKlazz() != Token.IDENTIFIER) {
			throw new SyntaxErrorException("expecting an identifier token, but found :" + t);
		}
		this.tokenizer.consumeToken();
		
		return new AstIdentifier(t);
	}
	
	/**
	 * @return
	 * @throws SyntaxErrorException
	 */
	public AstNode parseParenthesesExpr() throws SyntaxErrorException {
		Token token = this.tokenizer.peekToken();
		if (token.getKlazz() != '(') {
			throw new SyntaxErrorException("expecting a '(' token, but found :" + token);
		}
		this.tokenizer.consumeToken();
		
		AstNode expr = this.parseExpr();

		token = this.tokenizer.peekToken();
		if (token.getKlazz() != ')') {
			throw new SyntaxErrorException("expecting a ')' token, but found :" + token);
		}
		this.tokenizer.consumeToken();

		return expr;
	}
	
	/**
	 * @return
	 */
	public AstIntLiteral parseIntLiteral() throws SyntaxErrorException 
	{
		Token token = this.tokenizer.peekToken();
		
		if (token.getKlazz() != Token.INT_LITERAL) {
			throw new SyntaxErrorException("expecting an INT_LITERAL token, but found :" + token);
		}
		this.tokenizer.consumeToken();
						
		return new AstIntLiteral(token);
	}
	
	/**
	 * @return
	 */
	public AstBooleanLiteral parseBooleanLiteral() throws SyntaxErrorException 
	{
		Token token = this.tokenizer.peekToken();
		
		if (token.getKlazz() != Token.BOOLEAN_LITERAL) {
			throw new SyntaxErrorException("expecting an BOOLEAN_LITERAL token, but found :" + token);
		}
		this.tokenizer.consumeToken();
		
		return new AstBooleanLiteral(token);
	}
}
