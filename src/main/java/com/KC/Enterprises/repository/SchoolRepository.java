package com.KC.Enterprises.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.KC.Enterprises.entity.School;

public interface SchoolRepository extends JpaRepository<School,Long>{
     // Check if GST number exists
     boolean existsByGstNumber(String gstNumber);
    
     // Find school by GST number
     School findByGstNumber(String gstNumber);
     
     // Search methods
     School findBySchoolName(String schoolName);
     School findByContactEmail(String contactEmail);
     School findByContactPhone(String contactPhone);
}
