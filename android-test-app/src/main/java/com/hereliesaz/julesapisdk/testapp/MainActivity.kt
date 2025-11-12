package com.hereliesaz.julesapisdk.testapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.hereliesaz.julesapisdk.testapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        loadApiKey()
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter()
        binding.messagesRecyclerview.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupObservers() {
        viewModel.messages.observe(this) { messages ->
            messageAdapter.submitList(messages)
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
        binding.getApiKeyButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://jules.google.com/settings"))
            startActivity(intent)
        }

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
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            "JulesTestApp",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        sharedPreferences.edit().putString("api_key", apiKey).apply()
        showChat()
    }

    private fun loadApiKey() {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            "JulesTestApp",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val apiKey = sharedPreferences.getString("api_key", "")
        if (!apiKey.isNullOrBlank()) {
            binding.apiKeyEdittext.setText(apiKey)
            viewModel.setApiKey(apiKey)
            showChat()
        } else {
            showApiKeyEntry()
        }
    }

    private fun showApiKeyEntry() {
        binding.apiKeyEntryLayout.visibility = View.VISIBLE
        binding.messagesRecyclerview.visibility = View.GONE
        binding.messageInputLayout.visibility = View.GONE
    }

    private fun showChat() {
        binding.apiKeyEntryLayout.visibility = View.GONE
        binding.messagesRecyclerview.visibility = View.VISIBLE
        binding.messageInputLayout.visibility = View.VISIBLE
    }
}
