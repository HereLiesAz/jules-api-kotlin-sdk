package com.hereliesaz.julesapisdk.testapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hereliesaz.julesapisdk.JulesClient
import com.hereliesaz.julesapisdk.testapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val julesClient by lazy {
        JulesClient(BuildConfig.API_KEY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.callSdkButton.setOnClickListener {
            callSdk()
        }
    }

    private fun callSdk() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val sources = julesClient.listSources()
                withContext(Dispatchers.Main) {
                    binding.resultTextview.text = sources.toString()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.resultTextview.text = e.message
                }
            }
        }
    }
}
