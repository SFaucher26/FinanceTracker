package com.simplon.FinanceTracker.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.simplon.FinanceTracker.Models.Category;
import com.simplon.FinanceTracker.Models.User;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByIdAndOwner(Long id, User owner);
    List<Category> findByOwner(User owner);
}

