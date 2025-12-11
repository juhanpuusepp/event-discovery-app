package com.example.evntly.data.auth

import com.example.evntly.domain.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * User authentication and profile persistence.
 *
 * @property firebaseAuth Entry point for Firebase Authentication operations.
 * @property firestore Cloud Firestore instance used for storing user profiles.
 */
class AuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    /**
     * Signs in an existing user with email and password.
     *
     * On success, the associated [UserProfile] is loaded from Firestore if it exists,
     * or a minimal profile is constructed from the Firebase user object.
     *
     * @param email Email address used to authenticate the user.
     * @param password Plain-text password corresponding to the given email.
     * @return [Result] containing the authenticated [UserProfile] on success,
     * or an error on failure.
     */
    suspend fun signIn(
        email: String,
        password: String
    ): Result<UserProfile> {
        return try {
            // Authenticate with Firebase Auth.
            firebaseAuth.signInWithEmailAndPassword(email, password).await()

            val firebaseUser = firebaseAuth.currentUser
                ?: return Result.failure(
                    IllegalStateException("User is null after successful sign-in")
                )

            val uid = firebaseUser.uid

            // Attempt to load an extended profile from Firestore.
            val snapshot = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            val profile = snapshot.toObject(UserProfile::class.java)?.copy(uid = uid)
                ?: UserProfile(
                    uid = uid,
                    displayName = firebaseUser.displayName.orEmpty(),
                    email = firebaseUser.email ?: email,
                    gender = ""
                )

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registers a new user account and stores the associated profile in Firestore.
     *
     * @param displayName Human-readable name chosen by the user.
     * @param email Email address used as a unique identifier for sign-in.
     * @param password Plain-text password to be associated with the account.
     * @param gender User's self-described gender value stored for personalisation.
     * @return [Result] containing the created [UserProfile] on success,
     * or an error on failure.
     */
    suspend fun signUp(
        displayName: String,
        email: String,
        password: String,
        gender: String
    ): Result<UserProfile> {
        return try {
            val result = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val firebaseUser = result.user
                ?: return Result.failure(
                    IllegalStateException("User is null after successful sign-up")
                )

            val uid = firebaseUser.uid
            val profile = UserProfile(
                uid = uid,
                displayName = displayName,
                email = email,
                gender = gender
            )

            // Persist profile in Firestore.
            firestore.collection("users")
                .document(uid)
                .set(profile)
                .await()

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieves the currently authenticated user's profile from Firestore, if available.
     *
     * @return [Result] containing the current [UserProfile] when the user is logged in,
     * [Result.success] with `null` when no user is logged in, or [Result.failure] when
     * loading the profile from Firestore fails.
     */
    suspend fun getCurrentUserProfile(): Result<UserProfile?> {
        return try {
            val firebaseUser = firebaseAuth.currentUser
                ?: return Result.success(null)

            val uid = firebaseUser.uid

            val snapshot = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            val profile = snapshot.toObject(UserProfile::class.java)?.copy(uid = uid)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Signs out the currently authenticated user, if any.
     *
     * This operation is synchronous because Firebase Authentication performs sign-out locally.
     */
    fun signOut() {
        firebaseAuth.signOut()
    }
}
