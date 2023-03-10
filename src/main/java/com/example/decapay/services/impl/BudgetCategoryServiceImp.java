package com.example.decapay.services.impl;

import com.example.decapay.models.BudgetCategory;
import com.example.decapay.models.User;
import com.example.decapay.pojos.requestDtos.BudgetCategoryRequest;
import com.example.decapay.pojos.responseDtos.BudgetCategoryResponse;
import com.example.decapay.repositories.BudgetCategoryRepository;
import com.example.decapay.repositories.UserRepository;
import com.example.decapay.services.BudgetCategoryService;
import com.example.decapay.services.UserService;
import com.example.decapay.utils.UserUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class BudgetCategoryServiceImp implements BudgetCategoryService {

    private final BudgetCategoryRepository budgetCategoryRepository;
    private  final UserRepository userRepository;
    private final UserUtil userUtil;
    private final UserService userService;

    @Override
    public BudgetCategoryResponse createBudgetCategory(BudgetCategoryRequest budgetCategoryRequest) {

        String email= userUtil.getAuthenticatedUserEmail();

        User user= userRepository.findByEmail(email)
                .orElseThrow(()-> new EntityNotFoundException("User not found"));

        BudgetCategory budgetCategory= new BudgetCategory();

        budgetCategory.setName(budgetCategoryRequest.getName());
        budgetCategory.setUser(user);

        budgetCategory=budgetCategoryRepository.save(budgetCategory);

       BudgetCategoryResponse budgetCategoryResponse= BudgetCategoryResponse.mapFrom(budgetCategory);

       return budgetCategoryResponse;



    }


    @Override
    public BudgetCategoryResponse updateBudgetCategory(Long budgetCategoryId, BudgetCategoryRequest budgetCategoryRequest)
    {

        String email = userUtil
                .getAuthenticatedUserEmail();

        userService.verifyUserExists(email);

        BudgetCategory budgetCategory =
                budgetCategoryRepository
                        .findById(budgetCategoryId)
                        .orElseThrow(()-> new
                                EntityNotFoundException("Budget not found"));

        BeanUtils.copyProperties(budgetCategoryRequest, budgetCategory);

        budgetCategoryRepository.save(budgetCategory);

        return BudgetCategoryResponse.mapFrom(budgetCategory);

    }
}
