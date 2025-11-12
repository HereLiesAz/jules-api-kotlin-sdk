package com.hereliesaz.julesapisdk.testapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hereliesaz.julesapisdk.testapp.databinding.FragmentLogcatBinding

class LogcatFragment : Fragment() {

    private var _binding: FragmentLogcatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var logcatAdapter: LogcatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogcatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        logcatAdapter = LogcatAdapter()
        binding.logcatRecyclerview.apply {
            adapter = logcatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        viewModel.diagnosticLogs.observe(viewLifecycleOwner) { logs ->
            logcatAdapter.submitList(logs.toList()) // submitList needs a new list to calculate diff
            if (logs.isNotEmpty()) {
                binding.logcatRecyclerview.scrollToPosition(0) // Scroll to the top to see the newest log
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
