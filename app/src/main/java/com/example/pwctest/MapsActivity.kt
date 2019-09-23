package com.example.pwctest

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.test.espresso.IdlingResource
import com.example.pwctest.pojo.Transports
import com.example.pwctest.test.EspressoIdlingResource
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest.permission.ACCESS_FINE_LOCATION as MY_ACCESS_FINE_LOCATION


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var offset: Int = 0
    private lateinit var mMap: GoogleMap
    private lateinit var mViewModel: TransportViewModel
    private val requestCode = 101
    private var alertDialog: AlertDialog? = null

    /* The Idling Resource which will be null in production */
    private var mIdlingResource: EspressoIdlingResource? = null
    var markers = ArrayList<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        /* Obtain the SupportMapFragment and get notified when the map is ready to be used. */
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initViewModels(this)

        mViewModel.responseStatus.observe(
            this,
            Observer { consumeResponse(mViewModel.responseStatus.value) })

        /* get on start */
        mViewModel.getTransports(
            mViewModel.afterDate,
            mViewModel.beforeDate,
            mViewModel.trainMode,
            mViewModel.busMode
        )

        measureBottomOffset()

        /* set listeners */
        setListeners()

        /* Request permissions */
        val permissions = arrayOf(MY_ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions, requestCode)

        /* update UI */
        updateViews(mViewModel.afterDate, mViewModel.beforeDate)
    }

    private fun setListeners() {

        /* After date */
        cl_after.setOnClickListener { captureDateTime(mViewModel.afterDate) }

        /* Before date */
        cl_before.setOnClickListener { captureDateTime(mViewModel.beforeDate) }

        /* Toggle Button */
        tb_train.setOnClickListener {
            tb_train.isChecked = !mViewModel.trainMode
            mViewModel.trainMode = tb_train.isChecked
        }

        tb_bus.setOnClickListener {
            tb_bus.isChecked = !mViewModel.busMode
            mViewModel.busMode = tb_bus.isChecked
        }

        /* Search Button */
        bt_search.setOnClickListener {

            /* validate */
            if (!mViewModel.validateDate(Calendar.getInstance(), mViewModel.afterDate)) {
                Toast.makeText(this, "After date cannot be before now", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!mViewModel.validateDate(mViewModel.afterDate, mViewModel.beforeDate)) {
                Toast.makeText(this, "Before date cannot be After date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!mViewModel.validateMode(mViewModel.trainMode, mViewModel.busMode)) {
                Toast.makeText(this, "Please select Travel mode", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            /* if all good */
            mViewModel.getTransports(
                mViewModel.afterDate,
                mViewModel.beforeDate,
                mViewModel.trainMode,
                mViewModel.busMode
            )
        }
    }

    private fun captureDateTime(selectedDate: Calendar) {
        /* check existing alert */
        alertDialog?.dismiss()

        val mTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            selectedDate.set(Calendar.HOUR_OF_DAY, hour)
            selectedDate.set(Calendar.MINUTE, minute)
            selectedDate.set(Calendar.SECOND, 0)
            selectedDate.set(Calendar.MILLISECOND, 0)
            updateViews(mViewModel.afterDate, mViewModel.beforeDate)
        }

        val mDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            selectedDate.set(year, month, day)
            alertDialog = TimePickerDialog(
                this,
                mTimeSetListener,
                selectedDate.get(Calendar.HOUR_OF_DAY),
                selectedDate.get(Calendar.MINUTE),
                false
            )
            (alertDialog as TimePickerDialog).show()
            updateViews(mViewModel.afterDate, mViewModel.beforeDate)
        }

        alertDialog = DatePickerDialog(
            this,
            mDateSetListener,
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
        (alertDialog as DatePickerDialog).show()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        resetMap()
    }


    private fun updateViews(afterDate: Calendar, beforeDate: Calendar) {
        /* Update Views */

        val locale = Locale.getDefault()

        /* after date */
        tv_dayAfter.text =
            String.format(locale, "%02d", afterDate.get(Calendar.DAY_OF_MONTH))

        tv_monthYearDayNameAfter.text = String.format(
            getString(R.string.month_year_day_format),
            SimpleDateFormat("MMM", locale).format(afterDate.time),
            afterDate.get(Calendar.YEAR),
            SimpleDateFormat("EEE", locale).format(afterDate.time)
        )

        tv_timeAfter.text = String.format(
            getString(R.string.time_format),
            SimpleDateFormat("hh", locale).format(afterDate.time),
            SimpleDateFormat("mm", locale).format(afterDate.time),
            if (afterDate.get(Calendar.HOUR_OF_DAY) >= 12) "PM" else "AM"
        )

        /* before date */
        tv_dayBefore.text =
            String.format(locale, "%02d", beforeDate.get(Calendar.DAY_OF_MONTH))

        tv_monthYearDayNameBefore.text = String.format(
            getString(R.string.month_year_day_format),
            SimpleDateFormat("MMM", locale).format(beforeDate.time),
            beforeDate.get(Calendar.YEAR),
            SimpleDateFormat("EEE", locale).format(beforeDate.time)
        )

        tv_timeBefore.text = String.format(
            getString(R.string.time_format),
            SimpleDateFormat("hh", locale).format(beforeDate.time),
            SimpleDateFormat("mm", locale).format(beforeDate.time),
            if (afterDate.get(Calendar.HOUR_OF_DAY) >= 12) "PM" else "AM"
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            this.requestCode -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mMap.isMyLocationEnabled = true

                    val locationButton =
                        (findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(
                            Integer.parseInt("2")
                        )
                    val layoutParams = locationButton.layoutParams as (RelativeLayout.LayoutParams)
                    /* position on right bottom */
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                    layoutParams.setMargins(0, 0, 30, 30)
                }
                return
            }
            else -> {
                /* Ignore all other requests. */
            }
        }
    }

    fun resetMap() {
        mMap.setPadding(10, 20, 20, offset)
        mMap.clear()
        markers.clear()
    }

    private fun updateMap(transports: Transports) {

        /* Rest Map */
        resetMap()

        if (transports.modes.isEmpty()) {
            Toast.makeText(this, "No transport available for selected time", Toast.LENGTH_SHORT)
                .show()
            return
        }

        /* Update Map */
        val builder = LatLngBounds.Builder()
        for (i in transports.modes.indices) {
            val location = LatLng(
                transports.modes[i].latitude,
                transports.modes[i].longitude
            )

            val hue: Float = if (transports.modes[i].typeId == 1) {
                HUE_RED
            } else HUE_AZURE

            val marker: Marker
            marker = mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(transports.modes[i].name)
                    .icon(defaultMarker(hue))
            )
            markers.add(marker)
            builder.include(location)
        }
        val bounds = builder.build()
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.20).toInt() /* offset from edges of the map in pixels */
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
        mMap.animateCamera(cameraUpdate)
    }

    private fun initViewModels(activity: AppCompatActivity) {
        val factory = ViewModelFactory.getInstance()
        mViewModel = ViewModelProviders.of(activity, factory).get(TransportViewModel::class.java)
    }

    private fun consumeResponse(status: Status?) {
        when (status) {
            Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
                bt_search.isEnabled = false

                /* Espresso testing */
                EspressoIdlingResource.setIdleState(false)
            }
            Status.SUCCESS -> {
                progressBar.visibility = View.GONE
                bt_search.isEnabled = true
                /*Toast.makeText(this, "Refreshed data from network", Toast.LENGTH_SHORT).show()*/
                updateMap(mViewModel.transports)

                /* Espresso testing */
                EspressoIdlingResource.setIdleState(true)
            }
            Status.ERROR -> {
                progressBar.visibility = View.GONE
                bt_search.isEnabled = true
                Toast.makeText(
                    this,
                    "Error fetching from network",
                    Toast.LENGTH_SHORT
                ).show()

                /* Espresso testing */
                EspressoIdlingResource.setIdleState(true)
            }
            else -> {
                progressBar.visibility = View.GONE
                Log.e("consumeResponse", "Unexpected status value")

                /* Espresso testing */
                EspressoIdlingResource.setIdleState(true)
            }
        }
    }

    private fun measureBottomOffset() {
        cv_bottom.post {
            offset = cv_bottom.measuredHeight + 100
        }
    }


    /**
     * Only called from test, creates and returns a new EspressoIdlingResource.
     */
    @VisibleForTesting
    fun getIdlingResource(): IdlingResource {
        if (mIdlingResource == null) {
            mIdlingResource = EspressoIdlingResource()
        }
        return mIdlingResource as EspressoIdlingResource
    }
}
