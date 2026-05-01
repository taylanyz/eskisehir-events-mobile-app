package com.eskisehir.eventapi.repository;

import com.eskisehir.eventapi.domain.model.UserFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository for user feedback persistence.
 */
@Repository
public interface FeedbackRepository extends JpaRepository<UserFeedback, Long> {
    
    /**
     * Find all feedback submitted by a user.
     */
    List<UserFeedback> findByUserId(Long userId);
    
    /**
     * Find all feedback for a specific route.
     */
    List<UserFeedback> findByRouteId(Long routeId);
    
    /**
     * Find feedback by user and route.
     */
    Optional<UserFeedback> findByUserIdAndRouteId(Long userId, Long routeId);
    
    /**
     * Find all unprocessed feedback (for async reward integration).
     */
    @Query("SELECT f FROM UserFeedback f WHERE f.feedbackProcessed = FALSE ORDER BY f.createdAtMs ASC")
    List<UserFeedback> findUnprocessedFeedback();
    
    /**
     * Count feedback for a route.
     */
    long countByRouteId(Long routeId);
}
