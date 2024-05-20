package com.uni.research_portal.controller;

import com.uni.research_portal.dto.AdminRequestDto;
import com.uni.research_portal.dto.AdminResponseDto;
import com.uni.research_portal.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin")
public class AdminController {
    @Autowired
    AdminService adminService;

    @PostMapping("/login")
    public AdminResponseDto adminLogin(@RequestBody AdminRequestDto dto){

        return adminService.loginAdmin(dto);
    }
}

