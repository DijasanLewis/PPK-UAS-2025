package com.example.pembelajaranmandirippk2025.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pembelajaranmandirippk2025.adapter.PertemuanAdapter
import com.example.pembelajaranmandirippk2025.api.ApiResult
import com.example.pembelajaranmandirippk2025.databinding.FragmentPertemuanListBinding
import com.example.pembelajaranmandirippk2025.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

class PertemuanListFragment : Fragment() {
    private var _binding: FragmentPertemuanListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var pertemuanAdapter: PertemuanAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPertemuanListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        viewModel.loadPertemuan()
    }

    private fun setupRecyclerView() {
        pertemuanAdapter = PertemuanAdapter()
        binding.rvPertemuan.apply {
            adapter = pertemuanAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewModel.pertemuanList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {
                    pertemuanAdapter.submitList(result.data)
                }
                is ApiResult.Error -> {
                    Snackbar.make(
                        binding.root,
                        "Error: ${result.exception.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                is ApiResult.Loading -> {
                    // Show loading indicator
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}