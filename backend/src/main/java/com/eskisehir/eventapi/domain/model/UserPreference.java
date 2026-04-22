package com.eskisehir.eventapi.domain.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * User preferences for recommendations and route planning.
 * One-to-one relationship with User.
 */
@Entity
@Table(name = "user_preferences")
public class UserPreference {

    @Id
    private Long id; // Same as user ID

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_preferred_categories", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private List<Category> preferredCategories;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_preferred_tags", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "tag")
    private List<String> preferredTags;

    @Enumerated(EnumType.STRING)
    private SensitivityLevel budgetSensitivity;

    @Enumerated(EnumType.STRING)
    private SensitivityLevel crowdTolerance;

    @Enumerated(EnumType.STRING)
    private MobilityPreference mobilityPreference;

    /** How much the user values sustainability [0, 1] */
    private Double sustainabilityPreference;

    /** Max walking time the user is comfortable with (minutes) */
    private Integer maxWalkingMinutes;

    public UserPreference() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Category> getPreferredCategories() { return preferredCategories; }
    public void setPreferredCategories(List<Category> preferredCategories) { this.preferredCategories = preferredCategories; }

    public List<String> getPreferredTags() { return preferredTags; }
    public void setPreferredTags(List<String> preferredTags) { this.preferredTags = preferredTags; }

    public SensitivityLevel getBudgetSensitivity() { return budgetSensitivity; }
    public void setBudgetSensitivity(SensitivityLevel budgetSensitivity) { this.budgetSensitivity = budgetSensitivity; }

    public SensitivityLevel getCrowdTolerance() { return crowdTolerance; }
    public void setCrowdTolerance(SensitivityLevel crowdTolerance) { this.crowdTolerance = crowdTolerance; }

    public MobilityPreference getMobilityPreference() { return mobilityPreference; }
    public void setMobilityPreference(MobilityPreference mobilityPreference) { this.mobilityPreference = mobilityPreference; }

    public Double getSustainabilityPreference() { return sustainabilityPreference; }
    public void setSustainabilityPreference(Double sustainabilityPreference) { this.sustainabilityPreference = sustainabilityPreference; }

    public Integer getMaxWalkingMinutes() { return maxWalkingMinutes; }
    public void setMaxWalkingMinutes(Integer maxWalkingMinutes) { this.maxWalkingMinutes = maxWalkingMinutes; }
}
