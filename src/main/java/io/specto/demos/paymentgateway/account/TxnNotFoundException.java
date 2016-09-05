package io.specto.demos.paymentgateway.account;

public class TxnNotFoundException extends Exception {

	private static final long serialVersionUID = -881961869699052514L;

	public TxnNotFoundException() {
        super();
    }

    public TxnNotFoundException(String message) {
        super(message);
    }
}
