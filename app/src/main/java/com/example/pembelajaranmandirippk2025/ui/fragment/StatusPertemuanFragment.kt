package com.example.pembelajaranmandirippk2025.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pembelajaranmandirippk2025.StatusPertemuanDTO
import com.example.pembelajaranmandirippk2025.adapter.StatusPertemuanAdapter
import com.example.pembelajaranmandirippk2025.api.ApiResult
import com.example.pembelajaranmandirippk2025.databinding.FragmentStatusPertemuanBinding
import com.example.pembelajaranmandirippk2025.util.SessionManager
import com.example.pembelajaranmandirippk2025.viewmodel.StatusPertemuanViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.example.pembelajaranmandirippk2025.R
import com.example.pembelajaranmandirippk2025.databinding.FragmentStatusPertemuanDosenBinding
import com.example.pembelajaranmandirippk2025.viewmodel.StatusPertemuanViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDateTime

class StatusPertemuanFragment : Fragment() {
    private var _binding: FragmentStatusPertemuanBinding? = null
    private val binding get() = _binding!!
    private lateinit var statusAdapter: StatusPertemuanAdapter
    private lateinit var sessionManager: SessionManager

    private val viewModel: StatusPertemuanViewModel by viewModels {
        StatusPertemuanViewModelFactory(SessionManager(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatusPertemuanBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        loadStatusPertemuan()
        viewModel.loadPertemuan() // Load pertemuan list first
    }

    private fun loadStatusPertemuan() {
        val userId = sessionManager.getUserId()
        Log.d("StatusPertemuanFragment", "Loading status pertemuan for mahasiswa with ID: $userId")
        // Tambahkan null check
        if (userId != -1L) {  // -1L adalah default value jika user id tidak ditemukan
            viewModel.loadStatusByMahasiswa(mahasiswaId = userId)
        } else {
            // Handle kasus ketika userId tidak valid
            Snackbar.make(
                binding.root,
                "User ID tidak valid. Silakan login ulang.",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun setupRecyclerView() {
        statusAdapter = StatusPertemuanAdapter { status ->
            showUpdateStatusDialog(status)
        }
        binding.rvStatusPertemuan.apply {
            adapter = statusAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewModel.pertemuanList.observe(viewLifecycleOwner) { pertemuanResult ->
            when (pertemuanResult) {
                is ApiResult.Success -> {
                    val pertemuanMap = pertemuanResult.data
                        .mapNotNull { pertemuan ->
                            pertemuan.id?.let { id -> id to pertemuan }
                        }
                        .toMap()
                    viewModel.setPertemuanMap(pertemuanMap)
                }
                is ApiResult.Error -> {
                    Log.e("StatusPertemuanFragment", "Error loading pertemuan: ${pertemuanResult.exception.message}")
                }
                else -> {}
            }
        }

        viewModel.statusList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {
                    Log.d("StatusPertemuanFragment", "Received ${result.data.size} status entries")
                    val statusWithNames = result.data.map { status ->
                        val pertemuanName = viewModel.getPertemuanName(status.pertemuanId) ?: "Unknown"
                        Pair(status, pertemuanName)
                    }
                    statusAdapter.submitList(statusWithNames)
                    binding.progressCircular.visibility = View.GONE
                }
                is ApiResult.Error -> {
                    Log.e("StatusPertemuanFragment", "Error loading status pertemuan: ${result.exception.message}")
                    Snackbar.make(binding.root, "Error: ${result.exception.message}", Snackbar.LENGTH_LONG).show()
                    binding.progressCircular.visibility = View.GONE
                }
                is ApiResult.Loading -> {
                    binding.progressCircular.visibility = View.VISIBLE
                }
            }
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {
                    Log.d("StatusPertemuanFragment", "Status pertemuan updated successfully")
                    Snackbar.make(
                        binding.root,
                        "Status berhasil diperbarui",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                is ApiResult.Error -> {
                    Log.e("StatusPertemuanFragment", "Error updating status pertemuan: ${result.exception.message}")
                    Snackbar.make(
                        binding.root,
                        "Error: ${result.exception.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                is ApiResult.Loading -> {
                    // Handle loading state jika diperlukan
                }
            }
        }
    }

    private fun showUpdateStatusDialog(status: StatusPertemuanDTO) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_status, null)

        // Get references to all views
        val etLinkPraktikum = dialogView.findViewById<TextInputEditText>(R.id.etLinkPraktikum)
        val rgStatusMateri = dialogView.findViewById<RadioGroup>(R.id.rgStatusMateri)
        val rgStatusPengumpulan = dialogView.findViewById<RadioGroup>(R.id.rgStatusPengumpulan)
        val rgStatusKuis = dialogView.findViewById<RadioGroup>(R.id.rgStatusKuis)

        // Set current values
        etLinkPraktikum.setText(status.linkPengerjaanPraktikum ?: "")

        // Set radio button states
        when (status.statusMateri) {
            "Belum" -> dialogView.findViewById<RadioButton>(R.id.rbStatusMateriBelum).isChecked = true
            "Sudah" -> dialogView.findViewById<RadioButton>(R.id.rbStatusMateriSudah).isChecked = true
        }

        when (status.statusPengumpulan) {
            "Belum" -> dialogView.findViewById<RadioButton>(R.id.rbStatusPengumpulanBelum).isChecked = true
            "Sudah" -> dialogView.findViewById<RadioButton>(R.id.rbStatusPengumpulanSudah).isChecked = true
        }

        when (status.statusKuis) {
            "Belum" -> dialogView.findViewById<RadioButton>(R.id.rbStatusKuisBelum).isChecked = true
            "Sudah" -> dialogView.findViewById<RadioButton>(R.id.rbStatusKuisSudah).isChecked = true
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Update Status")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val updatedStatus = status.copy(
                    linkPengerjaanPraktikum = etLinkPraktikum.text.toString(),
                    statusMateri = if (rgStatusMateri.checkedRadioButtonId == R.id.rbStatusMateriSudah) "Sudah" else "Belum",
                    statusPengumpulan = if (rgStatusPengumpulan.checkedRadioButtonId == R.id.rbStatusPengumpulanSudah) "Sudah" else "Belum",
                    statusKuis = if (rgStatusKuis.checkedRadioButtonId == R.id.rbStatusKuisSudah) "Sudah" else "Belum"
                )
                viewModel.updateStatus(updatedStatus)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}