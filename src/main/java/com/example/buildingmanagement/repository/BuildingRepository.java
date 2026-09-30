package com.example.buildingmanagement.repository;

import com.example.buildingmanagement.entity.Building;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

    boolean existsByBuildingName(String buildingName);

    boolean existsByBuildingNameAndIdNot(String buildingName, Long id);

    Page<Building> findByBuildingNameContainingIgnoreCase(String buildingName, Pageable pageable);

    Page<Building> findByStatus(Short status, Pageable pageable);
}
