package com.example.pembelajaranmandirippk2025.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pembelajaranmandirippk2025.R
import com.example.pembelajaranmandirippk2025.StatusPertemuanDTO
import com.example.pembelajaranmandirippk2025.adapter.StatusPertemuanDosenAdapter
import com.example.pembelajaranmandirippk2025.api.ApiResult
import com.example.pembelajaranmandirippk2025.databinding.FragmentStatusPertemuanDosenBinding
import com.example.pembelajaranmandirippk2025.util.SessionManager
import com.example.pembelajaranmandirippk2025.viewmodel.StatusPertemuanViewModel
import com.example.pembelajaranmandirippk2025.viewmodel.StatusPertemuanViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class StatusPertemuanDosenFragment : Fragment() {
    private var _binding: FragmentStatusPertemuanDosenBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var statusAdapter: StatusPertemuanDosenAdapter
    private var selectedPertemuanId: Long = -1L
    private var currentPertemuanId: Long = -1L

    private val viewModel: StatusPertemuanViewModel by viewModels {
        StatusPertemuanViewModelFactory(SessionManager(requireContext()))
    }

    private fun updateSelectedPertemuanId(id: Long) {
        selectedPertemuanId = id
        if (id != -1L) {
            viewModel.loadStatusByPertemuan(id)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            _binding = FragmentStatusPertemuanDosenBinding.inflate(inflater, container, false)
            sessionManager = SessionManager(requireContext())
            return binding.root
        } catch (e: Exception) {
            throw e
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupPertemuanSpinner()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        statusAdapter = StatusPertemuanDosenAdapter { status ->
            showUpdateScoreDialog(status)
        }
        binding.rvStatusPertemuan.apply {
            adapter = statusAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupPertemuanSpinner() {
        viewModel.loadPertemuan()
        viewModel.pertemuanList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {
                    binding.progressCircular.visibility = View.GONE
                    if (result.data.isNotEmpty()) {
                        val pertemuanItems = result.data.map { it.namaPertemuan }
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            pertemuanItems
                        )
                        binding.spinnerPertemuan.setAdapter(adapter)

                        // Otomatis memilih pertemuan pertama
                        binding.spinnerPertemuan.setText(pertemuanItems[0], false)
                        result.data[0].id?.let { pertemuanId ->
                            currentPertemuanId = pertemuanId
                            binding.progressCircular.visibility = View.VISIBLE
                            binding.emptyState.visibility = View.GONE
                            binding.rvStatusPertemuan.visibility = View.GONE
                            
                            viewModel.loadStatusByPertemuan(pertemuanId)
                        }

                        binding.spinnerPertemuan.setOnItemClickListener { _, _, position, _ ->
                            if (position >= 0 && position < result.data.size) {
                                val selectedPertemuan = result.data[position]
                                selectedPertemuan.id?.let { pertemuanId ->
                                    currentPertemuanId = pertemuanId
                                    binding.progressCircular.visibility = View.VISIBLE
                                    binding.emptyState.visibility = View.GONE
                                    binding.rvStatusPertemuan.visibility = View.GONE
                                    
                                    viewModel.loadStatusByPertemuan(pertemuanId)
                                }
                            }
                        }
                    }
                }
                is ApiResult.Error -> {
                    binding.progressCircular.visibility = View.GONE
                    Snackbar.make(
                        binding.root,
                        "Gagal memuat daftar pertemuan: ${result.exception.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                is ApiResult.Loading -> {
                    binding.progressCircular.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showUpdateScoreDialog(status: StatusPertemuanDTO) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_score, null)
        val etPraktikum = dialogView.findViewById<TextInputEditText>(R.id.etPraktikumScore)
        val etKuis = dialogView.findViewById<TextInputEditText>(R.id.etQuizScore)

        // Set current scores
        etPraktikum.setText(status.skorPraktikum?.toString() ?: "")
        etKuis.setText(status.skorKuis?.toString() ?: "")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Update Scores")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val updatedStatus = status.copy(
                    skorPraktikum = etPraktikum.text.toString().toIntOrNull(),
                    skorKuis = etKuis.text.toString().toIntOrNull()
                )
                viewModel.updateStatus(updatedStatus, currentPertemuanId)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun observeViewModel() {
        viewModel.statusList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {
                    binding.progressCircular.visibility = View.GONE
                    if (result.data.isEmpty()) {
                        binding.emptyState.visibility = View.VISIBLE
                        binding.rvStatusPertemuan.visibility = View.GONE
                    } else {
                        binding.emptyState.visibility = View.GONE
                        binding.rvStatusPertemuan.visibility = View.VISIBLE
                        statusAdapter.submitList(result.data)
                    }
                }
                is ApiResult.Error -> {
                    binding.progressCircular.visibility = View.GONE
                    Snackbar.make(
                        binding.root,
                        "Terjadi kesalahan: ${result.exception.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                is ApiResult.Loading -> {
                    binding.progressCircular.visibility = View.VISIBLE
                    binding.emptyState.visibility = View.GONE
                    binding.rvStatusPertemuan.visibility = View.GONE
                }
            }
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {
                    if (currentPertemuanId != -1L) {
                        viewModel.loadStatusByPertemuan(currentPertemuanId)
                    }
                    Snackbar.make(binding.root, "Skor berhasil diperbarui", Snackbar.LENGTH_SHORT).show()
                }
                is ApiResult.Error -> {
                    Snackbar.make(
                        binding.root,
                        "Gagal memperbarui skor: ${result.exception.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                is ApiResult.Loading -> {
                    // Optional: tampilkan loading indicator jika diperlukan
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}