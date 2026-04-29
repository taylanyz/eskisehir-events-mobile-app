package com.eskisehir.eventapi.domain.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bandit_arm_stats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "poi_id"}))
public class BanditArmStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poi_id", nullable = false)
    private Poi poi;

    @Column(nullable = false)
    private Double alpha = 1.0;

    @Column(nullable = false)
    private Double beta = 1.0;

    @Column(nullable = false)
    private Long plays = 0L;

    @Column(nullable = false)
    private Long wins = 0L;

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Poi getPoi() { return poi; }
    public void setPoi(Poi poi) { this.poi = poi; }
    public Double getAlpha() { return alpha; }
    public void setAlpha(Double alpha) { this.alpha = alpha; }
    public Double getBeta() { return beta; }
    public void setBeta(Double beta) { this.beta = beta; }
    public Long getPlays() { return plays; }
    public void setPlays(Long plays) { this.plays = plays; }
    public Long getWins() { return wins; }
    public void setWins(Long wins) { this.wins = wins; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}