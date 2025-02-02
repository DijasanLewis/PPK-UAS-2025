package com.example.pembelajaranmandirippk2025.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pembelajaranmandirippk2025.PertemuanDTO
import com.example.pembelajaranmandirippk2025.databinding.ItemPertemuanDosenBinding
import com.google.android.material.button.MaterialButton

class PertemuanDosenAdapter(
    private val onEditClick: (PertemuanDTO) -> Unit,
    private val onDeleteClick: (PertemuanDTO) -> Unit
) : ListAdapter<PertemuanDTO, PertemuanDosenAdapter.PertemuanViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PertemuanViewHolder {
        val binding = ItemPertemuanDosenBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PertemuanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PertemuanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PertemuanViewHolder(
        private val binding: ItemPertemuanDosenBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pertemuan: PertemuanDTO) {
            binding.apply {
                tvNamaPertemuan.text = pertemuan.namaPertemuan

                // Setup link buttons
                btnMateri.apply {
                    text = "Materi"
                    isEnabled = !pertemuan.linkMateri.isNullOrEmpty()
                    setOnClickListener {
                        pertemuan.linkMateri?.let { url ->
                            openUrl(url)
                        }
                    }
                }

                btnPraktikum.apply {
                    text = "Praktikum"
                    isEnabled = !pertemuan.linkPraktikum.isNullOrEmpty()
                    setOnClickListener {
                        pertemuan.linkPraktikum?.let { url ->
                            openUrl(url)
                        }
                    }
                }

                btnKuis.apply {
                    text = "Kuis"
                    isEnabled = !pertemuan.linkKuis.isNullOrEmpty()
                    setOnClickListener {
                        pertemuan.linkKuis?.let { url ->
                            openUrl(url)
                        }
                    }
                }

                btnEdit.setOnClickListener { onEditClick(pertemuan) }
                btnDelete.setOnClickListener { onDeleteClick(pertemuan) }
            }
        }

        private fun openUrl(url: String) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            binding.root.context.startActivity(intent)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PertemuanDTO>() {
            override fun areItemsTheSame(oldItem: PertemuanDTO, newItem: PertemuanDTO): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PertemuanDTO, newItem: PertemuanDTO): Boolean {
                return oldItem == newItem
            }
        }
    }
}