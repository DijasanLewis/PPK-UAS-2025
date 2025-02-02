package com.example.pembelajaranmandirippk2025.adapter

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pembelajaranmandirippk2025.R
import com.example.pembelajaranmandirippk2025.StatusPertemuanDTO
import com.example.pembelajaranmandirippk2025.databinding.ItemStatusPertemuanDosenBinding

class StatusPertemuanDosenAdapter(
    private val onUpdateScoreClick: (StatusPertemuanDTO) -> Unit
) : ListAdapter<StatusPertemuanDTO, StatusPertemuanDosenAdapter.StatusViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val binding = ItemStatusPertemuanDosenBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StatusViewHolder(binding, onUpdateScoreClick)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class StatusViewHolder(
        private val binding: ItemStatusPertemuanDosenBinding,
        private val onUpdateScoreClick: (StatusPertemuanDTO) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(status: StatusPertemuanDTO) {
            binding.apply {
                tvNamaMahasiswa.text = "Nama: ${status.namaLengkapMahasiswa}"

                // Status dengan warna yang sesuai
                tvStatusMateri.apply {
                    text = status.statusMateri
                    setTextColor(when(status.statusMateri) {
                        "Sudah" -> context.getColor(R.color.green_500)
                        else -> context.getColor(R.color.red_500)
                    })
                }

                tvStatusPengumpulan.apply {
                    text = status.statusPengumpulan
                    setTextColor(when(status.statusPengumpulan) {
                        "Sudah" -> context.getColor(R.color.green_500)
                        else -> context.getColor(R.color.red_500)
                    })
                }

                tvStatusKuis.apply {
                    text = status.statusKuis
                    setTextColor(when(status.statusKuis) {
                        "Sudah" -> context.getColor(R.color.green_500)
                        else -> context.getColor(R.color.red_500)
                    })
                }

                // Link Praktikum
                if (status.linkPengerjaanPraktikum.isNullOrEmpty()) {
                    cardLinkPraktikum.visibility = View.GONE
                } else {
                    cardLinkPraktikum.visibility = View.VISIBLE
                    btnLinkPraktikum.setOnClickListener {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(status.linkPengerjaanPraktikum))
                            itemView.context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                itemView.context,
                                "Tidak dapat membuka link: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                // Format skor
                tvSkorPraktikum.text = status.skorPraktikum?.toString() ?: "Belum dinilai"
                tvSkorKuis.text = status.skorKuis?.toString() ?: "Belum dinilai"

                btnUpdateSkor.setOnClickListener {
                    onUpdateScoreClick(status)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StatusPertemuanDTO>() {
            override fun areItemsTheSame(oldItem: StatusPertemuanDTO, newItem: StatusPertemuanDTO): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StatusPertemuanDTO, newItem: StatusPertemuanDTO): Boolean {
                return oldItem == newItem
            }
        }
    }
}