package com.hereliesaz.julesapisdk.testapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hereliesaz.julesapisdk.testapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        loadApiKey()
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(viewModel.messages.value ?: mutableListOf())
        binding.messagesRecyclerview.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupObservers() {
        viewModel.messages.observe(this) { messages ->
            messageAdapter.messages = messages
            messageAdapter.notifyDataSetChanged()
            binding.messagesRecyclerview.scrollToPosition(messages.size - 1)
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }
    }

    private fun setupClickListeners() {
        binding.saveApiKeyButton.setOnClickListener {
            val apiKey = binding.apiKeyEdittext.text.toString()
            viewModel.setApiKey(apiKey)
            saveApiKey(apiKey)
        }

        binding.sendButton.setOnClickListener {
            val message = binding.messageEdittext.text.toString()
            if (message.isNotBlank()) {
                viewModel.sendMessage(message)
                binding.messageEdittext.text.clear()
            }
        }
    }

    private fun saveApiKey(apiKey: String) {
        val sharedPreferences = getSharedPreferences("JulesTestApp", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("api_key", apiKey).apply()
    }

    private fun loadApiKey() {
        val sharedPreferences = getSharedPreferences("JulesTestApp", Context.MODE_PRIVATE)
        val apiKey = sharedPreferences.getString("api_key", "")
        if (!apiKey.isNullOrBlank()) {
            binding.apiKeyEdittext.setText(apiKey)
            viewModel.setApiKey(apiKey)
        }
    }
}
