package br.com.caju.authorizer.service;

import br.com.caju.authorizer.domain.model.Account;
import br.com.caju.authorizer.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTests {

    @Mock
    private AccountRepository repository;

    @InjectMocks
    private AccountService service;

    @Test
    public void givenNullId_whenGetReferenceById_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getReferenceById(null));
        verifyNoInteractions(repository);
    }

    @Test
    public void givenValidId_whenGetReferenceById_thenReturnAccount() {
        when(repository.getReferenceById(anyLong())).thenReturn(new Account());

        var actual = assertDoesNotThrow(() -> service.getReferenceById(1L));

        verify(repository).getReferenceById(anyLong());
        assertNotNull(actual);
    }

    @Test
    public void givenInvalidId_whenGetReferenceById_thenThrowEntityNotFoundException() {
        when(repository.getReferenceById(anyLong())).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> service.getReferenceById(100L));
    }

}
