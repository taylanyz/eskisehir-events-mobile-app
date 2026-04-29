package com.eskisehir.eventapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request DTO for updating user preferences.
 */
data class PreferenceUpdateRequest(
    @SerializedName("preferredCategories")
    val preferredCategories: List<String>? = null,
    @SerializedName("preferredTags")
    val preferredTags: List<String>? = null,
    @SerializedName("budgetSensitivity")
    val budgetSensitivity: String? = null,
    @SerializedName("crowdTolerance")
    val crowdTolerance: String? = null,
    @SerializedName("mobilityPreference")
    val mobilityPreference: String? = null,
    @SerializedName("sustainabilityPreference")
    val sustainabilityPreference: Double? = null,
    @SerializedName("maxWalkingMinutes")
    val maxWalkingMinutes: Int? = null
)
