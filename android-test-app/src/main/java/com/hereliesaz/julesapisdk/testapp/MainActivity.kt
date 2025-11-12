package com.hereliesaz.julesapisdk.testapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hereliesaz.julesapisdk.CreateSessionRequest
import com.hereliesaz.julesapisdk.JulesClient
import com.hereliesaz.julesapisdk.SourceContext
import com.hereliesaz.julesapisdk.testapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var apiKeyEditText: EditText
    private lateinit var getApiKeyButton: Button
    private lateinit var saveApiKeyButton: Button
    private lateinit var chatHistoryRecyclerView: RecyclerView
    private lateinit var promptEditText: EditText
    private lateinit var sendButton: Button

    private val messages = mutableListOf<String>()
    private val chatAdapter = ChatAdapter(messages)
    private var sessionId: String? = null

    private val julesClient by lazy {
        val sharedPreferences = getSharedPreferences("JulesSDKTestApp", Context.MODE_PRIVATE)
        val apiKey = sharedPreferences.getString("api_key", "") ?: ""
        JulesClient(apiKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiKeyEditText = binding.apiKeyEdittext
        getApiKeyButton = binding.getApiKeyButton
        saveApiKeyButton = binding.saveApiKeyButton
        chatHistoryRecyclerView = binding.chatHistoryRecyclerview
        promptEditText = binding.promptEdittext
        sendButton = binding.sendButton

        val sharedPreferences = getSharedPreferences("JulesSDKTestApp", Context.MODE_PRIVATE)
        apiKeyEditText.setText(sharedPreferences.getString("api_key", ""))

        chatHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        chatHistoryRecyclerView.adapter = chatAdapter

        createJulesSession()

        getApiKeyButton.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://jules.google.com/settings"))
            startActivity(browserIntent)
        }

        saveApiKeyButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            val newApiKey = apiKeyEditText.text.toString()
            editor.putString("api_key", newApiKey)
            editor.apply()
            // Re-initialize the client with the new key and create a new session
            julesClient.apiKey = newApiKey
            createJulesSession()
        }

        sendButton.setOnClickListener {
            val prompt = promptEditText.text.toString()
            if (prompt.isNotBlank() && sessionId != null) {
                messages.add("You: $prompt")
                chatAdapter.notifyItemInserted(messages.size - 1)
                promptEditText.text.clear()

                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val response = julesClient.sendMessage(sessionId!!, prompt)
                        withContext(Dispatchers.Main) {
                            messages.add("Jules: ${response.message}")
                            chatAdapter.notifyItemInserted(messages.size - 1)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            messages.add("Error: ${e.message}")
                            chatAdapter.notifyItemInserted(messages.size - 1)
                        }
                    }
                }
            }
        }
    }

    private fun createJulesSession() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = CreateSessionRequest(
                    prompt = "Test session",
                    sourceContext = SourceContext("sources/github/example/example")
                )
                val session = julesClient.createSession(request)
                sessionId = session.id
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    messages.add("Error creating session: ${e.message}")
                    chatAdapter.notifyItemInserted(messages.size - 1)
                }
            }
        }
    }
}
