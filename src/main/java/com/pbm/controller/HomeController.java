package com.pbm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @PostMapping("/contact")
    public String handleContact(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String message) {
        // You can log it, save it to DB, or send an email
        System.out.println("Message from: " + name + " (" + email + ")");
        System.out.println("Message: " + message);

        return "redirect:/home?success"; // Adjust path as needed
    }

    // Optional: Map /features if not already mapped
    @GetMapping("/features")
    public String featuresPage() {
        return "home"; // This should match your features.html name
    }
}
