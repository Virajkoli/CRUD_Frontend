package com.practise.test.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.practise.test.entity.Student;
import com.practise.test.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Student register(Student student) {
        // Validate password is not null or empty
        if (student.getPassword() == null || student.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        // Hash the password before saving
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        return repo.save(student);
    }

    public List<Student> getAllStudents() {
        return repo.findAll();
    }

    public Optional<Student> getStudentById(Integer id) {
        return repo.findById(id);
    }

    public Student updateStudent(Student student) {
        // If password is being updated, hash it
        if (student.getPassword() != null && !student.getPassword().isEmpty()) {
            student.setPassword(passwordEncoder.encode(student.getPassword()));
        }
        return repo.save(student);
    }

    public void deleteStudent(Integer id) {
        repo.deleteById(id);
    }

    public boolean login(String email, String password) {
        // Check for null or empty password
        if (password == null || password.isEmpty()) {
            return false;
        }

        Optional<Student> studentOpt = repo.findByEmail(email);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            String storedPassword = student.getPassword();

            // Check if stored password is null or empty
            if (storedPassword == null || storedPassword.isEmpty()) {
                return false;
            }

            // Check if password is already BCrypt encoded
            if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")
                    || storedPassword.startsWith("$2y$")) {
                // Password is already BCrypt encoded
                return passwordEncoder.matches(password, storedPassword);
            } else {
                // Password is plain text - migrate it to BCrypt
                if (storedPassword.equals(password)) {
                    // Update the password to BCrypt format
                    student.setPassword(passwordEncoder.encode(password));
                    repo.save(student);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    // Utility method to migrate all existing plain text passwords to BCrypt
    public void migrateAllPasswordsToBCrypt() {
        List<Student> allStudents = repo.findAll();
        for (Student student : allStudents) {
            String password = student.getPassword();
            // Check if password is not already BCrypt encoded
            if (!password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
                // This is a plain text password, encode it
                student.setPassword(passwordEncoder.encode(password));
                repo.save(student);
                System.out.println("Migrated password for student: " + student.getEmail());
            }
        }
        System.out.println("Password migration completed!");
    }
}
