package com.example.evntly.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.evntly.data.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * @property authRepository Repository used to handle authentication operations and
 * profile persistence
 */
class AuthViewModelFactory(
    private val authRepository: AuthRepository = defaultRepository()
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    private companion object {

        fun defaultRepository(): AuthRepository {
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
            return AuthRepository(
                firebaseAuth = auth,
                firestore = firestore
            )
        }
    }
}
