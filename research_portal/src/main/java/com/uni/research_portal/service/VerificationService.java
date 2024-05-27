package com.uni.research_portal.service;

import com.uni.research_portal.model.VerificationCode;
import com.uni.research_portal.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class VerificationService {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    public String generateVerificationCode(String email) {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);

        VerificationCode verificationCode = new VerificationCode(String.valueOf(code), email, LocalDateTime.now().plusMinutes(10));
        verificationCodeRepository.save(verificationCode);
        return String.valueOf(code);
    }

    public boolean validateVerificationCode(String email, String code) {
        VerificationCode verificationCode = verificationCodeRepository.findByEmailAndCode(email, code);
        if (verificationCode != null && verificationCode.getExpirationTime().isAfter(LocalDateTime.now())) {
            verificationCodeRepository.delete(verificationCode);
            return true;
        }
        return false;
    }
}

