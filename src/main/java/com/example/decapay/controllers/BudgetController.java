package com.example.decapay.controllers;

import com.example.decapay.pojos.responseDtos.BudgetRest;
import com.example.decapay.pojos.requestDtos.CreateBudgetRequest;
import com.example.decapay.pojos.responseDtos.CreateBudgetResponse;
import com.example.decapay.services.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<BudgetRest>> getBudgets(@RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<BudgetRest> budgets = budgetService.getBudgets(page, limit);
        return ResponseEntity.ok(budgets);
    }

    @PostMapping
    public ResponseEntity<CreateBudgetResponse> createBudget(@Valid @RequestBody CreateBudgetRequest request){
        return new ResponseEntity<>(budgetService.createBudget(request), HttpStatus.CREATED);
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<CreateBudgetResponse> fetchBudget(@PathVariable(name = "budgetId")Long budgetId){
        return ResponseEntity.ok(budgetService.fetchBudgetById(budgetId));
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<String> deleteBudget(@PathVariable Long budgetId) {
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.ok("Budget Deleted Successfully");
    }
}