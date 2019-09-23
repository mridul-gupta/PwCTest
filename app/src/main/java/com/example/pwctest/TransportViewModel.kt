package com.example.pwctest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pwctest.network.ApiCallback
import com.example.pwctest.pojo.TransportRepository
import com.example.pwctest.pojo.Transports
import java.text.SimpleDateFormat
import java.util.*

class TransportViewModel : ViewModel() {
    /* view state */
    var afterDate: Calendar
    var beforeDate: Calendar
    var trainMode: Boolean
    var busMode: Boolean


    var transports: Transports

    internal val responseStatus = MutableLiveData(Status.IDLE)

    private var mRepository: TransportRepository? = null

    init {
        this.mRepository = TransportRepository()

        transports = getData() /* default data */

        /* default view data */
        afterDate = Calendar.getInstance()
        afterDate.set(2024, 6, 3, 9, 30, 0) /* map to api start dates */

        beforeDate = Calendar.getInstance()
        beforeDate.set(2024, 6, 10, 12, 30, 0) /* map to api end dates */

        trainMode = true
        busMode = true
    }

    fun getTransports(
        afterDate: Calendar,
        beforeDate: Calendar,
        trainMode: Boolean,
        busMode: Boolean
    ) {
        val locale = Locale.getDefault()
        val after: String? =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", locale).format(afterDate.time)
        val before: String? =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", locale).format(beforeDate.time)
        val type: Int? = getModeType(trainMode, busMode)

        responseStatus.value = Status.LOADING

        mRepository?.executeGetUserData(after, before, type, object : ApiCallback<Transports> {

            override fun onFailure() {
                responseStatus.value = Status.ERROR
            }

            override fun onSuccess(data: Transports) {
                transports = data
                responseStatus.value = Status.SUCCESS
            }
        })
    }


    fun getModeType(trainMode: Boolean, busMode: Boolean): Int? {
        if (trainMode && busMode) {
            return null
        } else if (trainMode) {
            return 0
        } else if (busMode) {
            return 1
        }
        return -1
    }


    fun validateDate(
        afterDate: Calendar,
        beforeDate: Calendar
    ): Boolean {
        return beforeDate.after(afterDate)
    }

    fun validateMode(
        trainMode: Boolean,
        busMode: Boolean
    ): Boolean {
        var isValidMode = true
        if (!trainMode && !busMode) {
            isValidMode = false
        }
        return isValidMode
    }
}
