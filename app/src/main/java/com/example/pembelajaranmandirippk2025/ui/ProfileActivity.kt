package com.example.pembelajaranmandirippk2025.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pembelajaranmandirippk2025.R
import com.example.pembelajaranmandirippk2025.UserDTO
import com.example.pembelajaranmandirippk2025.api.ApiResult
import com.example.pembelajaranmandirippk2025.databinding.ActivityProfileBinding
import com.example.pembelajaranmandirippk2025.util.SessionManager
import com.example.pembelajaranmandirippk2025.viewmodel.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(this)
    }
    private lateinit var sessionManager: SessionManager
    private var currentUser: UserDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding and session manager
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Setup UI listeners
        setupUI()

        // Observe ViewModel
        observeViewModel()

        // Load user profile
        loadUserProfile()
    }

    private fun setupUI() {
        // Back button
        binding.topAppBar.setNavigationOnClickListener { finish() }

        // Edit profile toggle
        binding.btnEditProfile.setOnClickListener {
            toggleEditMode(true)
        }

        // Cancel edit
        binding.btnCancelEdit.setOnClickListener {
            toggleEditMode(false)
            populateUserData()
        }

        // Save profile
        binding.btnSaveProfile.setOnClickListener {
            saveProfile() // Panggil metode saveProfile
        }

        // Change password
        binding.btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }
    }

    private fun loadUserProfile() {
        viewModel.loadUserProfile()
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(this) { result ->
            when (result) {
                is ApiResult.Success -> {
                    currentUser = result.data
                    populateUserData()
                    binding.progressBar.isVisible = false
                }
                is ApiResult.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is ApiResult.Error -> {
                    Toast.makeText(
                        this,
                        "Gagal memuat profil: ${result.exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressBar.isVisible = false
                }
            }
        }

        viewModel.updateProfileResult.observe(this) { result ->
            when (result) {
                is ApiResult.Success -> {
                    Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    currentUser = result.data
                    populateUserData()
                    toggleEditMode(false)
                    binding.progressBar.isVisible = false
                }
                is ApiResult.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is ApiResult.Error -> {
                    Toast.makeText(
                        this,
                        "Gagal memperbarui profil: ${result.exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressBar.isVisible = false
                }
            }
        }

        // Tambahkan observer untuk changePasswordResult
        viewModel.changePasswordResult.observe(this) { result ->
            when (result) {
                is ApiResult.Success -> {
                    Toast.makeText(this, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                }
                is ApiResult.Error -> {
                    Toast.makeText(
                        this,
                        "Gagal mengubah password: ${result.exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {}
            }
        }
    }


    private fun populateUserData() {
        currentUser?.let { user ->
            with(binding) {
                // Pastikan semua field tidak bisa diedit saat pertama kali
                etEmail.isEnabled = false
                etNamaLengkap.isEnabled = false

                etEmail.setText(user.email)
                etNamaLengkap.setText(user.namaLengkap ?: "")

                when (user.role) {
                    "DOSEN" -> {
                        tilNidn.isVisible = true
                        tilNim.isVisible = false
                        tilKelas.isVisible = false
                        etNidn.setText(user.nidn ?: "")
                        etNidn.isEnabled = false  // Pastikan NIDN tidak bisa diedit
                    }
                    "MAHASISWA" -> {
                        tilNidn.isVisible = false
                        tilNim.isVisible = true
                        tilKelas.isVisible = true
                        etNim.setText(user.nim ?: "")
                        etKelas.setText(user.kelas ?: "")
                        // Pastikan NIM dan Kelas tidak bisa diedit
                        etNim.isEnabled = false
                        etKelas.isEnabled = false
                    }
                    else -> {
                        // Handle other roles if needed
                    }
                }
                
                // Pastikan tombol edit terlihat dan tombol save/cancel tersembunyi
                btnEditProfile.isVisible = true
                editButtonsLayout.isVisible = false
            }
        } ?: run {
            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleEditMode(isEditable: Boolean) {
        binding.apply {
            etEmail.isEnabled = false // Email should never be editable
            etNamaLengkap.isEnabled = isEditable

            when (currentUser?.role) {
                "DOSEN" -> {
                    etNidn.isEnabled = isEditable
                    tilNim.isVisible = false
                    tilKelas.isVisible = false
                }
                "MAHASISWA" -> {
                    tilNidn.isVisible = false
                    etNim.isEnabled = isEditable
                    etKelas.isEnabled = isEditable
                }
            }

            // Show/hide the appropriate buttons
            btnEditProfile.isVisible = !isEditable
            editButtonsLayout.isVisible = isEditable // Add this line
        }
    }

    private fun saveProfile() {
        currentUser?.let { user ->
            val updatedUser = UserDTO(
                id = user.id,
                email = user.email,
                role = user.role,
                namaLengkap = binding.etNamaLengkap.text.toString(),
                nidn = if (user.role == "DOSEN") binding.etNidn.text.toString() else null,
                nim = if (user.role == "MAHASISWA") binding.etNim.text.toString() else null,
                kelas = if (user.role == "MAHASISWA") binding.etKelas.text.toString() else null,
                password = user.password
            )
            viewModel.updateUserProfile(updatedUser)
        }
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val oldPasswordLayout = dialogView.findViewById<TextInputLayout>(R.id.tilOldPassword)
        val newPasswordLayout = dialogView.findViewById<TextInputLayout>(R.id.tilNewPassword)
        val confirmPasswordLayout = dialogView.findViewById<TextInputLayout>(R.id.tilConfirmPassword)

        MaterialAlertDialogBuilder(this)
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Change", null) // Null agar tidak auto-dismiss
            .setNegativeButton("Cancel", null)
            .create()
            .apply {
                setOnShowListener { dialog ->
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val oldPassword = oldPasswordLayout.editText?.text.toString()
                        val newPassword = newPasswordLayout.editText?.text.toString()
                        val confirmPassword = confirmPasswordLayout.editText?.text.toString()

                        if (validatePasswordChange(oldPassword, newPassword, confirmPassword)) {
                            currentUser?.id?.let { userId ->
                                viewModel.changePassword(userId, oldPassword, newPassword)
                                dialog.dismiss()
                            }
                        }
                    }
                }
                show()
            }
    }

    private fun validatePasswordChange(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        if (oldPassword.isEmpty()) {
            Toast.makeText(this, "Password lama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Password baru tidak boleh kosong", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    // Factory untuk ProfileViewModel
    class ProfileViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
