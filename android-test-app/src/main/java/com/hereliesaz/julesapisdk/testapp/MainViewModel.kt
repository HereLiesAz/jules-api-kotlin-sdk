package com.hereliesaz.julesapisdk.testapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hereliesaz.julesapisdk.CreateSessionRequest
import com.hereliesaz.julesapisdk.JulesClient
import com.hereliesaz.julesapisdk.JulesSession
import com.hereliesaz.julesapisdk.SdkResult
import com.hereliesaz.julesapisdk.Session
import com.hereliesaz.julesapisdk.Source
import com.hereliesaz.julesapisdk.SourceContext
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel : ViewModel() {

    // For Chat tab
    private val _messages = MutableLiveData<List<Message>>(emptyList())
    val messages: LiveData<List<Message>> = _messages

    // For Settings tab
    private val _sources = MutableLiveData<List<Source>>()
    val sources: LiveData<List<Source>> = _sources

    // For Logcat tab - SINGLE SOURCE OF TRUTH FOR ALL LOGS/ERRORS
    private val _diagnosticLogs = MutableLiveData<List<String>>(emptyList())
    val diagnosticLogs: LiveData<List<String>> = _diagnosticLogs

    private var julesClient: JulesClient? = null
    private var julesSession: JulesSession? = null

    fun addLog(log: String) { // Changed to public
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        val currentLogs = _diagnosticLogs.value.orEmpty().toMutableList()
        currentLogs.add("$timestamp: $log") // Add to the bottom for most recent last
        _diagnosticLogs.postValue(currentLogs)
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
            addLog("Error: API Key is not set. Cannot load sources.")
            return
        }
        addLog("Loading sources...")
        viewModelScope.launch {
            when (val result = julesClient?.listSources()) {
                is SdkResult.Success -> {
                    val sourceList = result.data.sources
                    if (sourceList.isNullOrEmpty()) {
                        addLog("No sources found for this API key.")
                        _sources.postValue(emptyList())
                    } else {
                        _sources.postValue(sourceList)
                        addLog("Successfully loaded ${sourceList.size} sources.")
                    }
                }
                is SdkResult.Error -> {
                    addLog("API Error loading sources: ${result.code} - ${result.body}")
                }
                is SdkResult.NetworkError -> {
                    val sw = StringWriter()
                    result.throwable.printStackTrace(PrintWriter(sw))
                    addLog("Network error loading sources:\n$sw")
                }
                null -> {
                     addLog("Error: JulesClient is not initialized.")
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
            _messages.postValue(emptyList()) // Clear chat on new session
            when (val result = julesClient?.createSession(CreateSessionRequest("Test Application", SourceContext(source.name)))) {
                is SdkResult.Success -> {
                    julesSession = result.data
                    val successMsg = "Session created with source: ${source.url}"
                    addMessage(Message(successMsg, MessageType.BOT))
                    addLog(successMsg)
                }
                is SdkResult.Error -> {
                    val errorMsg = "API Error creating session: ${result.code} - ${result.body}"
                    addMessage(Message(errorMsg, MessageType.ERROR))
                    addLog(errorMsg)
                }
                is SdkResult.NetworkError -> {
                    val sw = StringWriter()
                    result.throwable.printStackTrace(PrintWriter(sw))
                    val errorMsg = "Network error creating session:\n$sw"
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
                    val errorMsg = "Network error sending message:\n$sw"
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
        val newMessages = _messages.value.orEmpty().toMutableList()
        newMessages.add(message)
        _messages.postValue(newMessages)
    }
}
