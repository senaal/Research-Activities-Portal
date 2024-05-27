package com.uni.research_portal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "verification_code")
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String code;
    private String email;
    private LocalDateTime expirationTime;

    public VerificationCode(String code, String email, LocalDateTime expirationTime) {
        this.code = code;
        this.email = email;
        this.expirationTime = expirationTime;
    }
}
