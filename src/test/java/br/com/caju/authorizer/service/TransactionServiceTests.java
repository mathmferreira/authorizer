package br.com.caju.authorizer.service;

import br.com.caju.authorizer.domain.model.Account;
import br.com.caju.authorizer.domain.model.CashBalance;
import br.com.caju.authorizer.domain.model.FoodBalance;
import br.com.caju.authorizer.domain.model.Transaction;
import br.com.caju.authorizer.enums.BalanceType;
import br.com.caju.authorizer.exception.InsufficientBalanceException;
import br.com.caju.authorizer.exception.InvalidAmountException;
import br.com.caju.authorizer.repository.TransactionRepository;
import br.com.caju.authorizer.util.BigDecimalUtils;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {

    @Mock
    private TransactionRepository repository;

    @Mock
    private AccountService accountService;

    @Mock
    private BenefitBalanceService balanceService;

    @InjectMocks
    private TransactionService service;

    private FoodBalance foodBalance;

    @BeforeEach
    public void setup() {
        foodBalance = new FoodBalance();
        foodBalance.setId(1L);
        foodBalance.setBalanceType(BalanceType.FOOD);
        foodBalance.setAmount(NumberUtils.toScaledBigDecimal(500.00));
    }

    @Test
    public void givenValidTransaction_whenAuthorizeTransaction_thenTransactionIsCreated() {
        var transaction = Transaction.builder().mcc(5412).merchant("Test")
                .totalAmount(NumberUtils.toScaledBigDecimal(100.00)).build();
        var expectedBalance = NumberUtils.toScaledBigDecimal(400.00);

        when(repository.save(any())).thenReturn(new Transaction());
        when(accountService.getReferenceById(anyLong())).thenReturn(new Account());
        when(balanceService.findByAccountAndMcc(any(), anyInt())).thenReturn(foodBalance);
        doAnswer(it -> {
            var newAmount = foodBalance.getAmount().subtract(transaction.getTotalAmount());
            foodBalance.setAmount(newAmount);
            return null;
        }).when(balanceService).debitBalance(foodBalance, transaction.getTotalAmount());

        assertDoesNotThrow(() -> service.authorizeTransaction(transaction, "1"));

        verify(accountService).getReferenceById(anyLong());
        verify(balanceService).findByAccountAndMcc(any(), anyInt());
        verify(balanceService).debitBalance(any(), any());
        verify(repository).save(any());
        assertTrue(BigDecimalUtils.equals(expectedBalance, foodBalance.getAmount()));
    }

    @ParameterizedTest(name = "accountId = {1}, transaction = {0}")
    @MethodSource("provideNullTransactionAndAccountId")
    public void givenNullTransactionOrAccountId_whenAuthorizeTransaction_thenThrowIllegalArgumentException(Transaction transaction, String accountId) {
        assertThrows(IllegalArgumentException.class, () -> service.authorizeTransaction(transaction, accountId));
        verifyNoInteractions(accountService, balanceService, repository);
    }

    @Test
    public void givenAccountId_whenAuthorizeTransaction_thenThrowEntityNotFoundException() {
        when(accountService.getReferenceById(anyLong())).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> service.authorizeTransaction(new Transaction(), "99"));
        verify(accountService).getReferenceById(anyLong());
        verifyNoInteractions(balanceService, repository);
    }

    @Test
    public void givenInvalidMcc_whenAuthorizeTransaction_thenTransactionIsCreated() {
        var transaction = Transaction.builder().mcc(9999).merchant("Test")
                .totalAmount(NumberUtils.toScaledBigDecimal(100.00)).build();

        when(accountService.getReferenceById(anyLong())).thenReturn(new Account());
        when(balanceService.findByAccountAndMcc(any(), anyInt())).thenReturn(new CashBalance());
        doNothing().when(balanceService).debitBalance(any(), any());

        assertDoesNotThrow(() -> service.authorizeTransaction(transaction, "1"));

        verify(accountService).getReferenceById(anyLong());
        verify(balanceService).findByAccountAndMcc(any(), anyInt());
        verify(balanceService).debitBalance(any(CashBalance.class), any());
        verify(repository).save(any());
    }

    @Test
    public void givenInsufficientBalance_whenAuthorizeTransaction_thenThrowInsufficientBalanceException() {
        var transaction = Transaction.builder().mcc(5412).merchant("Test")
                .totalAmount(NumberUtils.toScaledBigDecimal(1000.00)).build();

        when(accountService.getReferenceById(anyLong())).thenReturn(new Account());
        when(balanceService.findByAccountAndMcc(any(), anyInt())).thenReturn(foodBalance);
        doThrow(InsufficientBalanceException.class).when(balanceService).debitBalance(any(), any());

        assertThrows(InsufficientBalanceException.class, () -> service.authorizeTransaction(transaction, "1"));

        verify(accountService).getReferenceById(anyLong());
        verify(balanceService).findByAccountAndMcc(any(), anyInt());
        verify(balanceService).debitBalance(any(), any());
    }

    @Test
    public void givenInvalidAmount_whenAuthorizeTransaction_thenThrowInvalidAmountException() {
        var transaction = Transaction.builder().mcc(5412).merchant("Test")
                .totalAmount(NumberUtils.toScaledBigDecimal(-100.00)).build();

        when(accountService.getReferenceById(anyLong())).thenReturn(new Account());
        when(balanceService.findByAccountAndMcc(any(), anyInt())).thenReturn(foodBalance);
        doThrow(InvalidAmountException.class).when(balanceService).debitBalance(any(), any());

        assertThrows(InvalidAmountException.class, () -> service.authorizeTransaction(transaction, "1"));

        verify(accountService).getReferenceById(anyLong());
        verify(balanceService).findByAccountAndMcc(any(), anyInt());
        verify(balanceService).debitBalance(any(), any());
    }

    private static Stream<Arguments> provideNullTransactionAndAccountId() {
        return Stream.of(
                Arguments.of(null, "1"),
                Arguments.of(new Transaction(), null),
                Arguments.of(new Transaction(), ""),
                Arguments.of(null, null)
        );
    }

}
