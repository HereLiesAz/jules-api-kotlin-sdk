package com.hereliesaz.julesapisdk.testapp

import com.hereliesaz.julesapisdk.GithubRepoSource
import com.hereliesaz.julesapisdk.JulesSession

sealed class UiState {
    data object Idle : UiState()
    data object Loading : UiState()
    data class SourcesLoaded(val sources: List<GithubRepoSource>) : UiState()
    data class SessionCreated(val session: JulesSession) : UiState()
    data class Chat(val messages: List<Message>) : UiState()
    data class Error(val message: String) : UiState()
}
