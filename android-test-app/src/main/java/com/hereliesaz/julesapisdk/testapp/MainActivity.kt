package com.hereliesaz.julesapisdk.testapp

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.hereliesaz.julesapisdk.JulesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : Activity() {

    private lateinit var callSdkButton: Button
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        callSdkButton = findViewById(R.id.call_sdk_button)
        resultTextView = findViewById(R.id.result_textview)

        callSdkButton.setOnClickListener {
            callSdk()
        }
    }

    private fun callSdk() {
        // Replace with your actual API key
        val apiKey = "YOUR_API_KEY"
        val client = JulesClient(apiKey)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val sources = client.listSources()
                withContext(Dispatchers.Main) {
                    resultTextView.text = sources.toString()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    resultTextView.text = e.message
                }
            }
        }
    }
}
