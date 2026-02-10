package com.practise.test.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practise.test.dto.LoginRequest;
import com.practise.test.entity.Student;
import com.practise.test.service.StudentService;
import com.practise.test.util.JwtUtil;

@RestController
@RequestMapping("/api/students")
@CrossOrigin("http://localhost:5173/")
public class StudentController {

    @Autowired
    private StudentService service;

    @Autowired
    private JwtUtil jwtUtil;

    // Register Student
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Student student) {
        try {
            Student savedStudent = service.register(student);

            // Generate JWT token for immediate login after registration
            String token = jwtUtil.generateToken(student.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("student", savedStudent);
            response.put("message", "Registration successful");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Registration failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Login - Updated to return JWT token
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Use the existing login method from service
            boolean success = service.login(loginRequest.getEmail(), loginRequest.getPassword());

            if (success) {
                // Generate JWT token
                String token = jwtUtil.generateToken(loginRequest.getEmail());

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("email", loginRequest.getEmail());
                response.put("message", "Login successful");

                return ResponseEntity.ok(response);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid credentials");
                errorResponse.put("message", "Email or password is incorrect");
                return ResponseEntity.status(401).body(errorResponse);
            }

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Authentication failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Migrate passwords endpoint - REMOVE THIS AFTER MIGRATION
    @PostMapping("/migrate-passwords")
    public ResponseEntity<?> migratePasswords() {
        try {
            service.migrateAllPasswordsToBCrypt();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password migration completed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Migration failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Get All Students
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(service.getAllStudents());
    }

    // Get Student by Id
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Student>> getStudentById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getStudentById(id));
    }

    // Update Student
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Integer id, @RequestBody Student student) {
        student.setId(id);
        return ResponseEntity.ok(service.updateStudent(student));
    }

    // Delete Student
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Integer id) {
        service.deleteStudent(id);
        return ResponseEntity.ok("Student deleted successfully!");
    }
}
