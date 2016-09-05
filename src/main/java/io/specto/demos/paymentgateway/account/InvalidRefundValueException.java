package io.specto.demos.paymentgateway.account;

/**
 * Thrown when an invalid Refund Value is requested.
 *
 */
public class InvalidRefundValueException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidRefundValueException() {
	        super();
	    }

	    public InvalidRefundValueException(String message) {
	        super(message);
	    }

}
