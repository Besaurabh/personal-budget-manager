package com.pbm.controller;

import com.pbm.entity.Expense;
import com.pbm.entity.User;
import com.pbm.repository.ExpenseRepository;
import com.pbm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/expense")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    // Show expense add form
    @GetMapping("/add")
    public String showExpenseForm(Model model) {
        model.addAttribute("expense", new Expense());
        return "add-expense"; // Thymeleaf template name
    }

    // Save expense after form submission
    @PostMapping("/add")
    public String saveExpense(@ModelAttribute Expense expense,
                              @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                                  .orElseThrow(() -> new RuntimeException("User not found"));
        expense.setUser(user);
        expenseRepository.save(expense);
        return "redirect:/home";
    }

    // Show history of expenses
    @GetMapping("/history")
    public String viewExpenseHistory(Model model,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                                  .orElseThrow(() -> new RuntimeException("User not found"));
        List<Expense> expenses = expenseRepository.findByUser(user);
        model.addAttribute("expenses", expenses);
        model.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd-MM-yyyy")); // ✅ used in Thymeleaf
        return "expense-history"; // template
    }
}
