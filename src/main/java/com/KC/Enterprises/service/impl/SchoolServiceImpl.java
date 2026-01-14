package com.KC.Enterprises.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.SchoolRequest;
import com.KC.Enterprises.dto.SchoolResponse;
import com.KC.Enterprises.entity.School;
import com.KC.Enterprises.exception.ResourceNotFoundException;
import com.KC.Enterprises.repository.SchoolRepository;
import com.KC.Enterprises.service.SchoolService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchoolServiceImpl implements SchoolService{

    private final SchoolRepository schoolRepository;

    @Override
    public SchoolResponse createSchool(SchoolRequest request){
        if(request.getGstNumber()!=null && !request.getGstNumber().isEmpty()){
            boolean gstNumExists = schoolRepository.existsByGstNumber(request.getGstNumber());
            if(gstNumExists){
                throw new RuntimeException("School with GST number " + request.getGstNumber() + " already exists");
            }
        }
        School school = School.builder()
                .schoolName(request.getSchoolName())
                .address(request.getAddress())
                .contactPersonName(request.getContactPersonName())
                .contactPhone(request.getContactPhone())
                .contactEmail(request.getContactEmail())
                .gstNumber(request.getGstNumber())
                .build();

            School savedSchool = schoolRepository.save(school);
        return mapToResponse(savedSchool);
    }

    @Override
    public SchoolResponse getSchoolById(Long id){
        School school = schoolRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + id));
        return mapToResponse(school);
    }

    @Override
    public List<SchoolResponse> getAllSchools(){
        return schoolRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<SchoolResponse> searchSchools(String schoolName , String city, String contactPerson){
        return schoolRepository.findAll()
            .stream()
            .filter(school -> 
                (schoolName == null || schoolName.isEmpty() || 
                     school.getSchoolName().toLowerCase().contains(schoolName.toLowerCase())) &&
                    (city == null || city.isEmpty() || 
                     (school.getAddress() != null && school.getAddress().toLowerCase().contains(city.toLowerCase()))) &&
                    (contactPerson == null || contactPerson.isEmpty() || 
                     (school.getContactPersonName() != null && 
                      school.getContactPersonName().toLowerCase().contains(contactPerson.toLowerCase())))
            )
            .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SchoolResponse updateSchool(Long id, SchoolRequest request) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + id));

        // Check if GST number is being changed and if it already exists
        if (request.getGstNumber() != null && 
            !request.getGstNumber().isEmpty() && 
            !request.getGstNumber().equals(school.getGstNumber())) {
            
            boolean gstExists = schoolRepository.existsByGstNumber(request.getGstNumber());
            if (gstExists) {
                throw new RuntimeException("Another school with GST number " + request.getGstNumber() + " already exists");
            }
        }

        school.setSchoolName(request.getSchoolName());
        school.setAddress(request.getAddress());
        school.setContactPersonName(request.getContactPersonName());
        school.setContactPhone(request.getContactPhone());
        school.setContactEmail(request.getContactEmail());
        school.setGstNumber(request.getGstNumber());

        School updatedSchool = schoolRepository.save(school);
        return mapToResponse(updatedSchool);
    }

    public DeleteResponse deleteSchool(Long id){
        if(!schoolRepository.existsById(id)){
            throw new ResourceNotFoundException("School not found with id : " + id);
        }

        schoolRepository.deleteById(id);
        return DeleteResponse.builder()
            .success(true)
            .message("School deleted successfully")
            .deletedId(id)
            .build();
    }


    private SchoolResponse mapToResponse(School school) {
        return SchoolResponse.builder()
                .id(school.getId())
                .schoolName(school.getSchoolName())
                .address(school.getAddress())
                .contactPersonName(school.getContactPersonName())
                .contactPhone(school.getContactPhone())
                .contactEmail(school.getContactEmail())
                .gstNumber(school.getGstNumber())
                .createdAt(school.getCreatedAt())
                .updatedAt(school.getUpdatedAt())
                .build();
    }
}
