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

    private val _messages = MutableLiveData<MutableList<Message>>(mutableListOf())
    val messages: LiveData<MutableList<Message>> = _messages

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var julesClient: JulesClient? = null
    private var julesSession: JulesSession? = null

    fun setApiKey(apiKey: String) {
        if (apiKey.isNotBlank()) {
            julesClient = JulesClient(apiKey)
            _messages.value?.clear()
            _messages.postValue(_messages.value)
            createJulesSession()
        }
    }

    private fun createJulesSession() {
        viewModelScope.launch {
            try {
                julesSession = julesClient?.createSession(CreateSessionRequest("Test Application", SourceContext("Test Application")))
            } catch (e: Exception) {
                _errorMessage.postValue("Error creating session: ${e.message}")
            }
        }
    }

    fun sendMessage(text: String) {
        val userMessage = Message(text, true)
        _messages.value?.add(userMessage)
        _messages.postValue(_messages.value)

        viewModelScope.launch {
            try {
                val response = julesSession?.sendMessage(text)
                response?.let {
                    val botMessage = Message(it.message, false)
                    _messages.value?.add(botMessage)
                    _messages.postValue(_messages.value)
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error sending message: ${e.message}")
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
