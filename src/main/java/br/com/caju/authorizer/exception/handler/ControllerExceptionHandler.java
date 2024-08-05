package br.com.caju.authorizer.exception.handler;

import br.com.caju.authorizer.exception.InsufficientBalanceException;
import br.com.caju.authorizer.records.TransactionResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(InsufficientBalanceException.class)
    public TransactionResponseVO insufficientBalanceExceptionHandler(InsufficientBalanceException exception) {
        log.debug(exception.getMessage(), exception);
        return new TransactionResponseVO("51");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({ RuntimeException.class, BindException.class })
    public TransactionResponseVO genericExceptionHandler(Exception exception) {
        log.debug(exception.getMessage(), exception);
        return new TransactionResponseVO("07");
    }

}
