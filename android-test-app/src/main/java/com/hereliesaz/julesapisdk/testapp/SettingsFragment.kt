package com.hereliesaz.julesapisdk.testapp

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.hereliesaz.julesapisdk.Source
import com.hereliesaz.julesapisdk.testapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var sourcesAdapter: SourcesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        setupObservers()

        loadSettings()
    }

    private fun setupRecyclerView() {
        sourcesAdapter = SourcesAdapter { source ->
            // Optional: Handle source selection directly, e.g., for immediate feedback
        }
        binding.sourcesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = sourcesAdapter
        }
    }

    private fun setupClickListeners() {
        binding.getApiKeyButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://jules.google.com/settings"))
            startActivity(intent)
        }

        binding.apiKeyEdittext.addTextChangedListener(object : android.text.TextWatcher {
            private var searchFor: String = ""
            private val handler = android.os.Handler(android.os.Looper.getMainLooper())
            private val runnable = Runnable {
                val apiKey = searchFor
                if (apiKey.isNotBlank()) {
                    viewModel.initializeClient(apiKey)
                    viewModel.loadSources()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchFor = s.toString()
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 500) // 500ms debounce
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        binding.saveApiKeyButton.setOnClickListener {
            val selectedSource = sourcesAdapter.getSelectedSource()
            if (selectedSource != null) {
                val apiKey = binding.apiKeyEdittext.text.toString()

                viewModel.addLog("Settings saved. Requesting session creation...")
                viewModel.createSession(selectedSource)
                saveSettings(apiKey, selectedSource.name)

                // Switch to logcat tab to see the session creation status
                (requireActivity() as? MainActivity)?.binding?.viewPager?.currentItem = 2
            } else {
                viewModel.addLog("Save failed: Please load and select a source first.")
                (requireActivity() as? MainActivity)?.binding?.viewPager?.currentItem = 2 // Switch to logcat tab to see the error
            }
        }
    }

    private fun setupObservers() {
        viewModel.sources.observe(viewLifecycleOwner) { sources ->
            sourcesAdapter.setSources(sources)
            val savedSourceName = getEncryptedSharedPreferences().getString("selected_source_name", null)
            if (savedSourceName != null) {
                sourcesAdapter.setSelectedSource(savedSourceName)
            }
        }
    }

    private fun getEncryptedSharedPreferences(): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            "JulesTestApp-Settings",
            masterKeyAlias,
            requireContext(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun saveSettings(apiKey: String, sourceName: String) {
        getEncryptedSharedPreferences().edit()
            .putString("api_key", apiKey)
            .putString("selected_source_name", sourceName)
            .apply()
    }

    private fun loadSettings() {
        val sharedPreferences = getEncryptedSharedPreferences()
        val apiKey = sharedPreferences.getString("api_key", "")
        if (!apiKey.isNullOrBlank()) {
            binding.apiKeyEdittext.setText(apiKey)
            viewModel.initializeClient(apiKey)
            viewModel.loadSources()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
