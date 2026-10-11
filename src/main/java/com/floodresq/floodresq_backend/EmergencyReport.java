package com.floodresq.floodresq_backend;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "emergency_reports")
public class EmergencyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String victimName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, length = 150)
    private String location;

    @Column(nullable = false, length = 300)
    private String address = "";

    @Column(nullable = false, length = 1000)
    private String description = "";

    @Column(nullable = false, length = 50)
    private String emergencyType;

    private Integer peopleAffected;

    private Integer riskScore;

    @Column(length = 20)
    private String riskLevel;

    @Column(length = 50)
    private String waterLevel;

    @Column(length = 20)
    private String injuries;

    private Integer vulnerablePeople;

    @Column(length = 20)
    private String roadCondition;

    @Column(length = 20)
    private String immediateDanger;

    @Column(nullable = false, length = 30)
    private String status = "SUBMITTED";

    @Column(length = 100)
    private String assignedTeam = "";

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public EmergencyReport() {
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null || status.isBlank()) {
            status = "SUBMITTED";
        }
        if (description == null) {
            description = "";
        }
        if (address == null) {
            address = "";
        }
        if (assignedTeam == null) {
            assignedTeam = "";
        }
    }

    public Long getId() {
        return id;
    }

    public String getVictimName() {
        return victimName;
    }

    public void setVictimName(String victimName) {
        this.victimName = victimName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }

    public Integer getPeopleAffected() {
        return peopleAffected;
    }

    public void setPeopleAffected(Integer peopleAffected) {
        this.peopleAffected = peopleAffected;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(String waterLevel) {
        this.waterLevel = waterLevel;
    }

    public String getInjuries() {
        return injuries;
    }

    public void setInjuries(String injuries) {
        this.injuries = injuries;
    }

    public Integer getVulnerablePeople() {
        return vulnerablePeople;
    }

    public void setVulnerablePeople(Integer vulnerablePeople) {
        this.vulnerablePeople = vulnerablePeople;
    }

    public String getRoadCondition() {
        return roadCondition;
    }

    public void setRoadCondition(String roadCondition) {
        this.roadCondition = roadCondition;
    }

    public String getImmediateDanger() {
        return immediateDanger;
    }

    public void setImmediateDanger(String immediateDanger) {
        this.immediateDanger = immediateDanger;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedTeam() {
        return assignedTeam;
    }

    public void setAssignedTeam(String assignedTeam) {
        this.assignedTeam = assignedTeam;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
