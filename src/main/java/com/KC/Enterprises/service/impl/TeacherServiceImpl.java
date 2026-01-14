package com.KC.Enterprises.service.impl;

import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.TeacherRequest;
import com.KC.Enterprises.dto.TeacherResponse;
import com.KC.Enterprises.entity.School;
import com.KC.Enterprises.entity.Teacher;
import com.KC.Enterprises.exception.ResourceNotFoundException;
import com.KC.Enterprises.repository.SchoolRepository;
import com.KC.Enterprises.repository.TeacherRepository;
import com.KC.Enterprises.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    
    private final TeacherRepository teacherRepository;
    private final SchoolRepository schoolRepository;
    
    @Override
    @Transactional
    public TeacherResponse createTeacher(TeacherRequest request) {
        // Fetch school
        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + request.getSchoolId()));
        
        // Check if email already exists in the same school
        if (teacherRepository.existsByEmailAndSchoolId(request.getEmail(), request.getSchoolId())) {
            throw new RuntimeException("Teacher with email " + request.getEmail() + " already exists in this school");
        }
        
        // Check if mobile already exists in the same school
        if (teacherRepository.existsByMobileAndSchoolId(request.getMobile(), request.getSchoolId())) {
            throw new RuntimeException("Teacher with mobile " + request.getMobile() + " already exists in this school");
        }
        
        Teacher teacher = Teacher.builder()
                .school(school)
                .teacherName(request.getTeacherName())
                .mobile(request.getMobile())
                .email(request.getEmail())
                .designation(request.getDesignation())
                .isHeadTeacher(request.getIsHeadTeacher() != null ? request.getIsHeadTeacher() : false)
                .build();
        
        Teacher savedTeacher = teacherRepository.save(teacher);
        return mapToResponse(savedTeacher);
    }
    
    @Override
    public TeacherResponse getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
        return mapToResponse(teacher);
    }
    
    @Override
    public List<TeacherResponse> getAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TeacherResponse> getTeachersBySchoolId(Long schoolId) {
        List<Teacher> teachers = teacherRepository.findBySchoolId(schoolId);
        if (teachers.isEmpty()) {
            throw new ResourceNotFoundException("No teachers found for school with id: " + schoolId);
        }
        return teachers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TeacherResponse> searchTeachers(Long schoolId, String teacherName, String mobile, String email, Boolean isHeadTeacher) {
        return teacherRepository.findAll()
                .stream()
                .filter(teacher -> 
                    (schoolId == null || teacher.getSchool().getId().equals(schoolId)) &&
                    (teacherName == null || teacherName.isEmpty() || 
                     teacher.getTeacherName().toLowerCase().contains(teacherName.toLowerCase())) &&
                    (mobile == null || mobile.isEmpty() || 
                     teacher.getMobile().contains(mobile)) &&
                    (email == null || email.isEmpty() || 
                     teacher.getEmail().toLowerCase().contains(email.toLowerCase())) &&
                    (isHeadTeacher == null || 
                     (teacher.getIsHeadTeacher() != null && teacher.getIsHeadTeacher().equals(isHeadTeacher)))
                )
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public TeacherResponse updateTeacher(Long id, TeacherRequest request) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
        
        // Check if email is being changed and if it already exists in the same school
        if (request.getEmail() != null && !request.getEmail().equals(teacher.getEmail())) {
            boolean emailExists = teacherRepository.existsByEmailAndSchoolId(request.getEmail(), request.getSchoolId());
            if (emailExists) {
                throw new RuntimeException("Another teacher with email " + request.getEmail() + " already exists in this school");
            }
        }
        
        // Check if mobile is being changed and if it already exists in the same school
        if (request.getMobile() != null && !request.getMobile().equals(teacher.getMobile())) {
            boolean mobileExists = teacherRepository.existsByMobileAndSchoolId(request.getMobile(), request.getSchoolId());
            if (mobileExists) {
                throw new RuntimeException("Another teacher with mobile " + request.getMobile() + " already exists in this school");
            }
        }
        
        // Update school if schoolId is different
        if (!teacher.getSchool().getId().equals(request.getSchoolId())) {
            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + request.getSchoolId()));
            teacher.setSchool(school);
        }
        
        teacher.setTeacherName(request.getTeacherName());
        teacher.setMobile(request.getMobile());
        teacher.setEmail(request.getEmail());
        teacher.setDesignation(request.getDesignation());
        teacher.setIsHeadTeacher(request.getIsHeadTeacher() != null ? request.getIsHeadTeacher() : teacher.getIsHeadTeacher());
        
        Teacher updatedTeacher = teacherRepository.save(teacher);
        return mapToResponse(updatedTeacher);
    }
    
    @Override
    @Transactional
    public DeleteResponse deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
        
        teacherRepository.delete(teacher);
        
        return DeleteResponse.builder()
                .success(true)
                .message("Teacher deleted successfully")
                .deletedId(id)
                .build();
    }
    
    private TeacherResponse mapToResponse(Teacher teacher) {
        return TeacherResponse.builder()
                .id(teacher.getId())
                .schoolId(teacher.getSchool().getId())
                .schoolName(teacher.getSchool().getSchoolName())
                .teacherName(teacher.getTeacherName())
                .mobile(teacher.getMobile())
                .email(teacher.getEmail())
                .designation(teacher.getDesignation())
                .isHeadTeacher(teacher.getIsHeadTeacher())
                .createdAt(teacher.getCreatedAt())
                .updatedAt(teacher.getUpdatedAt())
                .build();
    }
}