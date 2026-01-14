package com.KC.Enterprises.repository;

import com.KC.Enterprises.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    
    List<Teacher> findBySchoolId(Long schoolId);
    
    List<Teacher> findByTeacherNameContainingIgnoreCase(String teacherName);
    
    List<Teacher> findByMobile(String mobile);
    
    Optional<Teacher> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByMobile(String mobile);
    
    List<Teacher> findByIsHeadTeacher(Boolean isHeadTeacher);
    
    List<Teacher> findBySchoolIdAndIsHeadTeacher(Long schoolId, Boolean isHeadTeacher);
    
    boolean existsByEmailAndSchoolId(String email, Long schoolId);
    
    boolean existsByMobileAndSchoolId(String mobile, Long schoolId);
}