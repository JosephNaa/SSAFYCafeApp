package com.ssafy.smartstoredb.data.service

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface FirebaseTokenService {

    @POST("token")
    fun uploadToken(@Query("token") token: String): Call<String>
}