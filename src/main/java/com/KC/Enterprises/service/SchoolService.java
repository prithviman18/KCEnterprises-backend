package com.KC.Enterprises.service;

import java.util.List;

import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.SchoolRequest;
import com.KC.Enterprises.dto.SchoolResponse;

public interface SchoolService {
    SchoolResponse createSchool(SchoolRequest request);
    SchoolResponse getSchoolById(Long id);
    List<SchoolResponse> getAllSchools();
    List<SchoolResponse> searchSchools(String schoolName, String city, String contactPerson);
    SchoolResponse updateSchool(Long id, SchoolRequest request);
    DeleteResponse deleteSchool(Long id);
}
