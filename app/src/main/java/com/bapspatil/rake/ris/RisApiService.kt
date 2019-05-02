package com.bapspatil.rake.ris

import com.bapspatil.rake.model.RisInputModel
import com.bapspatil.rake.model.RisOutputModel
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

interface RisApiService {

    @Headers("Content-Type: application/json")
    @POST("labelsearch")
    fun getRisInfo(@Body risInputModel: RisInputModel): Call<RisOutputModel>

    companion object {
        const val BASE_URL = "https://ris-app.herokuapp.com/"

        fun create(): RisApiService {
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            return retrofit.create(RisApiService::class.java)
        }
    }
}