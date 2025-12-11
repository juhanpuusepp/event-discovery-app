package com.example.evntly.domain.model

/**
 * Signed-in user's profile.
 *
 * @property uid Unique identifier of the user from Firebase Auth
 * @property displayName Actual name name shown in the UI
 * @property email
 * @property gender
 */
data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val gender: String = ""
)