package com.example.buildingmanagement.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class BuildingRequest {

    @NotBlank
    @Size(max = 100)
    private String buildingName;

    @NotNull
    private Double buildingArea;

    @NotBlank
    @Size(max = 10)
    private String areaUnit;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private Integer time;

    @NotBlank
    @Size(max = 10)
    private String timeUnit;

    @Size(max = 255)
    private String design;

    @NotBlank
    @Size(max = 255)
    private String content;

    private Short status;

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public Double getBuildingArea() {
        return buildingArea;
    }

    public void setBuildingArea(Double buildingArea) {
        this.buildingArea = buildingArea;
    }

    public String getAreaUnit() {
        return areaUnit;
    }

    public void setAreaUnit(String areaUnit) {
        this.areaUnit = areaUnit;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getDesign() {
        return design;
    }

    public void setDesign(String design) {
        this.design = design;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }
}
