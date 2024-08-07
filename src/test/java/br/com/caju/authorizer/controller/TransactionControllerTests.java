package br.com.caju.authorizer.controller;

import br.com.caju.authorizer.domain.vo.TransactionVO;
import br.com.caju.authorizer.exception.InsufficientBalanceException;
import br.com.caju.authorizer.exception.InvalidAmountException;
import br.com.caju.authorizer.records.TransactionResponseVO;
import br.com.caju.authorizer.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@WebMvcTest(TransactionController.class)
public class TransactionControllerTests {

    @MockBean
    private TransactionService service;

    @Autowired
    private MockMvc mockMvc;

    private String basePath;
    private ObjectMapper mapper;
    private TransactionResponseVO successResponse;
    private TransactionResponseVO deniedResponse;
    private TransactionResponseVO errorResponse;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        basePath = "/v1/transaction";
        mapper = JsonMapper.builder().findAndAddModules().build();
        successResponse = new TransactionResponseVO("00");
        deniedResponse = new TransactionResponseVO("51");
        errorResponse = new TransactionResponseVO("07");
    }

    @Test
    public void givenValidRequest_whenAuthorizeTransaction_thenReturnCode00() throws JsonProcessingException {
        var request = TransactionVO.builder().account("123").merchant("Test")
                .mcc(5411).totalAmount(NumberUtils.toScaledBigDecimal(100.00)).build();
        doNothing().when(service).authorizeTransaction(any(), anyString());

        given().contentType(ContentType.JSON).body(request)
                .when().post(basePath + "/authorize")
                .then().log().ifValidationFails()
                .status(HttpStatus.OK)
                .body(notNullValue(), not(emptyString()))
                .body(equalTo(mapper.writeValueAsString(successResponse)));
    }

    @Test
    public void givenInsufficientBalance_whenAuthorizeTransaction_thenReturnCode51() throws JsonProcessingException {
        var request = TransactionVO.builder().account("123").merchant("Test")
                .mcc(5411).totalAmount(NumberUtils.toScaledBigDecimal(100.00)).build();
        doThrow(InsufficientBalanceException.class).when(service).authorizeTransaction(any(), anyString());

        given().contentType(ContentType.JSON).body(request)
                .when().post(basePath + "/authorize")
                .then().log().ifValidationFails()
                .status(HttpStatus.OK)
                .body(notNullValue(), not(emptyString()))
                .body(equalTo(mapper.writeValueAsString(deniedResponse)));
    }

    @Test
    public void givenInvalidRequest_whenAuthorizeTransaction_thenReturnCode07() throws JsonProcessingException {
        var request = TransactionVO.builder().account("123").build();

        given().contentType(ContentType.JSON).body(request)
                .when().post(basePath + "/authorize")
                .then().log().ifValidationFails()
                .status(HttpStatus.OK)
                .body(notNullValue(), not(emptyString()))
                .body(equalTo(mapper.writeValueAsString(errorResponse)));
        verifyNoInteractions(service);
    }

    @Test
    public void givenAccountNotFound_whenAuthorizeTransaction_thenReturnCode07() throws JsonProcessingException {
        var request = TransactionVO.builder().account("123").merchant("Test")
                .mcc(5411).totalAmount(NumberUtils.toScaledBigDecimal(100.00)).build();
        doThrow(EntityNotFoundException.class).when(service).authorizeTransaction(any(), anyString());

        given().contentType(ContentType.JSON).body(request)
                .when().post(basePath + "/authorize")
                .then().log().ifValidationFails()
                .status(HttpStatus.OK)
                .body(notNullValue(), not(emptyString()))
                .body(equalTo(mapper.writeValueAsString(errorResponse)));
    }

    @Test
    public void givenInvalidMcc_whenAuthorizeTransaction_thenReturnCode00() throws JsonProcessingException {
        var request = TransactionVO.builder().account("123").merchant("Test")
                .mcc(9999).totalAmount(NumberUtils.toScaledBigDecimal(100.00)).build();
        doNothing().when(service).authorizeTransaction(any(), anyString());

        given().contentType(ContentType.JSON).body(request)
                .when().post(basePath + "/authorize")
                .then().log().ifValidationFails()
                .status(HttpStatus.OK)
                .body(notNullValue(), not(emptyString()))
                .body(equalTo(mapper.writeValueAsString(successResponse)));
    }

    @Test
    public void givenInvalidAmount_whenAuthorizeTransaction_thenReturnCode07() throws JsonProcessingException {
        var request = TransactionVO.builder().account("123").merchant("Test")
                .mcc(9999).totalAmount(NumberUtils.toScaledBigDecimal(-100.00)).build();
        doThrow(InvalidAmountException.class).when(service).authorizeTransaction(any(), anyString());

        given().contentType(ContentType.JSON).body(request)
                .when().post(basePath + "/authorize")
                .then().log().ifValidationFails()
                .status(HttpStatus.OK)
                .body(notNullValue(), not(emptyString()))
                .body(equalTo(mapper.writeValueAsString(errorResponse)));
    }

}
