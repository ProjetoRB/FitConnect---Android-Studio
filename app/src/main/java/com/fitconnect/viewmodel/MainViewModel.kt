package com.fitconnect.viewmodel

import androidx.lifecycle.ViewModel
import com.fitconnect.model.Usuario

class MainViewModel : ViewModel() {

    var usuario: Usuario? = null

    fun init() {
        usuario = Usuario("Aluno de Android")
    }
}