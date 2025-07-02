package com.pbm.controller;

import com.pbm.entity.Income;
import com.pbm.entity.User;
import com.pbm.repository.IncomeRepository;
import com.pbm.repository.UserRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/income")
public class IncomeController {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    // Show form to add new income
    @GetMapping("/add")
    public String showIncomeForm(Model model) {
        model.addAttribute("income", new Income());
        return "add-income"; // Make sure templates/add-income.html exists
    }

    // Save new income
    @PostMapping("/add")
    public String saveIncome(@ModelAttribute("income") Income income,
                             @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return "redirect:/login"; // In case not logged in
        }

        User user = userRepository.findByUsername(userDetails.getUsername())
                                  .orElseThrow(() -> new RuntimeException("User not found"));
        income.setUser(user);
        incomeRepository.save(income);

        return "redirect:/home";
    }

    // View income history
    @GetMapping("/history")
    public String viewIncomeHistory(Model model,
                                    @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return "redirect:/login"; // Safety check
        }

        User user = userRepository.findByUsername(userDetails.getUsername())
                                  .orElseThrow(() -> new RuntimeException("User not found"));
        List<Income> incomes = incomeRepository.findByUser(user);
        model.addAttribute("incomes", incomes);

        return "income-history"; // Should map to templates/income-history.html
    }
}
