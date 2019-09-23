package com.example.pwctest

import com.example.pwctest.pojo.Transport
import com.example.pwctest.pojo.Transports

const val BASE_URL = "https://lreypjgj1c.execute-api.ap-southeast-2.amazonaws.com/dev/"
const val HEADER_ACCESS_TOKEN = "x-api-key"
const val accessToken = "6PSJVe1Jr733JRzxP0uAe3TuqBxRqlbE9f4ua8Wf" /*ToDo Store safely */


enum class Status {
    IDLE,
    LOADING,
    SUCCESS,
    ERROR
}

fun getData(): Transports {
    return Transports(
        listOf(
            Transport(
                0,
                "2024-07-03T09:10:00.000Z",
                625,
                "Flinders",
                -35.8181755,
                144.9661256,
                isExpress = true,
                hasMyKiTopUp = true
            ),
            Transport(
                0,
                "2024-07-03T09:10:00.000Z",
                625,
                "Flinders",
                -36.8181755,
                144.9661256,
                isExpress = true,
                hasMyKiTopUp = true
            ),
            Transport(
                0,
                "2024-07-03T09:10:00.000Z",
                625,
                "Flinders",
                -37.8181755,
                144.9661256,
                isExpress = true,
                hasMyKiTopUp = true
            ),
            Transport(
                0,
                "2024-07-03T09:10:00.000Z",
                625,
                "Flinders",
                -38.8181755,
                144.9661256,
                isExpress = true,
                hasMyKiTopUp = true
            )
        )
    )
}