package com.hereliesaz.julesapisdk.testapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hereliesaz.julesapisdk.JulesClient
import com.hereliesaz.julesapisdk.JulesSession
import com.hereliesaz.julesapisdk.CreateSessionRequest
import com.hereliesaz.julesapisdk.SourceContext
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>(emptyList())
    val messages: LiveData<List<Message>> = _messages

    private var julesClient: JulesClient? = null
    private var julesSession: JulesSession? = null

    fun setApiKey(apiKey: String) {
        if (apiKey.isNotBlank()) {
            julesClient = JulesClient(apiKey)
            _messages.postValue(emptyList())
            createJulesSession()
        }
    }

    private fun createJulesSession() {
        viewModelScope.launch {
            try {
                julesSession = julesClient?.createSession(CreateSessionRequest("Test Application", SourceContext("Test Application")))
            } catch (e: Exception) {
                addMessage(Message("Error creating session: ${e.message}", MessageType.ERROR))
            }
        }
    }

    fun sendMessage(text: String) {
        if (julesSession == null) {
            addMessage(Message("Session not created. Please check your API key.", MessageType.ERROR))
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
                addMessage(Message("Error sending message: ${e.message}", MessageType.ERROR))
            }
        }
    }

    private fun addMessage(message: Message) {
        val newMessages = _messages.value.orEmpty().toMutableList()
        newMessages.add(message)
        _messages.postValue(newMessages)
    }
}
