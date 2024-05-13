package com.uni.research_portal.controller;

import com.uni.research_portal.dto.CreateDepartmentDto;
import com.uni.research_portal.model.Department;
import com.uni.research_portal.model.Faculty;
import com.uni.research_portal.service.DepartmentService;
import com.uni.research_portal.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/department")
public class DepartmentController {
    @Autowired
    DepartmentService departmentService;

    @PostMapping("/")
    public Department createDepartment(@RequestBody CreateDepartmentDto dto){

        return departmentService.createDepartment(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable int id){
        return departmentService.deleteDepartment(id);
    }

    @GetMapping("/")
    public List<Department> getDepartments(){
        return departmentService.getDepartments();
    }


}
