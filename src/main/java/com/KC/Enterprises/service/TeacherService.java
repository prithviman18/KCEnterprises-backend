package com.KC.Enterprises.service;

import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.TeacherRequest;
import com.KC.Enterprises.dto.TeacherResponse;
import java.util.List;

public interface TeacherService {
    
    TeacherResponse createTeacher(TeacherRequest request);
    
    TeacherResponse getTeacherById(Long id);
    
    List<TeacherResponse> getAllTeachers();
    
    List<TeacherResponse> getTeachersBySchoolId(Long schoolId);
    
    List<TeacherResponse> searchTeachers(Long schoolId, String teacherName, String mobile, String email, Boolean isHeadTeacher);
    
    TeacherResponse updateTeacher(Long id, TeacherRequest request);
    
    DeleteResponse deleteTeacher(Long id);
}