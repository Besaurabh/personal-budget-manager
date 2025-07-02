package com.pbm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "income")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;          // e.g., "Job", "Freelance"
    private String category;        // ✅ Add this field
    private double amount;
    private LocalDate date;

    private String description; // ✅ add this
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
