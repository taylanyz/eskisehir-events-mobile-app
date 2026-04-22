package com.eskisehir.eventapi.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Logs which POIs were recommended to a user, their score/rank,
 * and whether the user interacted with them.
 * Used for computing Precision@K, Recall@K, NDCG@K.
 */
@Entity
@Table(name = "recommendation_logs")
public class RecommendationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poi_id", nullable = false)
    private Poi poi;

    private Double score;
    private Integer rank;
    private Boolean wasClicked;
    private Boolean wasVisited;

    private String algorithmVersion;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public RecommendationLog() {
        this.createdAt = LocalDateTime.now();
        this.wasClicked = false;
        this.wasVisited = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Poi getPoi() { return poi; }
    public void setPoi(Poi poi) { this.poi = poi; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }

    public Boolean getWasClicked() { return wasClicked; }
    public void setWasClicked(Boolean wasClicked) { this.wasClicked = wasClicked; }

    public Boolean getWasVisited() { return wasVisited; }
    public void setWasVisited(Boolean wasVisited) { this.wasVisited = wasVisited; }

    public String getAlgorithmVersion() { return algorithmVersion; }
    public void setAlgorithmVersion(String algorithmVersion) { this.algorithmVersion = algorithmVersion; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
