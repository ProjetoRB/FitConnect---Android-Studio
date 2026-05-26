package com.fitconnect.network

import com.fitconnect.model.LoginRequest
import com.fitconnect.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>
}