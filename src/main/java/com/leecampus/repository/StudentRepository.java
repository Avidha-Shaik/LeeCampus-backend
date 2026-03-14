package com.leecampus.repository;

import com.leecampus.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // 🔥 Exists checks
    boolean existsById(Long id);

    boolean existsByEmail(String email);

    boolean existsByRollNumber(String rollNumber);

    boolean existsByLeetcodeUsername(String leetcodeUsername);

    // 🔥 Finders
    Optional<Student> findByEmail(String email);

    Optional<Student> findByRollNumber(String rollNumber);

    Optional<Student> findByLeetcodeUsername(String leetcodeUsername);

    Optional<Student> findByEmailAndPassword(String email, String password);

    // 🔥 Delete by unique field
    void deleteByEmail(String email);
}