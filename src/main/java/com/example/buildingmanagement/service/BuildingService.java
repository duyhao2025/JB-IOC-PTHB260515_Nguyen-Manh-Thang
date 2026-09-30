package com.example.buildingmanagement.service;

import com.example.buildingmanagement.dto.BuildingRequest;
import com.example.buildingmanagement.entity.Building;
import com.example.buildingmanagement.repository.BuildingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class BuildingService {

    @Autowired
    private BuildingRepository buildingRepository;

    private static final String UPLOAD_DIR = "uploads/";

    public Page<Building> getAllBuildings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return buildingRepository.findAll(pageable);
    }

    public Building getBuildingById(Long id) {
        return buildingRepository.findById(id).orElse(null);
    }

    public boolean isBuildingNameExists(String buildingName) {
        return buildingRepository.existsByBuildingName(buildingName);
    }

    public boolean isBuildingNameExistsForOther(String buildingName, Long id) {
        return buildingRepository.existsByBuildingNameAndIdNot(buildingName, id);
    }

    public Building createBuilding(BuildingRequest request, MultipartFile designFile) throws IOException {
        Building building = new Building();
        building.setBuildingName(request.getBuildingName());
        building.setBuildingArea(request.getBuildingArea());
        building.setAreaUnit(request.getAreaUnit());
        building.setStartDate(request.getStartDate());
        building.setTime(request.getTime());
        building.setTimeUnit(request.getTimeUnit());
        building.setContent(request.getContent());
        building.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        if (designFile != null && !designFile.isEmpty()) {
            String filePath = saveFile(designFile);
            building.setDesign(filePath);
        } else if (request.getDesign() != null && !request.getDesign().isBlank()) {
            building.setDesign(request.getDesign());
        } else {
            building.setDesign("");
        }

        return buildingRepository.save(building);
    }

    public Building updateBuilding(Long id, BuildingRequest request, MultipartFile designFile) throws IOException {
        Optional<Building> optionalBuilding = buildingRepository.findById(id);
        if (optionalBuilding.isEmpty()) {
            return null;
        }

        Building building = optionalBuilding.get();
        building.setBuildingName(request.getBuildingName());
        building.setBuildingArea(request.getBuildingArea());
        building.setAreaUnit(request.getAreaUnit());
        building.setStartDate(request.getStartDate());
        building.setTime(request.getTime());
        building.setTimeUnit(request.getTimeUnit());
        building.setContent(request.getContent());

        if (designFile != null && !designFile.isEmpty()) {
            String filePath = saveFile(designFile);
            building.setDesign(filePath);
        } else if (request.getDesign() != null && !request.getDesign().isBlank()) {
            building.setDesign(request.getDesign());
        }

        return buildingRepository.save(building);
    }

    public Building changeStatus(Long id, Short status) {
        Optional<Building> optionalBuilding = buildingRepository.findById(id);
        if (optionalBuilding.isEmpty()) {
            return null;
        }

        Building building = optionalBuilding.get();
        if (building.getStatus() == 2) {
            return null;
        }

        building.setStatus(status);
        return buildingRepository.save(building);
    }

    public Page<Building> searchByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return buildingRepository.findByBuildingNameContainingIgnoreCase(name, pageable);
    }

    public Page<Building> searchByStatus(Short status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return buildingRepository.findByStatus(status, pageable);
    }

    private String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filePath.toString();
    }
}
