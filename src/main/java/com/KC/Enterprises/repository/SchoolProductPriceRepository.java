package com.KC.Enterprises.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.KC.Enterprises.entity.SchoolProductPrice;

public interface SchoolProductPriceRepository extends JpaRepository<SchoolProductPrice,Long> {
    @Query("SELECT spp FROM SchoolProductPrice spp WHERE spp.school.id = :schoolId AND spp.product.id = :productId")
    Optional<SchoolProductPrice> findBySchoolIdAndProductId(
            @Param("schoolId") Long schoolId, 
            @Param("productId") Long productId);
    
    boolean existsBySchoolIdAndProductId(Long schoolId, Long productId);
}
