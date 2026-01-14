package com.KC.Enterprises.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherResponse {
    
    private Long id;
    private String teacherName;
    private String mobile;
    private String email;
    private Long schoolId;
    private String schoolName;
    private String designation;
    private Boolean isHeadTeacher;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}