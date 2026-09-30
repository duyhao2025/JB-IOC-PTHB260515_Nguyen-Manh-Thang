package com.example.buildingmanagement.controller;

import com.example.buildingmanagement.dto.ApiResponse;
import com.example.buildingmanagement.dto.BuildingRequest;
import com.example.buildingmanagement.entity.Building;
import com.example.buildingmanagement.service.BuildingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/buildings")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Building>>> getAllBuildings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<Building> buildings = buildingService.getAllBuildings(page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách tòa nhà thành công", buildings, 200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Building>> getBuildingById(@PathVariable Long id) {
        Building building = buildingService.getBuildingById(id);
        if (building == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy tòa nhà", List.of("Tòa nhà với id " + id + " không tồn tại"), 404));
        }
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin tòa nhà thành công", building, 200));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Building>> createBuilding(
            @Valid @ModelAttribute BuildingRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "designFile", required = false) MultipartFile designFile) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Dữ liệu không hợp lệ", errors, 400));
        }

        if (buildingService.isBuildingNameExists(request.getBuildingName())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Tên tòa nhà đã tồn tại", List.of("buildingName: Tên tòa nhà '" + request.getBuildingName() + "' đã được sử dụng"), 400));
        }

        try {
            Building building = buildingService.createBuilding(request, designFile);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Thêm tòa nhà thành công", building, 201));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi thêm tòa nhà", List.of(e.getMessage()), 500));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Building>> updateBuilding(
            @PathVariable Long id,
            @Valid @ModelAttribute BuildingRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "designFile", required = false) MultipartFile designFile) {

        Building existing = buildingService.getBuildingById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy tòa nhà", List.of("Tòa nhà với id " + id + " không tồn tại"), 404));
        }

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Dữ liệu không hợp lệ", errors, 400));
        }

        if (buildingService.isBuildingNameExistsForOther(request.getBuildingName(), id)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Tên tòa nhà đã tồn tại", List.of("buildingName: Tên tòa nhà '" + request.getBuildingName() + "' đã được sử dụng bởi tòa nhà khác"), 400));
        }

        try {
            Building building = buildingService.updateBuilding(id, request, designFile);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật tòa nhà thành công", building, 200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi cập nhật tòa nhà", List.of(e.getMessage()), 500));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Building>> changeStatus(
            @PathVariable Long id,
            @RequestParam Short status) {

        Building existing = buildingService.getBuildingById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy tòa nhà", List.of("Tòa nhà với id " + id + " không tồn tại"), 404));
        }

        if (existing.getStatus() == 2) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Không thể thay đổi trạng thái", List.of("Tòa nhà đã hoàn thành (status = 2), không thể thay đổi trạng thái"), 400));
        }

        Building building = buildingService.changeStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Thay đổi trạng thái thành công", building, 200));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Building>>> searchBuildings(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Short status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<Building> result;

        if (name != null && !name.isBlank()) {
            result = buildingService.searchByName(name, page, size);
        } else if (status != null) {
            result = buildingService.searchByStatus(status, page, size);
        } else {
            result = buildingService.getAllBuildings(page, size);
        }

        return ResponseEntity.ok(ApiResponse.success("Tìm kiếm thành công", result, 200));
    }
}
