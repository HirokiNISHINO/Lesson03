/**
 * 
 */
package kut.compiler.error;

/**
 * @author hnishino
 *
 */
public class SemanticErrorException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public SemanticErrorException() {
	}

	/**
	 * @param message
	 */
	public SemanticErrorException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SemanticErrorException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SemanticErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public SemanticErrorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
