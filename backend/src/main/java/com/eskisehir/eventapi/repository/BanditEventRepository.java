package com.eskisehir.eventapi.repository;

import com.eskisehir.eventapi.domain.model.BanditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BanditEventRepository extends JpaRepository<BanditEvent, Long> {

    List<BanditEvent> findByPoiId(Long poiId);

    List<BanditEvent> findByUserId(Long userId);
}
