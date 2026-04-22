package com.eskisehir.eventapi.repository;

import com.eskisehir.eventapi.domain.model.Route;
import com.eskisehir.eventapi.domain.model.RouteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Route> findByUserIdAndStatus(Long userId, RouteStatus status);
}
