package com.hereliesaz.julesapisdk.testapp

import com.hereliesaz.julesapisdk.Source

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class SourcesLoaded(val sources: List<Source>) : UiState()
    data class SessionCreated(val sessionId: String) : UiState()
    data class Error(val message: String) : UiState()
}
