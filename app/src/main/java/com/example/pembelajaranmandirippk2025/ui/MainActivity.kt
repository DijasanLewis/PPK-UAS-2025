package com.example.pembelajaranmandirippk2025.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.pembelajaranmandirippk2025.R
import com.example.pembelajaranmandirippk2025.databinding.ActivityMainBinding
import com.example.pembelajaranmandirippk2025.util.SessionManager
import com.example.pembelajaranmandirippk2025.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Setup navigation based on user role
        setupNavigation()

        // Setup top app bar
        setupTopAppBar()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val graphInflater = navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.nav_graph)

        // Set start destination based on user role
        navGraph.setStartDestination(
            when (sessionManager.getUserRole()) {
                "DOSEN" -> R.id.pertemuanListDosenFragment
                else -> R.id.pertemuanListFragment
            }
        )

        // Set the modified graph
        navController.graph = navGraph

        // Set up bottom navigation
        setupBottomNavigation(navController)
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun performLogout() {
        sessionManager.clearSession()
        // Arahkan ke LoginActivity dan hapus stack activity sebelumnya
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupTopAppBar() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.menu_logout -> {
                    showLogoutConfirmationDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupBottomNavigation(navController: NavController) {
        val bottomNavView = binding.bottomNav
        bottomNavView.setupWithNavController(navController)

        // Inflate the appropriate menu based on user role
        bottomNavView.menu.clear()
        when (sessionManager.getUserRole()) {
            "DOSEN" -> {
                bottomNavView.inflateMenu(R.menu.bottom_nav_menu_dosen)
            }
            else -> {
                bottomNavView.inflateMenu(R.menu.bottom_nav_menu_mahasiswa)
            }
        }
    }
}