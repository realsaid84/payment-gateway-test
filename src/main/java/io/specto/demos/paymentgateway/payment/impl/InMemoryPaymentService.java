/**
 * 
 */
package io.specto.demos.paymentgateway.payment.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import io.specto.demos.paymentgateway.account.Account;
import io.specto.demos.paymentgateway.account.RefundExceedsExecuteValueException;
import io.specto.demos.paymentgateway.account.Transaction;
import io.specto.demos.paymentgateway.account.TxnNotFoundException;
import io.specto.demos.paymentgateway.payment.PaymentService;

/**
 * Implementation of a Payment Service based on an in-memory transaction data
 * store<br />
 * The in-memory transaction data store is implemented using a LinkedHashMap to
 * maintain <br />
 * the natural ordering of transactions in a key value store.
 */
public class InMemoryPaymentService implements PaymentService {

	private Map<UUID, Transaction> transactionMap;

	public InMemoryPaymentService() {
		transactionMap = new LinkedHashMap<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UUID execute(Account payer, Account payee, double amount) {
		Transaction transaction = new Transaction(UUID.randomUUID(), payer, payee, amount);
		transactionMap.put(transaction.getId(), transaction);
		return transaction.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Transaction view(UUID transactionId) throws TxnNotFoundException {
		Transaction transaction = transactionMap.get(transactionId);
		if (transaction == null) {
			throw new TxnNotFoundException();
		}
		return transaction;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UUID refund(UUID transactionId, double amount)
			throws TxnNotFoundException, RefundExceedsExecuteValueException {
		Transaction transaction = view(transactionId);
		amount = negateAmount(amount);
		if (-amount > transaction.getAmount() || amount == 0d) {
			throw new RefundExceedsExecuteValueException();
		}
		return execute(transaction.getPayee(), transaction.getPayer(), amount);
	}

	/**
	 * Normalizes amount to negative if positive
	 * @param amount
	 * @return
	 */
	private double negateAmount(double amount) {
		return amount > 0d ? -amount : amount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getCumulativeTxnValue() {
		return transactionMap.values().stream().mapToDouble(Transaction::getAmount).sum();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getCumulativePayerNames() {
		List<String> payerNames = transactionMap.values().stream().map(e -> e.getPayer().getName())
				.collect(Collectors.toList());

		List<String> cumulativePayerNames = new ArrayList<>();
		for (String name : payerNames) {
			if (Collections.frequency(payerNames, name) > 1) {
				cumulativePayerNames.add(name);
			}
		}
		return cumulativePayerNames;
	}

}
