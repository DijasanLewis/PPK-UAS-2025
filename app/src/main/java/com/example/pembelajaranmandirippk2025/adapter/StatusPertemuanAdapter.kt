package com.example.pembelajaranmandirippk2025.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pembelajaranmandirippk2025.StatusPertemuanDTO
import com.example.pembelajaranmandirippk2025.R
import com.example.pembelajaranmandirippk2025.databinding.ItemStatusPertemuanBinding

class StatusPertemuanAdapter(
    private val onStatusClick: (StatusPertemuanDTO) -> Unit
) : ListAdapter<Pair<StatusPertemuanDTO, String>, StatusPertemuanAdapter.StatusViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val binding = ItemStatusPertemuanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StatusViewHolder(binding, onStatusClick)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class StatusViewHolder(
        private val binding: ItemStatusPertemuanBinding,
        private val onStatusClick: (StatusPertemuanDTO) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pair: Pair<StatusPertemuanDTO, String>) {
            val (status, pertemuanName) = pair
            binding.apply {
                tvNamaPertemuan.text = pertemuanName
                
                // Set skor praktikum
                tvSkorPraktikum.text = status.skorPraktikum?.toString() ?: "-"
                
                // Set skor kuis
                tvSkorKuis.text = status.skorKuis?.toString() ?: "-"
                
                // Set chips dengan warna yang sesuai
                chipStatusMateri.apply {
                    text = status.statusMateri
                    setChipBackgroundColorResource(
                        when(status.statusMateri) {
                            "Sudah" -> R.color.green_500
                            else -> R.color.red_500
                        }
                    )
                }
                
                chipStatusPengumpulan.apply {
                    text = status.statusPengumpulan
                    setChipBackgroundColorResource(
                        when(status.statusPengumpulan) {
                            "Sudah" -> {
                                if (status.skorPraktikum != null) R.color.green_500
                                else R.color.orange_500 // Sudah mengumpulkan tapi belum dinilai
                            }
                            else -> R.color.red_500
                        }
                    )
                }
                
                chipStatusKuis.apply {
                    text = status.statusKuis
                    setChipBackgroundColorResource(
                        when(status.statusKuis) {
                            "Sudah" -> {
                                if (status.skorKuis != null) R.color.green_500
                                else R.color.orange_500 // Sudah mengerjakan tapi belum dinilai
                            }
                            else -> R.color.red_500
                        }
                    )
                }

                btnUpdateStatus.setOnClickListener {
                    onStatusClick(status)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Pair<StatusPertemuanDTO, String>>() {
            override fun areItemsTheSame(oldItem: Pair<StatusPertemuanDTO, String>, newItem: Pair<StatusPertemuanDTO, String>): Boolean {
                return oldItem.first.id == newItem.first.id
            }

            override fun areContentsTheSame(oldItem: Pair<StatusPertemuanDTO, String>, newItem: Pair<StatusPertemuanDTO, String>): Boolean {
                return oldItem == newItem
            }
        }
    }
}