package com.leecampus.controller;

import com.leecampus.dto.*;
import com.leecampus.service.StudentService;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/leecampus")
//@CrossOrigin(origins="http://localhost:5173")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }



    @PostMapping("/registerStudent")
    public StudentResponseDTO registerStudent(
            @Valid @RequestBody StudentRequestDTO dto) {
        return studentService.registerStudent(dto);
    }
    @GetMapping("/allStudents")
    public List<StudentResponseDTO> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/getStudent/{id}")
    public StudentResponseDTO getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PutMapping("/updateStudent/{id}")
    public StudentResponseDTO updateStudent(
            @PathVariable Long id,
            @RequestBody StudentRequestDTO dto) {
        return studentService.updateStudent(id, dto);
    }

    @DeleteMapping("/deleteStudent/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "Student deleted successfully";
    }

    
    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody LoginRequestDTO dto) {
        return studentService.login(dto);
    }
  
    @PostMapping("/registerBulk")
    public List<BulkResponseDTO> registerBulk(
            @RequestBody List<StudentRequestDTO> dtos) {

        return studentService.registerBulk(dtos);
    }
}