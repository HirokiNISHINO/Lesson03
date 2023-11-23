/**
 * 
 */
package kut.compiler.error;

/**
 * @author hnishino
 *
 */
public class SyntaxErrorException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public SyntaxErrorException() {
	}

	/**
	 * @param message
	 */
	public SyntaxErrorException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SyntaxErrorException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SyntaxErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public SyntaxErrorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
