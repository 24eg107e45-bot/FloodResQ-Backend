
package com.floodresq.floodresq_backend;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emergency-reports")
@CrossOrigin(origins = "http://localhost:5173")
public class EmergencyReportController {

    private final EmergencyReportRepository repository;

    public EmergencyReportController(
            EmergencyReportRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<EmergencyReport> createReport(
            @RequestBody EmergencyReport report) {

        // Always set the initial status on the backend.
        report.setStatus("SUBMITTED");
        report.setAssignedTeam("");

        // Calculate the risk score on the server.
        int score = calculateRiskScore(report);
        report.setRiskScore(score);
        report.setRiskLevel(getRiskLevel(score));

        EmergencyReport savedReport = repository.save(report);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedReport);
    }

    @GetMapping
    public List<EmergencyReport> getAllReports() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmergencyReport> getReportById(
            @PathVariable Long id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private int calculateRiskScore(EmergencyReport report) {
        int score = 10;

        String water = safe(report.getWaterLevel());
        String injuries = safe(report.getInjuries());
        String road = safe(report.getRoadCondition());
        String danger = safe(report.getImmediateDanger());
        String type = safe(report.getEmergencyType());

        switch (water) {
            case "Ankle deep" -> score += 5;
            case "Knee deep" -> score += 15;
            case "Waist deep" -> score += 25;
            case "Above head" -> score += 40;
            case "Water entering home" -> score += 25;
            case "Unknown" -> score += 10;
            default -> {
            }
        }

        if ("Yes".equalsIgnoreCase(injuries)) {
            score += 20;
        } else if ("Unknown".equalsIgnoreCase(injuries)) {
            score += 10;
        }

        if (report.getVulnerablePeople() != null
                && report.getVulnerablePeople() > 0) {
            score += 10;
        }

        if ("Flooded".equalsIgnoreCase(road)) {
            score += 10;
        } else if ("Blocked".equalsIgnoreCase(road)) {
            score += 15;
        } else if ("Unknown".equalsIgnoreCase(road)) {
            score += 5;
        }

        if ("Yes".equalsIgnoreCase(danger)) {
            score += 25;
        } else if ("Unknown".equalsIgnoreCase(danger)) {
            score += 10;
        }

        int people = report.getPeopleAffected() == null
                ? 0 : report.getPeopleAffected();

        if (people >= 6) {
            score += 5;
        }
        if (people >= 15) {
            score += 5;
        }

        if ("People trapped".equalsIgnoreCase(type)
                || "Medical emergency".equalsIgnoreCase(type)) {
            score += 15;
        }

        return Math.min(score, 100);
    }

    private String getRiskLevel(int score) {
        if (score >= 75) {
            return "Critical";
        }
        if (score >= 50) {
            return "High";
        }
        if (score >= 25) {
            return "Moderate";
        }
        return "Low";
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
