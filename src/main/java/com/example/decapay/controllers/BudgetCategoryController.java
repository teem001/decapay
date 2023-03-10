package com.example.decapay.controllers;

import com.example.decapay.pojos.requestDtos.BudgetCategoryRequest;
import com.example.decapay.pojos.responseDtos.BudgetCategoryResponse;
import com.example.decapay.services.BudgetCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/budgets/category")

public class BudgetCategoryController {

    private final BudgetCategoryService budgetCategoryService;

    @PostMapping("/create")
    public ResponseEntity<String> createBudgetCategory(@Valid @RequestBody BudgetCategoryRequest budgetCategoryRequest){
        budgetCategoryService.createBudgetCategory(budgetCategoryRequest);
        return ResponseEntity.ok("Budget category created");
    }
    @PutMapping("/update/{budgetCategoryId}")
    public ResponseEntity<String> updateBudgetCategory(@Valid @PathVariable Long budgetCategoryId, @RequestBody BudgetCategoryRequest budgetCategoryRequest)
    {
      BudgetCategoryResponse  budgetCategoryResponse=  budgetCategoryService.updateBudgetCategory(budgetCategoryId,budgetCategoryRequest);
      return  ResponseEntity.ok("Budget category updated");
    }
}
