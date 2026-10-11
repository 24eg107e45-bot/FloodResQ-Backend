
package com.floodresq.floodresq_backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmergencyReportRepository
        extends JpaRepository<EmergencyReport, Long> {
}
