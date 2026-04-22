package com.eskisehir.eventapi.repository;

import com.eskisehir.eventapi.domain.model.UserFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFeedbackRepository extends JpaRepository<UserFeedback, Long> {

    List<UserFeedback> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<UserFeedback> findByRouteId(Long routeId);
}
