package com.uni.research_portal.service;

import com.uni.research_portal.dto.AdminRequestDto;
import com.uni.research_portal.dto.AdminResponseDto;
import com.uni.research_portal.exception.BadRequestException;
import com.uni.research_portal.exception.ResourceNotFoundException;
import com.uni.research_portal.model.Admin;
import com.uni.research_portal.repository.AdminRepository;
import com.uni.research_portal.util.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;


@Service
public class AdminService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AdminRepository adminRepository;

    public AdminResponseDto loginAdmin(AdminRequestDto admin) {
        Optional<Admin> adminOptional = adminRepository.findByEmail(admin.getEmail());
        if (adminOptional.isPresent()) {
             Admin adminDto = adminOptional.get();

            String storedPassword = adminDto.getPassword();
            String enteredPassword = admin.getPassword();

            if (enteredPassword.equals(storedPassword)) {
                String token = Jwt.generateToken(adminDto.getEmail());
                AdminResponseDto response = new AdminResponseDto();
                response.setAdmin(adminDto);
                response.setToken(token);
                return response;
            }
            else {
                throw new BadRequestException("Password is wrong");
            }
        }
        else {
            throw new ResourceNotFoundException("User not found");
        }
    }
}
