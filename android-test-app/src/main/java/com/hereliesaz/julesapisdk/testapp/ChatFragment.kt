package com.hereliesaz.julesapisdk.testapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hereliesaz.julesapisdk.testapp.databinding.FragmentChatBinding
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter()
        binding.messagesRecyclerview.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                if (uiState is UiState.Chat) {
                    messageAdapter.submitList(uiState.messages)
                    if (uiState.messages.isNotEmpty()) {
                        binding.messagesRecyclerview.scrollToPosition(uiState.messages.size - 1)
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.sendButton.setOnClickListener {
            val message = binding.messageEdittext.text.toString()
            if (message.isNotBlank()) {
                viewModel.sendMessage(message)
                binding.messageEdittext.text.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
