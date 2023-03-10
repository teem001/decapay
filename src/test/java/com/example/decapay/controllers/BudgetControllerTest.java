package com.example.decapay.controllers;

import com.example.decapay.configurations.security.CustomUserDetailService;
import com.example.decapay.configurations.security.JwtAuthFilter;
import com.example.decapay.pojos.responseDtos.BudgetRest;
import com.example.decapay.pojos.responseDtos.LineItemRest;
import com.example.decapay.services.BudgetService;
import com.example.decapay.pojos.requestDtos.CreateBudgetRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BudgetController.class)
@AutoConfigureMockMvc(addFilters = false)
class BudgetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    BudgetService budgetService;

    @Autowired
    ObjectMapper objectMapper;


    @MockBean
    private CustomUserDetailService customUserDetailService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;


    @Test
    void testCreateBudget() throws Exception {

        CreateBudgetRequest budgetRequest = new CreateBudgetRequest();
        String budgetJson = objectMapper.writeValueAsString(budgetRequest);

        mockMvc.perform(post("/api/v1/budgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(budgetJson))
                        .andExpect(status().isCreated());
    }

    @Test
    void testFetchBudget() throws Exception {

        long budgetId = 2L;

        mockMvc.perform(get("/api/v1/budgets/{budgetId}", budgetId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Controller Test to a all Budget for particular User")
    void getBudgets() throws Exception {
        LineItemRest lineItemRest = new LineItemRest();
        lineItemRest.setProjectedAmount(new BigDecimal(800));
        lineItemRest.setAmountSpentSoFar(new BigDecimal(750));
        lineItemRest.setPercentageSpentSoFar(new BigDecimal("0.67"));

        List<LineItemRest> lineItems = new ArrayList<>();
        lineItems.add(lineItemRest);

        List<BudgetRest> budgetRests = new ArrayList<>();
        BudgetRest budgetRest = new BudgetRest();
        budgetRest.setAmount(new BigDecimal(5000));
        budgetRest.setTotalAmountSpent(new BigDecimal(4000));
        budgetRest.setPercentage(new BigDecimal("0.8"));
        budgetRest.setLineItemRests(lineItems);

        budgetRests.add(budgetRest);

        when(budgetService.getBudgets(eq(0), eq(10))).thenReturn(budgetRests);

        String urlPath = "/api/v1/budgets";

        MvcResult mvcResult = mockMvc.perform(
                        get(urlPath))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
                .andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(budgetService, times(1)).getBudgets(0, 10);

    }

    @Test
    void testDeleteBudget() throws Exception {

        long budgetId = 1L;

        mockMvc.perform(delete("/api/v1/budgets/{budgetId}", budgetId))
                .andExpect(status().isOk());
    }
}