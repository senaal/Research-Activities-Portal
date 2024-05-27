package com.uni.research_portal.repository;

import com.uni.research_portal.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Integer> {
    VerificationCode findByEmailAndCode(String email, String code);
}
