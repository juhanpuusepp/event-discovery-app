package com.example.evntly.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.evntly.data.auth.AuthRepository
import com.example.evntly.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Authentication screen state.
 *
 * @property isLoading Indicates if operation is in progress
 * @property errorMessage Message to be displayed in the UI when an operation fails
 * @property currentUser Currently signed-in user's profile, null when no user is authenticated
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentUser: UserProfile? = null
) {

    /**
     * Evaluates to `true` when [currentUser] is not null
     */
    val isLoggedIn: Boolean
        get() = currentUser != null
}

/**
 * ViewModel for coordinating authentication flows and exposing UI state
 *
 * @property authRepository
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<AuthUiState> = MutableStateFlow(AuthUiState())

    val uiState: StateFlow<AuthUiState> = _uiState

    /**
     * Tries to sign in a user with the provided credentials
     *
     * On success [uiState] is updated with the authenticated [UserProfile]
     * On failure [uiState] contains an error message
     *
     * @param email
     * @param password
     */
    fun signIn(
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.signIn(email = email, password = password)

            _uiState.update { current ->
                result.fold(
                    onSuccess = { profile ->
                        current.copy(
                            isLoading = false,
                            errorMessage = null,
                            currentUser = profile
                        )
                    },
                    onFailure = { error ->
                        current.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Sign-in failed"
                        )
                    }
                )
            }
        }
    }

    /**
     * Register a new account and store the user profile
     *
     * On success [uiState] is updated with the newly created [UserProfile]
     * On failure [uiState] contains an error message
     *
     * @param displayName
     * @param email
     * @param password
     * @param gender
     */
    fun signUp(
        displayName: String,
        email: String,
        password: String,
        gender: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.signUp(
                displayName = displayName,
                email = email,
                password = password,
                gender = gender
            )

            _uiState.update { current ->
                result.fold(
                    onSuccess = { profile ->
                        current.copy(
                            isLoading = false,
                            errorMessage = null,
                            currentUser = profile
                        )
                    },
                    onFailure = { error ->
                        current.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Sign-up failed"
                        )
                    }
                )
            }
        }
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.getCurrentUserProfile()

            _uiState.update { current ->
                result.fold(
                    onSuccess = { profile ->
                        current.copy(
                            isLoading = false,
                            errorMessage = null,
                            currentUser = profile
                        )
                    },
                    onFailure = { error ->
                        current.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to load user profile"
                        )
                    }
                )
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = null,
                currentUser = null
            )
        }
    }
}
