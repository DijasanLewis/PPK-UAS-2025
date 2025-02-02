package com.example.pembelajaranmandirippk2025.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pembelajaranmandirippk2025.UserDTO
import com.example.pembelajaranmandirippk2025.api.ApiResult
import com.example.pembelajaranmandirippk2025.databinding.ActivityRegisterBinding
import com.example.pembelajaranmandirippk2025.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
        setupToolbar()
    }

    private fun setupUI() {
        with(binding) {
            // Initialize radio buttons to be unselected
            rbDosen.isChecked = false
            rbMahasiswa.isChecked = false

            // Set initial visibility to GONE for additional fields
            layoutLecturer.root.visibility = View.GONE
            layoutStudent.root.visibility = View.GONE

            // Handle radio button changes for role selection
            rbDosen.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    layoutLecturer.root.visibility = View.VISIBLE
                    layoutStudent.root.visibility = View.GONE
                    rbMahasiswa.isChecked = false // Unselect Mahasiswa radio button
                }
            }

            rbMahasiswa.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    layoutStudent.root.visibility = View.VISIBLE
                    layoutLecturer.root.visibility = View.GONE
                    rbDosen.isChecked = false // Unselect Dosen radio button
                }
            }

            btnRegister.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val role = if (rbDosen.isChecked) "DOSEN" else "MAHASISWA"
                val namaLengkap = etNamaLengkap.text.toString()

                // Get NIDN or NIM and Kelas based on role
                val nidn = if (role == "DOSEN") layoutLecturer.etNidn.text.toString() else null
                val nim = if (role == "MAHASISWA") layoutStudent.etNim.text.toString() else null
                val kelas = if (role == "MAHASISWA") layoutStudent.etKelas.text.toString() else null

                // Validasi input
                if (email.isEmpty() || password.isEmpty() || namaLengkap.isEmpty()) {
                    Toast.makeText(this@RegisterActivity, "Mohon lengkapi semua kolom yang diperlukan", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Validasi tambahan berdasarkan role
                if (role == "DOSEN" && nidn.isNullOrEmpty()) {
                    Toast.makeText(this@RegisterActivity, "Mohon masukkan NIDN Anda", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (role == "MAHASISWA" && (nim.isNullOrEmpty() || kelas.isNullOrEmpty())) {
                    Toast.makeText(this@RegisterActivity, "Mohon lengkapi NIM dan Kelas Anda", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val user = UserDTO(
                    email = email,
                    password = password,
                    role = role,
                    namaLengkap = namaLengkap,
                    nidn = nidn,
                    nim = nim,
                    kelas = kelas
                )

                viewModel.register(user)
            }
        }
    }

    private fun setupToolbar() {
        // Set up the back button in the toolbar
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle the back button click
        toolbar.setNavigationOnClickListener {
            // Navigate back to LoginActivity
            finish()  // or you can use Intent to open LoginActivity if needed
        }
    }

    private fun observeViewModel() {
        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is ApiResult.Success -> {
                    Toast.makeText(this, "Pendaftaran berhasil! Silakan masuk dengan akun Anda", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is ApiResult.Error -> {
                    val errorMessage = when {
                        result.exception.message?.contains("409") == true -> "Email sudah terdaftar"
                        result.exception.message?.contains("Connection") == true -> "Gagal terhubung ke server"
                        else -> "Pendaftaran gagal: ${result.exception.message}"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
                ApiResult.Loading -> {
                    // Tampilkan loading indicator jika diperlukan
                }
            }
        }
    }
}