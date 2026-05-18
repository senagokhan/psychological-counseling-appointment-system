package com.psikolojikdanismanlik.randevusistemi.repository;

import com.psikolojikdanismanlik.randevusistemi.entity.Email;
import com.psikolojikdanismanlik.randevusistemi.enums.EmailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    List<Email> findTop10ByStatusAndRetryCountLessThanOrderByCreatedAtAsc(EmailStatus status, int retryCount);
}
