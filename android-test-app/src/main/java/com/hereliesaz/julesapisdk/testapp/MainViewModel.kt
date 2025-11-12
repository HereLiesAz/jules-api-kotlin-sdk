package com.hereliesaz.julesapisdk.testapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hereliesaz.julesapisdk.CreateSessionRequest
import com.hereliesaz.julesapisdk.JulesClient
import com.hereliesaz.julesapisdk.JulesSession
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
        currentLogs.add(0, "$timestamp: $log") // Add to the top for most recent first
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
            try {
                val sourceList = julesClient?.listSources()?.sources
                if (sourceList.isNullOrEmpty()) {
                    addLog("No sources found for this API key.")
                    _sources.postValue(emptyList())
                } else {
                    _sources.postValue(sourceList)
                    addLog("Successfully loaded ${sourceList.size} sources.")
                }
            } catch (e: Exception) {
                val sw = StringWriter()
                e.printStackTrace(PrintWriter(sw))
                addLog("Error loading sources:\n$sw")
            }
        }
    }

    fun createSession(source: Source) {
        if (julesClient == null) {
            addLog("Error: API Key not set. Cannot create session.")
            return
        }
        addLog("Creating session with source: ${source.name}")
        viewModelScope.launch {
            try {
                _messages.postValue(emptyList()) // Clear chat on new session
                julesSession = julesClient?.createSession(CreateSessionRequest("Test Application", SourceContext(source.name)))
                val successMsg = "Session created with source: ${source.url}"
                addMessage(Message(successMsg, MessageType.BOT))
                addLog(successMsg)
            } catch (e: Exception) {
                val sw = StringWriter()
                e.printStackTrace(PrintWriter(sw))
                val errorMsg = "Error creating session:\n$sw"
                addMessage(Message(errorMsg, MessageType.ERROR)) // Also show error in chat
                addLog(errorMsg)
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
            try {
                val response = julesSession?.sendMessage(text)
                response?.let {
                    addMessage(Message(it.message, MessageType.BOT))
                }
            } catch (e: Exception) {
                val errorMsg = "Error sending message: ${e.message}"
                addLog(errorMsg)
                addMessage(Message(errorMsg, MessageType.ERROR))
            }
        }
    }

    private fun addMessage(message: Message) {
        val newMessages = _messages.value.orEmpty().toMutableList()
        newMessages.add(message)
        _messages.postValue(newMessages)
    }
}
