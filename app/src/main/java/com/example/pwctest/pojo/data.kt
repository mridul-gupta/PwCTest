package com.example.pwctest.pojo


data class Transport(
    var typeId: Int,
    var departureTime: String,
    var route: Int,
    var name: String,
    var latitude: Double,
    var longitude: Double,
    var isExpress: Boolean,
    var hasMyKiTopUp: Boolean
)

data class Transports(
    var modes: List<Transport>
)

data class ResultObj (
    var transportation: Transports
)

data class ApiResponse<T> (
    var statusCode : Int,
    var message: String,
    var result: T? = null
)
