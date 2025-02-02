package com.example.pembelajaranmandirippk2025.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pembelajaranmandirippk2025.R
import com.example.pembelajaranmandirippk2025.adapter.PertemuanDosenAdapter
import com.example.pembelajaranmandirippk2025.api.ApiResult
import com.example.pembelajaranmandirippk2025.databinding.FragmentPertemuanListDosenBinding
import com.example.pembelajaranmandirippk2025.PertemuanDTO
import com.example.pembelajaranmandirippk2025.viewmodel.PertemuanViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class PertemuanListDosenFragment : Fragment() {
    private var _binding: FragmentPertemuanListDosenBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PertemuanViewModel by viewModels()
    private lateinit var pertemuanAdapter: PertemuanDosenAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPertemuanListDosenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFAB()
        observeViewModel()
        viewModel.loadPertemuan()
    }

    private fun setupRecyclerView() {
        pertemuanAdapter = PertemuanDosenAdapter(
            onEditClick = { pertemuan -> showEditDialog(pertemuan) },
            onDeleteClick = { pertemuan -> showDeleteConfirmation(pertemuan) }
        )
        binding.rvPertemuan.apply {
            adapter = pertemuanAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupFAB() {
        binding.fabAddPertemuan.setOnClickListener {
            showAddDialog()
        }
    }

    private fun showAddDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_pertemuan, null)
        val etNamaPertemuan = dialogView.findViewById<TextInputEditText>(R.id.etNamaPertemuan)
        val etLinkMateri = dialogView.findViewById<TextInputEditText>(R.id.etLinkMateri)
        val etLinkPraktikum = dialogView.findViewById<TextInputEditText>(R.id.etLinkPraktikum)
        val etLinkKuis = dialogView.findViewById<TextInputEditText>(R.id.etLinkKuis)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Tambah Pertemuan")
            .setView(dialogView)
            .setPositiveButton("Tambah") { dialog, _ ->
                val pertemuan = PertemuanDTO(
                    id = null,
                    namaPertemuan = etNamaPertemuan.text.toString(),
                    linkMateri = etLinkMateri.text.toString().takeIf { it.isNotEmpty() },
                    linkPraktikum = etLinkPraktikum.text.toString().takeIf { it.isNotEmpty() },
                    linkKuis = etLinkKuis.text.toString().takeIf { it.isNotEmpty() }
                )
                viewModel.createPertemuan(pertemuan)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showDeleteConfirmation(pertemuan: PertemuanDTO) {
        pertemuan.id?.let { pertemuanId ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.delete_pertemuan))
                .setMessage(getString(R.string.delete_confirmation))
                .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                    viewModel.deletePertemuan(pertemuanId)
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                .show()
        } ?: run {
            Snackbar.make(
                binding.root,
                "Tidak dapat menghapus pertemuan: ID tidak valid",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun showEditDialog(pertemuan: PertemuanDTO) {
        pertemuan.id?.let { pertemuanId ->
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_pertemuan, null)
            val etNamaPertemuan = dialogView.findViewById<TextInputEditText>(R.id.etNamaPertemuan)
            val etLinkMateri = dialogView.findViewById<TextInputEditText>(R.id.etLinkMateri)
            val etLinkPraktikum = dialogView.findViewById<TextInputEditText>(R.id.etLinkPraktikum)
            val etLinkKuis = dialogView.findViewById<TextInputEditText>(R.id.etLinkKuis)

            etNamaPertemuan.setText(pertemuan.namaPertemuan)
            etLinkMateri.setText(pertemuan.linkMateri ?: "")
            etLinkPraktikum.setText(pertemuan.linkPraktikum ?: "")
            etLinkKuis.setText(pertemuan.linkKuis ?: "")

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.edit_pertemuan))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.save)) { dialog, _ ->
                    val updatedPertemuan = pertemuan.copy(
                        namaPertemuan = etNamaPertemuan.text.toString(),
                        linkMateri = etLinkMateri.text.toString().takeIf { it.isNotEmpty() },
                        linkPraktikum = etLinkPraktikum.text.toString().takeIf { it.isNotEmpty() },
                        linkKuis = etLinkKuis.text.toString().takeIf { it.isNotEmpty() }
                    )
                    viewModel.updatePertemuan(updatedPertemuan)
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun observeViewModel() {
        viewModel.pertemuanList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {
                    pertemuanAdapter.submitList(result.data)
                    binding.progressCircular.visibility = View.GONE
                }
                is ApiResult.Error -> {
                    Snackbar.make(
                        binding.root,
                        "Error: ${result.exception.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                    binding.progressCircular.visibility = View.GONE
                }
                is ApiResult.Loading -> {
                    binding.progressCircular.visibility = View.VISIBLE
                }
            }
        }

        viewModel.operationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {
                    Snackbar.make(
                        binding.root,
                        "Operasi berhasil",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    viewModel.loadPertemuan()
                }
                is ApiResult.Error -> {
                    Snackbar.make(
                        binding.root,
                        "Error: ${result.exception.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                is ApiResult.Loading -> {
                    // Show loading if needed
                }
            }
        }
        viewModel.operationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {
                    Snackbar.make(
                        binding.root,
                        "Operasi berhasil",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    viewModel.loadPertemuan()
                }
                is ApiResult.Error -> {
                    val errorMessage = when {
                        result.exception.message?.contains("403") == true ->
                            "Anda tidak memiliki izin untuk menghapus pertemuan ini"
                        result.exception.message?.contains("404") == true ->
                            "Pertemuan tidak ditemukan"
                        else -> "Error: ${result.exception.message}"
                    }
                    Snackbar.make(
                        binding.root,
                        errorMessage,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                is ApiResult.Loading -> {
                    // Show loading indicator if needed
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
