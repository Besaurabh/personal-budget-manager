package com.pbm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Random;

@Controller
public class SavingsController {

	private static final List<String> TIPS = List.of(
		    "💡 Save at least 20% of your income each month.",
		    "🛒 Track your spending to identify unnecessary expenses.",
		    "📈 Use the 50/30/20 budgeting rule: Needs/Wants/Savings.",
		    "🧾 Set monthly saving goals and automate transfers.",
		    "🏦 Open a high-interest savings account.",
		    "🚫 Avoid impulse purchases — wait 24 hours before buying.",
		    "🧠 Review your subscriptions — cancel unused services.",
		    "📊 Use analytics to see where you can reduce spending.",
		    
		    // New tips added below:
		    "🔄 Set up auto-debits to move money into your savings every payday.",
		    "📅 Review your budget every month to stay on track.",
		    "🛠️ Do minor repairs yourself instead of hiring help when possible.",
		    "🍽️ Cook more meals at home to reduce food expenses.",
		    "💳 Pay credit card bills on time to avoid interest and penalties.",
		    "🧳 Plan vacations during off-season to save money.",
		    "📦 Buy in bulk and use discount coupons for groceries.",
		    "👕 Buy quality over quantity — it lasts longer and saves money.",
		    "🧾 Keep digital receipts and analyze your purchases monthly.",
		    "🔔 Set savings reminders or alerts to stay focused on goals."
		);


    @GetMapping("/savings/tips")
    public String showSavingsTip(Model model) {
        // Pick a random tip
        String tip = TIPS.get(new Random().nextInt(TIPS.size()));
        model.addAttribute("tip", tip);
        return "savings-tip"; // Return the Thymeleaf view name
    }
}
