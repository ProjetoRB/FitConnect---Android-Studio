package com.fitconnect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.fitconnect.databinding.ActivityMainBinding
import com.fitconnect.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar layout com Data Binding
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )

        // Instanciar ViewModel
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Inicializar dados
        viewModel.init()

        // Conectar ViewModel ao XML
        binding.vm = viewModel

        // Lifecycle
        binding.lifecycleOwner = this
    }
}