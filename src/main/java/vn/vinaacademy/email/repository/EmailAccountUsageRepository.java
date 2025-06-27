package vn.vinaacademy.email.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.vinaacademy.email.entity.EmailAccountUsage;

import java.time.LocalDate;
import java.util.Optional;

public interface EmailAccountUsageRepository extends JpaRepository<EmailAccountUsage, Long> {
    Optional<EmailAccountUsage> findByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE EmailAccountUsage e SET e.emailCount = 0, e.lastResetDate = :today WHERE e.lastResetDate <> :today OR e.lastResetDate IS NULL")
    void resetEmailCountsIfNotUpdatedToday(@Param("today") LocalDate today);
}
