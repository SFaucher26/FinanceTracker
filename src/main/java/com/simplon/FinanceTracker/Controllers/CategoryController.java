package com.simplon.FinanceTracker.Controllers;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.simplon.FinanceTracker.Models.Category;
import com.simplon.FinanceTracker.Models.User;
import com.simplon.FinanceTracker.Dto.CategoryDto;
import com.simplon.FinanceTracker.Repositories.CategoryRepository;
import com.simplon.FinanceTracker.Repositories.UserRepository;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryController(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }
    @PostMapping(value ="/category", produces = MediaType.APPLICATION_JSON_VALUE)
    public Category createCategory(@RequestBody CategoryDto categoryDto, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName()).orElseThrow();

        Category category = Category.builder()
                .name(categoryDto.getName())
                .color(categoryDto.getColor())
                .limit(categoryDto.getLimit())
                .owner(currentUser)
                .build();

        return categoryRepository.save(category);
    }
}
