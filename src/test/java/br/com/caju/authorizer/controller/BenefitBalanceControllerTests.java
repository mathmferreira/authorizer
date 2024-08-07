package br.com.caju.authorizer.controller;

import br.com.caju.authorizer.domain.model.FoodBalance;
import br.com.caju.authorizer.domain.model.MealBalance;
import br.com.caju.authorizer.domain.vo.BenefitBalanceVO;
import br.com.caju.authorizer.enums.BalanceType;
import br.com.caju.authorizer.service.BenefitBalanceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@WebMvcTest(BenefitBalanceController.class)
public class BenefitBalanceControllerTests {

    @MockBean
    private BenefitBalanceService service;

    @Autowired
    private MockMvc mockMvc;

    private String basePath;
    private ObjectMapper mapper;
    private FoodBalance foodBalance;
    private MealBalance mealBalance;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        basePath = "/v1/benefits/balance";
        mapper = JsonMapper.builder().findAndAddModules().build();
        foodBalance = new FoodBalance();
        foodBalance.setBalanceType(BalanceType.FOOD);
        foodBalance.setAmount(NumberUtils.toScaledBigDecimal(500.00));
        mealBalance = new MealBalance();
        mealBalance.setAmount(NumberUtils.toScaledBigDecimal(300.00));
    }

    @Test
    public void givenAccountId_whenFindByAccount_thenReturnBenefitBalanceList() throws JsonProcessingException {
        var balances = List.of(foodBalance, mealBalance);
        var expected = balances.stream().map(it -> new BenefitBalanceVO(it.getBalanceType(), it.getAmount())).toList();
        when(service.findByAccount(anyString())).thenReturn(balances);

        given().when().get(basePath + "/1")
                .then().log().ifValidationFails()
                .status(HttpStatus.OK)
                .body(notNullValue(), not(emptyString()))
                .body(equalTo(mapper.writeValueAsString(expected)));
    }

    @Test
    public void givenAccountId_whenFindByAccount_thenReturnEmptyList() {
        when(service.findByAccount(anyString())).thenReturn(Collections.emptyList());
        given().when().get(basePath + "/99")
                .then().log().ifValidationFails()
                .status(HttpStatus.OK)
                .body(notNullValue(), not(emptyString()))
                .body(equalTo("[]"));
    }

}
