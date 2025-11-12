package com.hereliesaz.julesapisdk.testapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.hereliesaz.julesapisdk.testapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    internal lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        // Use the new adapter for 3 tabs
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        // Set the tab titles for all 3 tabs
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Chat"
                1 -> "Settings"
                2 -> "Logcat"
                else -> null
            }
        }.attach()
    }
}
