package com.example.pembelajaranmandirippk2025.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pembelajaranmandirippk2025.PertemuanDTO
import com.example.pembelajaranmandirippk2025.databinding.ItemPertemuanBinding
import com.google.android.material.snackbar.Snackbar

class PertemuanAdapter : ListAdapter<PertemuanDTO, PertemuanAdapter.PertemuanViewHolder>(DIFF_CALLBACK) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PertemuanViewHolder {
        val binding = ItemPertemuanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PertemuanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PertemuanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PertemuanViewHolder(private val binding: ItemPertemuanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pertemuan: PertemuanDTO) {
            binding.apply {
                tvNamaPertemuan.text = pertemuan.namaPertemuan
                
                // Setup button clicks dengan pengecekan null/empty
                btnMateri.setOnClickListener {
                    if (!pertemuan.linkMateri.isNullOrEmpty()) {
                        openUrl(itemView.context, pertemuan.linkMateri)
                    } else {
                        showSnackbar(itemView, "Link materi belum tersedia")
                    }
                }

                btnPraktikum.setOnClickListener {
                    if (!pertemuan.linkPraktikum.isNullOrEmpty()) {
                        openUrl(itemView.context, pertemuan.linkPraktikum)
                    } else {
                        showSnackbar(itemView, "Link praktikum belum tersedia")
                    }
                }

                btnKuis.setOnClickListener {
                    if (!pertemuan.linkKuis.isNullOrEmpty()) {
                        openUrl(itemView.context, pertemuan.linkKuis)
                    } else {
                        showSnackbar(itemView, "Link kuis belum tersedia")
                    }
                }

                // Set button states
                btnMateri.isEnabled = !pertemuan.linkMateri.isNullOrEmpty()
                btnPraktikum.isEnabled = !pertemuan.linkPraktikum.isNullOrEmpty()
                btnKuis.isEnabled = !pertemuan.linkKuis.isNullOrEmpty()
            }
        }

        private fun openUrl(context: Context, url: String) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (e: Exception) {
                showSnackbar(itemView, "Tidak dapat membuka link")
            }
        }

        private fun showSnackbar(view: View, message: String) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
        }
    }
}