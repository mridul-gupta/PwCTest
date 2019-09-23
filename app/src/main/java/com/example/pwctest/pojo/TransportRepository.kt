package com.example.pwctest.pojo


import com.example.pwctest.network.ApiCallback
import com.example.pwctest.network.RemoteDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransportRepository {

    /*
     * method to call get user data api
     */
    fun executeGetUserData(
        after: String?,
        before: String?,
        type: Int?,
        apiCallback: ApiCallback<Transports>
    ) {

        val apiInterface = RemoteDataSource.apiInterface

        val call = apiInterface.getTransports(after, before, type)
        call.enqueue(object : Callback<ApiResponse<ResultObj>> {

            override fun onResponse(
                call: Call<ApiResponse<ResultObj>>,
                response: Response<ApiResponse<ResultObj>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    apiCallback.onSuccess(response.body()!!.result!!.transportation)
                }
            }

            override fun onFailure(call: Call<ApiResponse<ResultObj>>, t: Throwable) {
                apiCallback.onFailure()
            }
        })
    }
}
