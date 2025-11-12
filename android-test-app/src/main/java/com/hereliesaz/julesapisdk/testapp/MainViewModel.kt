package com.hereliesaz.julesapisdk.testapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hereliesaz.julesapisdk.CreateSessionRequest
import com.hereliesaz.julesapisdk.GithubRepoSource
import com.hereliesaz.julesapisdk.GithubRepoContext
import com.hereliesaz.julesapisdk.JulesClient
import com.hereliesaz.julesapisdk.JulesSession
import com.hereliesaz.julesapisdk.SdkResult
import com.hereliesaz.julesapisdk.Source
import com.hereliesaz.julesapisdk.SourceContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel : ViewModel() {

    // A single StateFlow to hold the UI state
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    // For Logcat tab
    private val _diagnosticLogs = MutableStateFlow<List<String>>(emptyList())
    val diagnosticLogs: StateFlow<List<String>> = _diagnosticLogs

    private var julesClient: JulesClient? = null
    private var julesSession: JulesSession? = null
    private var activityPollingJob: Job? = null
    private var lastSeenActivityId: String? = null

    fun addLog(log: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        val currentLogs = _diagnosticLogs.value.toMutableList()
        currentLogs.add("$timestamp: $log")
        _diagnosticLogs.value = currentLogs
    }

    fun initializeClient(apiKey: String) {
        if (apiKey.isNotBlank()) {
            julesClient = JulesClient(apiKey)
            addLog("JulesClient initialized.")
        } else {
            addLog("Attempted to initialize client with blank API key.")
        }
    }

    fun loadSources() {
        if (julesClient == null) {
            _uiState.value = UiState.Error("API Key is not set. Cannot load sources.")
            return
        }
        addLog("Loading sources...")
        viewModelScope.launch {
            when (val result = julesClient?.listSources()) {
                is SdkResult.Success -> {
                    val sourceList = result.data.sources
                    if (sourceList.isNullOrEmpty()) {
                        addLog("No sources found for this API key.")
                        _uiState.value = UiState.SourcesLoaded(emptyList())
                    } else {
                        _uiState.value = UiState.SourcesLoaded(sourceList)
                        addLog("Successfully loaded ${sourceList.size} sources.")
                    }
                }
                is SdkResult.Error -> {
                    val errorMsg = "API Error loading sources: ${result.code} - ${result.body}"
                    _uiState.value = UiState.Error(errorMsg)
                    addLog(errorMsg)
                }
                is SdkResult.NetworkError -> {
                    val sw = StringWriter()
                    result.throwable.printStackTrace(PrintWriter(sw))
                    val errorMsg = "Network error loading sources:$sw"
                    _uiState.value = UiState.Error(errorMsg)
                    addLog(errorMsg)
                }
                null -> {
                    val errorMsg = "Error: JulesClient is not initialized."
                    _uiState.value = UiState.Error(errorMsg)
                    addLog(errorMsg)
                }
            }
        }
    }

    fun createSession(source: Source) {
        if (julesClient == null) {
            addLog("Error: API Key is not set. Cannot create session.")
            return
        }
        addLog("Creating session with source: ${source.name}")
        viewModelScope.launch {
            val sourceContext = if (source is GithubRepoSource) {
                SourceContext(source.name, GithubRepoContext("main"))
            } else {
                SourceContext(source.name)
            }
            when (val result = julesClient?.createSession(CreateSessionRequest("Test Application", sourceContext))) {
                is SdkResult.Success -> {
                    julesSession = result.data
                    if (source is GithubRepoSource) {
                        val url = "https://github.com/${source.githubRepo.owner}/${source.githubRepo.repo}"
                        val successMsg = "Session created with source: $url"
                        addMessage(Message(successMsg, MessageType.BOT))
                        addLog(successMsg)
                    } else {
                        addLog("Session created with source: ${source.name} (URL not available)")
                    }
                    startActivityPolling(julesSession!!.session.id)
                }
                is SdkResult.Error -> {
                    val errorMsg = "API Error creating session: ${result.code} - ${result.body}"
                    addMessage(Message(errorMsg, MessageType.ERROR))
                    addLog(errorMsg)
                }
                is SdkResult.NetworkError -> {
                    val sw = StringWriter()
                    result.throwable.printStackTrace(PrintWriter(sw))
                    val errorMsg = "Network error creating session:$sw"
                    addMessage(Message(errorMsg, MessageType.ERROR))
                    addLog(errorMsg)
                }
                null -> {
                     addLog("Error: JulesClient is not initialized.")
                }
            }
        }
    }

    fun sendMessage(text: String) {
        if (julesSession == null) {
            val errorMsg = "Session not created. Please configure API Key and Source in Settings."
            addMessage(Message(errorMsg, MessageType.ERROR))
            addLog(errorMsg)
            return
        }

        addMessage(Message(text, MessageType.USER))

        viewModelScope.launch {
            when (val result = julesSession?.sendMessage(text)) {
                is SdkResult.Success -> {
                    addLog("Message sent successfully. Agent response will arrive in a new activity.")
                }
                is SdkResult.Error -> {
                    val errorMsg = "Error sending message: ${result.code} - ${result.body}"
                    addLog(errorMsg)
                    addMessage(Message(errorMsg, MessageType.ERROR))
                }
                is SdkResult.NetworkError -> {
                    val sw = StringWriter()
                    result.throwable.printStackTrace(PrintWriter(sw))
                    val errorMsg = "Network error sending message:$sw"
                    addLog(errorMsg)
                    addMessage(Message(errorMsg, MessageType.ERROR))
                }
                null -> {
                    addLog("Error: Session is not initialized.")
                }
            }
        }
    }

    private fun addMessage(message: Message) {
        val currentState = _uiState.value
        if (currentState is UiState.Chat) {
            val newMessages = currentState.messages.toMutableList()
            newMessages.add(message)
            _uiState.value = UiState.Chat(newMessages)
        } else {
            _uiState.value = UiState.Chat(listOf(message))
        }
    }

    private fun startActivityPolling(sessionId: String) {
        stopActivityPolling() // Ensure no multiple polls are running
        activityPollingJob = viewModelScope.launch {
            while (true) {
                when (val result = julesClient?.listActivities(sessionId)) {
                    is SdkResult.Success -> {
                        val activities = result.data.activities
                        if (!activities.isNullOrEmpty()) {
                            val newActivities = if (lastSeenActivityId == null) {
                                activities
                            } else {
                                val lastIndex = activities.indexOfFirst { it.id == lastSeenActivityId }
                                if (lastIndex != -1 && lastIndex < activities.lastIndex) {
                                    activities.subList(lastIndex + 1, activities.size)
                                } else {
                                    emptyList()
                                }
                            }

                            newActivities.forEach { activity ->
                                val activityMessage = Message("Activity: ${activity.description}", MessageType.BOT)
                                addMessage(activityMessage)
                                lastSeenActivityId = activity.id
                            }
                        }
                    }
                    is SdkResult.Error -> {
                        val errorMsg = "Error polling activities: ${result.code} - ${result.body}"
                        addLog(errorMsg)
                        addMessage(Message(errorMsg, MessageType.ERROR))
                        stopActivityPolling()
                    }
                    is SdkResult.NetworkError -> {
                        val sw = StringWriter()
                        result.throwable.printStackTrace(PrintWriter(sw))
                        val errorMsg = "Network error polling activities:$sw"
                        addLog(errorMsg)
                        addMessage(Message(errorMsg, MessageType.ERROR))
                        stopActivityPolling()
                    }
                    null -> {
                        addLog("Error: JulesClient is not initialized.")
                        stopActivityPolling()
                    }
                }
                delay(5000) // Poll every 5 seconds
            }
        }
    }

    private fun stopActivityPolling() {
        activityPollingJob?.cancel()
        activityPollingJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopActivityPolling()
        julesClient?.close()
    }
}
