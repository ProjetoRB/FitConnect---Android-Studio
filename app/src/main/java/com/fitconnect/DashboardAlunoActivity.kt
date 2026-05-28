package com.fitconnect

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.fitconnect.databinding.ActivityDashboardAlunoBinding

class DashboardAlunoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardAlunoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardAlunoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.btnSairAluno.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}