package com.pbm.controller;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pbm.entity.Expense;
import com.pbm.entity.User;
import com.pbm.repository.ExpenseRepository;
import com.pbm.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AnalyticsController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    // 📊 Show analytics page
    @GetMapping("/analytics")
    public String showAnalytics(Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        List<Expense> expenses = expenseRepository.findByUser(user);

        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense expense : expenses) {
            categoryTotals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }

        model.addAttribute("categoryTotals", categoryTotals);
        return "analytics";
    }

    // 📥 Export analytics to PDF
    @GetMapping("/analytics/pdf")
    public void exportAnalyticsPdf(HttpServletResponse response,
                                   @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        List<Expense> expenses = expenseRepository.findByUser(user);

        Map<String, Double> categoryTotals = new HashMap<>();
        double grandTotal = 0;

        for (Expense expense : expenses) {
            categoryTotals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
            grandTotal += expense.getAmount();
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=analytics-report.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Expense Analytics Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph("Generated on: " + LocalDate.now()));
        document.add(new Paragraph(" ")); // Spacer

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        table.addCell("Category");
        table.addCell("Total Amount (₹)");

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            table.addCell(entry.getKey());
            table.addCell(String.format("%.2f", entry.getValue()));
        }

        table.addCell("TOTAL");
        table.addCell(String.format("%.2f", grandTotal));

        document.add(table);
        document.close();
    }
}
