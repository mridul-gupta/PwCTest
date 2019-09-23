package com.example.pwctest

import org.junit.Before
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TransportSearchTest {
    private lateinit var viewModel: TransportViewModel

    @Before
    fun setup() {
        viewModel = TransportViewModel()
    }

    @Test
    fun getModeType_WithNoMode_ReturnError() {
        val result = viewModel.getModeType(trainMode = false, busMode = false)
        assert(result == -1)
    }

    @Test
    fun getModeType_WithTrainSelected_ReturnZero() {
        val result = viewModel.getModeType(trainMode = true, busMode = false)
        assert(result == 0)
    }

    @Test
    fun getModeType_WithBusSelected_ReturnOne() {
        val result = viewModel.getModeType(trainMode = false, busMode = true)
        assert(result == 1)
    }

    @Test
    fun getModeType_WithTrainBusSelected_ReturnNull() {
        val result = viewModel.getModeType(trainMode = true, busMode = true)
        assert(result == null)
    }

    @Test
    fun validateDate_WithValidDates_ReturnTrue() {
        /* default view data */
        val date1 = Calendar.getInstance()
        date1.set(2024, 6, 3, 9, 30, 0) /* map to api start dates */
        val date2 = Calendar.getInstance()
        date2.set(2024, 6, 10, 12, 30, 0) /* map to api end dates */
        assert(viewModel.validateDate(date1, date2))
    }

    @Test
    fun validateDate_WithInValidDates_ReturnFalse() {
        /* default view data */
        val date1 = Calendar.getInstance()
        date1.set(2024, 6, 3, 9, 30, 0) /* map to api start dates */
        val date2 = Calendar.getInstance()
        date2.set(2024, 6, 10, 12, 30, 0) /* map to api end dates */
        assert(!viewModel.validateDate(date2, date1))
    }

    @Test
    fun validateMode_WithTrainSelected_ReturnTrue() {
        assert(viewModel.validateMode(trainMode = true, busMode = false))
    }

    @Test
    fun validateMode_WithBusSelected_ReturnTrue() {
        assert(viewModel.validateMode(trainMode = false, busMode = true))
    }

    @Test
    fun validateMode_WithTrainBusSelected_ReturnTrue() {
        assert(viewModel.validateMode(trainMode = true, busMode = true))
    }

    @Test
    fun validateMode_WithTrainBusUnSelected_ReturnFalse() {
        assert(!viewModel.validateMode(trainMode = false, busMode = false))
    }
}
