package io.specto.demos.paymentgateway.payment;

import io.specto.demos.paymentgateway.account.Account;
import io.specto.demos.paymentgateway.account.InvalidRefundValueException;
import io.specto.demos.paymentgateway.account.RefundExceedsExecuteValueException;
import io.specto.demos.paymentgateway.account.Transaction;
import io.specto.demos.paymentgateway.account.TxnNotFoundException;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
	/**
	 * Executes a payment transaction between a payee and a payer.
	 * @param payer
	 * @param payee
	 * @param amount This is the amount to be transfered from the payer to the Payee
	 * @return A unique transaction identifier.
	 */
    UUID execute(Account payer, Account payee, double amount);
    
    /**
     * Retrieves an existing transaction for the given transactionId
     * @param transactionId
     * @return
     * @throws TxnNotFoundException when the Transaction can't be found
     */
    Transaction view(UUID transactionId) throws TxnNotFoundException;
    
    /**
	 * Refunds the given amount from the transaction which cannot exceed<br />
	 * the original transaction amount.
     * @param transactionId
     * @param amount
     * @return
     * @throws TxnNotFoundException 
     * @throws RefundExceedsExecuteValueException
     * @throws InvalidRefundValueException
     */
    UUID refund(UUID transactionId, double amount) throws TxnNotFoundException, 
    										RefundExceedsExecuteValueException;
    
    /**
     * Computes the cumulative transaction value for all valid transactions and refunds
     * @return
     */
    double getCumulativeTxnValue();
    
    /**
     * Retrieves a list of payers with cumulative transactions <br />
     * i.e. Payers that have made more than 1 transaction
     * @return
     */
    List<String> getCumulativePayerNames();
}
