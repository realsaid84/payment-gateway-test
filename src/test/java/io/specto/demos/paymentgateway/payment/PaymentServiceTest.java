package io.specto.demos.paymentgateway.payment;

import io.specto.demos.paymentgateway.account.Account;
import io.specto.demos.paymentgateway.account.RefundExceedsExecuteValueException;
import io.specto.demos.paymentgateway.account.Transaction;
import io.specto.demos.paymentgateway.account.TxnNotFoundException;
import io.specto.demos.paymentgateway.payment.impl.InMemoryPaymentService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class PaymentServiceTest {

	private static final UUID INVALID_TRANSACTION_UIID = UUID.fromString("7e8bdb3c-1538-4b3c-a99e-aae3e6df6540");

	private PaymentService paymentService;

	private Account payer = new Account("benji", "benji@specto.io");
	private Account payee = new Account("daniel", "daniel@specto.io");

	@Before
	public void setUp() throws Exception {
		paymentService = new InMemoryPaymentService();
	}

	@After
	public void tearDown() throws Exception {
		paymentService = null;
	}

	@Test
	public void executeReturnsTxnId() {
		UUID txnId = paymentService.execute(payer, payee, 4.5d);

		assertThat(txnId, is(notNullValue()));
	}

	@Test
	public void viewTxnReturnsAssociatedExecuteData() throws TxnNotFoundException {
		UUID txnId = paymentService.execute(payer, payee, 4.5d);
		Transaction transaction = paymentService.view(txnId);
		
		assertThat(transaction.getAmount(), is(4.5d));
	}

	@Test(expected = TxnNotFoundException.class)
	public void viewTxnNotFound() throws TxnNotFoundException {
		paymentService.view(INVALID_TRANSACTION_UIID);
	}

	@Test
	public void validRefundReturnsNewTxn() throws TxnNotFoundException, RefundExceedsExecuteValueException {
		UUID txnId = paymentService.execute(payer, payee, 4.5d);
		UUID refundTxnId = paymentService.refund(txnId, -2.5d);
		
		assertThat(refundTxnId, is(notNullValue()));
	}

	@Test(expected = TxnNotFoundException.class)
	public void invalidRefundThrowsTxnNotFound() throws TxnNotFoundException, RefundExceedsExecuteValueException {
		paymentService.refund(INVALID_TRANSACTION_UIID, -3.5d);
	}

	@Test
	public void validPositiveRefundReturnsCorrectTxn() throws TxnNotFoundException, RefundExceedsExecuteValueException {
		UUID txnId = paymentService.execute(payer, payee, 4.5d);
		UUID refundTxnId = paymentService.refund(txnId, 2.5d);
		
		assertThat(refundTxnId, is(notNullValue()));
		assertTrue(paymentService.view(refundTxnId).getAmount() == -2.5d);
	}

	@Test(expected = RefundExceedsExecuteValueException.class)
	public void invalidRefundThrowsREEVE() throws TxnNotFoundException, RefundExceedsExecuteValueException {
		UUID txnId = paymentService.execute(payer, payee, 4.5d);
		Transaction transaction = paymentService.view(txnId);
		
		paymentService.refund(transaction.getId(), -6.5d);
	}

	@Test(expected = RefundExceedsExecuteValueException.class)
	public void zeroRefundThrowsREEVE() throws TxnNotFoundException, RefundExceedsExecuteValueException {
		UUID txnId = paymentService.execute(payer, payee, 4.5d);
		Transaction transaction = paymentService.view(txnId);
		
		paymentService.refund(transaction.getId(), 0d);
	}

	@Test
	public void cumulativeTxnValueIncRefundsIsCorrect()
			throws TxnNotFoundException, RefundExceedsExecuteValueException {
		UUID txnId = paymentService.execute(payer, payee, 4.5d);
		paymentService.refund(txnId, -2.5d);
		paymentService.execute(payer, payee, 6.0d);

		assertThat(paymentService.getCumulativeTxnValue(), is(8.0d));
	}

	@Test
	public void noCumulativeTxn() throws TxnNotFoundException, RefundExceedsExecuteValueException {
		assertThat(paymentService.getCumulativeTxnValue(), is(0d));
	}

	@Test
	public void cumulativePayersIsCorrect() throws TxnNotFoundException, RefundExceedsExecuteValueException {
		UUID txnId = paymentService.execute(payer, payee, 4.5d);
		paymentService.refund(txnId, -2.5d);
		paymentService.execute(payer, payee, 6.0d);

		List<String> names = Arrays.asList("benji", "benji");

		assertThat(paymentService.getCumulativePayerNames(), is(names));
	}

	@Test
	public void noCumulativePayers() throws TxnNotFoundException, RefundExceedsExecuteValueException {
		paymentService.execute(payer, payee, 4.5d);
		paymentService.execute(payee, payer, 6.0d);
		
		assertTrue(paymentService.getCumulativePayerNames().isEmpty());
	}
}
