package com.example.pwctest.network

import com.example.pwctest.BASE_URL
import com.example.pwctest.HEADER_ACCESS_TOKEN
import com.example.pwctest.accessToken
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {

    val client: Retrofit
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(RequestHeaderInterceptor())
                .build()
            val sRetrofit: Retrofit

            sRetrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return sRetrofit
        }


    private class RequestHeaderInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()
            request = request.newBuilder().header(HEADER_ACCESS_TOKEN, accessToken).build()

            return chain.proceed(request)
        }
    }
}
