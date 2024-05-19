package com.uni.research_portal.controller;

import com.uni.research_portal.dto.CreateDepartmentDto;
import com.uni.research_portal.model.Department;
import com.uni.research_portal.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static com.uni.research_portal.util.Jwt.validateToken;

@RestController
@RequestMapping("/department")
public class DepartmentController {
    @Autowired
    DepartmentService departmentService;

    @PostMapping("/")
    public Department createDepartment(@RequestBody CreateDepartmentDto dto,
                                       @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token){
        if(validateToken(token.substring(7))){
            return departmentService.createDepartment(dto, token.substring(7));
        }
        else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable int id,
                                                   @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        if (validateToken(token.substring(7))) {
            return departmentService.deleteDepartment(id, token.substring(7));
        } else {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/")
    public List<Department> getDepartments(){
        return departmentService.getDepartments();
    }

}
