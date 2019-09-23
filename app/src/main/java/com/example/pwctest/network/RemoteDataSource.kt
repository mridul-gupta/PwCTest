package com.example.pwctest.network

object RemoteDataSource {
    val apiInterface: ApiInterface = ApiClient.client.create(ApiInterface::class.java)
}
