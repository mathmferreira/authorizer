package br.com.caju.authorizer.service;

import br.com.caju.authorizer.domain.model.*;
import br.com.caju.authorizer.enums.BalanceType;
import br.com.caju.authorizer.exception.InsufficientBalanceException;
import br.com.caju.authorizer.exception.InvalidAmountException;
import br.com.caju.authorizer.repository.BenefitBalanceRepository;
import br.com.caju.authorizer.repository.CashBalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BenefitBalanceServiceTests {

    @Mock
    private BenefitBalanceRepository repository;

    @Mock
    private CashBalanceRepository cashBalanceRepository;

    @Mock
    private MerchantService merchantService;

    @InjectMocks
    private BenefitBalanceService service;

    @Test
    public void givenValidAccountAndMcc_whenFindByAccountAndBalanceType_thenReturnBenefitBalance() {
        var foodMerchant = Merchant.builder().balanceType(BalanceType.FOOD).build();
        var mealMerchant = Merchant.builder().balanceType(BalanceType.MEAL).build();
        when(repository.findByAccountAndBalanceType(any(), eq(BalanceType.FOOD))).thenReturn(Optional.of(new FoodBalance()));
        when(repository.findByAccountAndBalanceType(any(), eq(BalanceType.MEAL))).thenReturn(Optional.of(new MealBalance()));
        when(merchantService.findByName(anyString())).thenReturn(Optional.of(foodMerchant), Optional.of(mealMerchant));

        var balance = assertDoesNotThrow(() -> service.findByAccountAndBalanceType(new Account(), "PADARIA DO ZE     BRASIL"));
        assertNotNull(balance);
        assertInstanceOf(FoodBalance.class, balance);

        balance = assertDoesNotThrow(() -> service.findByAccountAndBalanceType(new Account(), "UBER EATS    BRASIL"));
        assertNotNull(balance);
        assertInstanceOf(MealBalance.class, balance);
    }

    @Test
    public void givenInvalidMcc_whenFindByAccountAndBalanceType_thenReturnCashBalance() {
        when(repository.findByAccountAndBalanceType(any(), any())).thenReturn(Optional.of(new CashBalance()));
        when(merchantService.findByName(anyString())).thenReturn(Optional.of(new Merchant()));
        var actual = assertDoesNotThrow(() -> service.findByAccountAndBalanceType(new Account(), "UBER EATS    BRASIL"));
        verify(repository).findByAccountAndBalanceType(any(), any());
        assertNotNull(actual);
        assertInstanceOf(CashBalance.class, actual);
    }

    @Test
    public void givenNullMcc_whenFindByAccountAndBalanceType_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.findByAccountAndBalanceType(new Account(), null));
        verifyNoInteractions(repository);
    }

    @Test
    public void givenNotFoundBalance_whenFindByAccountAndBalanceType_thenThrowEntityNotFoundException() {
        when(repository.findByAccountAndBalanceType(any(), any())).thenReturn(Optional.empty());
        when(merchantService.findByName(anyString())).thenReturn(Optional.of(new Merchant()));
        assertThrows(EntityNotFoundException.class, () -> service.findByAccountAndBalanceType(new Account(), "UBER EATS    BRASIL"));
    }

    @Test
    public void givenAccountId_whenFindByAccount_thenReturnBenefitBalanceList() {
        var balances = List.of(new FoodBalance(), new MealBalance());
        when(repository.findByAccount(anyString())).thenReturn(balances);

        var actual = assertDoesNotThrow(() -> service.findByAccount("123"));

        assertTrue(CollectionUtils.isNotEmpty(actual));
        assertArrayEquals(balances.toArray(), actual.toArray());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " " })
    public void givenNullEmptyOrBlankAccountId_whenFindByAccount_thenThrowIllegalArgumentException(String accountId) {
        assertThrows(IllegalArgumentException.class, () -> service.findByAccount(accountId));
        verifyNoInteractions(repository);
    }

    @Test
    public void givenSufficientBalance_whenDebitBalance_thenSaveNewBalance() {
        var balance = new FoodBalance();
        balance.setAmount(NumberUtils.toScaledBigDecimal(500.00));

        when(repository.save(any())).thenReturn(new FoodBalance());

        assertDoesNotThrow(() -> service.debitBalance(balance, BigDecimal.valueOf(100)));

        verify(repository).save(any());
        assertEquals(400.0, balance.getAmount().doubleValue());
    }

    @Test
    public void givenNullBalance_whenDebitBalance_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.debitBalance(null, BigDecimal.TEN));
        verifyNoInteractions(repository);
    }

    @Test
    public void givenNegativeAmount_whenDebitBalance_thenThrowInvalidAmountException() {
        var balance = new FoodBalance();
        balance.setAmount(NumberUtils.toScaledBigDecimal(500.00));

        assertThrows(InvalidAmountException.class, () -> service.debitBalance(balance, BigDecimal.valueOf(-10)));
        verifyNoInteractions(repository);
    }

    @Test
    public void givenInsufficientBenefitAndCashBalance_whenDebitBalance_thenFallbacksAndThrowInsufficientBalanceException() {
        var balance = new FoodBalance();
        balance.setAmount(NumberUtils.toScaledBigDecimal(5.0));
        var cashBalance = new CashBalance();
        cashBalance.setAmount(NumberUtils.toScaledBigDecimal(9.0));

        when(cashBalanceRepository.findByAccount(any())).thenReturn(Optional.of(cashBalance));

        assertThrows(InsufficientBalanceException.class, () -> service.debitBalance(balance, BigDecimal.TEN));
        verify(cashBalanceRepository).findByAccount(any());
        verifyNoInteractions(repository);
    }

    @Test
    public void givenInsufficientCashBalance_whenDebitBalance_thenDontFallbackAndThrowInsufficientBalanceException() {
        var balance = new CashBalance();
        balance.setAmount(NumberUtils.toScaledBigDecimal(9.0));

        assertThrows(InsufficientBalanceException.class, () -> service.debitBalance(balance, BigDecimal.TEN));
        verifyNoInteractions(cashBalanceRepository);
        verifyNoInteractions(repository);
    }

    @Test
    public void givenInsufficientBenefitBalance_whenDebitBalance_thenFallbacksAndSaveNewCashBalance() {
        var balance = new FoodBalance();
        balance.setAmount(NumberUtils.toScaledBigDecimal(5.0));
        var cashBalance = new CashBalance();
        cashBalance.setAmount(NumberUtils.toScaledBigDecimal(100.0));

        when(cashBalanceRepository.findByAccount(any())).thenReturn(Optional.of(cashBalance));

        assertDoesNotThrow(() -> service.debitBalance(balance, BigDecimal.TEN));
        verify(cashBalanceRepository).findByAccount(any());
        verify(repository).save(any());
        assertEquals(5.0, balance.getAmount().doubleValue());
        assertEquals(90.0, cashBalance.getAmount().doubleValue());
    }

}
