package com.eskisehir.eventapi.repository;

import com.eskisehir.eventapi.domain.model.RecommendationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationLogRepository extends JpaRepository<RecommendationLog, Long> {

    List<RecommendationLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}
