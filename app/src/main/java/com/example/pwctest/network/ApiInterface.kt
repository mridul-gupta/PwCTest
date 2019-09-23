package com.example.pwctest.network

import com.example.pwctest.pojo.ApiResponse
import com.example.pwctest.pojo.ResultObj
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("/dev/transportation")
    fun getTransports(
        @Query("departureTimeMin") departureTimeMin: String?,
        @Query("departureTimeMax") departureTimeMax: String?,
        @Query("typeId") typeId: Int?
    ): Call<ApiResponse<ResultObj>>
}
