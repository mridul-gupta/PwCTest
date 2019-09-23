package com.example.pwctest.network

interface ApiCallback<V> {
    fun onFailure()

    fun onSuccess(data: V)
}