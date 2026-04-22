package com.eskisehir.eventapi.repository;

import com.eskisehir.eventapi.domain.model.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {

    List<UserInteraction> findByUserIdOrderByTimestampDesc(Long userId);

    List<UserInteraction> findByUserIdAndPoiId(Long userId, Long poiId);

    long countByPoiId(Long poiId);
}
