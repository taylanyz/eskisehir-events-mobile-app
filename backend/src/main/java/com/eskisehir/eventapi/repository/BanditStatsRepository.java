package com.eskisehir.eventapi.repository;

import com.eskisehir.eventapi.domain.model.BanditArmStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BanditStatsRepository extends JpaRepository<BanditArmStat, Long> {

    Optional<BanditArmStat> findByUserIdAndPoiId(Long userId, Long poiId);

    List<BanditArmStat> findByUserId(Long userId);
}