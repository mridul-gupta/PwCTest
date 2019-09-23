package com.example.pwctest

import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProviders
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MapsActivityInstrumentedTests {
    private var mIdlingResource: IdlingResource? = null
    private lateinit var mViewModel: TransportViewModel

    @Rule
    @JvmField
    var rule: ActivityTestRule<MapsActivity> = ActivityTestRule(MapsActivity::class.java)
    private lateinit var mapsActivity: MapsActivity

    @Before
    fun setUp() {
        mapsActivity = rule.activity
        mIdlingResource = mapsActivity.getIdlingResource()

        IdlingRegistry.getInstance().register(mIdlingResource)
        initViewModels()
    }

    private fun initViewModels() {
        val factory = ViewModelFactory.getInstance()
        mViewModel =
            ViewModelProviders.of(mapsActivity, factory).get(TransportViewModel::class.java)
    }

    @After
    fun cleanUp() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource)
        }
    }

    @Test
    fun instrumented_CheckAllViews_IdleState() {
        onView(withId(R.id.map)).check(matches(notNullValue()))
            .check(matches(CoreMatchers.instanceOf<Any>(FrameLayout::class.java)))
        onView(withId(R.id.progressBar)).check(matches(notNullValue()))
            .check(matches(CoreMatchers.instanceOf<Any>(ProgressBar::class.java)))
        onView(withId(R.id.cv_bottom)).check(matches(notNullValue()))
            .check(matches(CoreMatchers.instanceOf<Any>(CardView::class.java)))
        onView(withId(R.id.tv_after)).check(matches(notNullValue()))
            .check(matches(CoreMatchers.instanceOf<Any>(TextView::class.java)))
        onView(withId(R.id.tv_dayAfter)).check(matches(notNullValue()))
            .check(matches(CoreMatchers.instanceOf<Any>(TextView::class.java)))
        onView(withId(R.id.tv_monthYearDayNameAfter)).check(matches(notNullValue()))
            .check(matches(CoreMatchers.instanceOf<Any>(TextView::class.java)))
        onView(withId(R.id.tv_timeAfter)).check(matches(notNullValue()))
            .check(matches(CoreMatchers.instanceOf<Any>(TextView::class.java)))
        onView(withId(R.id.tv_before)).check(matches(notNullValue()))
            .check(matches(CoreMatchers.instanceOf<Any>(TextView::class.java)))
        onView(withId(R.id.tv_dayBefore)).check(matches(notNullValue()))
            .check(matches(CoreMatchers.instanceOf<Any>(TextView::class.java)))
        onView(withId(R.id.tv_monthYearDayNameBefore)).check(matches(notNullValue()))
            .check(matches(CoreMatchers.instanceOf<Any>(TextView::class.java)))
        onView(withId(R.id.tv_timeBefore)).check(matches(notNullValue()))
            .check(matches(CoreMatchers.instanceOf<Any>(TextView::class.java)))
        onView(withId(R.id.tb_train)).check(matches(notNullValue()))
            .check(matches(CoreMatchers.instanceOf<Any>(ToggleButton::class.java)))
        onView(withId(R.id.tb_bus)).check(matches(notNullValue()))
            .check(matches(CoreMatchers.instanceOf<Any>(ToggleButton::class.java)))
        onView(withId(R.id.bt_search)).check(matches(notNullValue()))
            .check(matches(CoreMatchers.instanceOf<Any>(Button::class.java)))
    }

    @Test
    fun instrumented_GetTransportWithTrain_MarkerCount() {
        onView(withId(R.id.tb_bus)).perform(click())
        onView(withId(R.id.bt_search)).perform(click())

        try {
            sleep(1000) /* give it breathing time */
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        assert(mapsActivity.markers.size == mViewModel.transports.modes.size)
    }

    @Test
    fun instrumented_GetTransportWithBus_MarkerCount() {
        onView(withId(R.id.tb_train)).perform(click())
        onView(withId(R.id.bt_search)).perform(click())

        try {
            sleep(1000) /* give it breathing time */
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        assert(mapsActivity.markers.size == mViewModel.transports.modes.size)
    }

    @Test
    fun instrumented_GetTransportWithTrainBus_MarkerCount() {
        onView(withId(R.id.bt_search)).perform(click())

        try {
            sleep(1000) /* give it breathing time */
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        assert(mapsActivity.markers.size == mViewModel.transports.modes.size)
    }

    @Test
    fun instrumented_GetTransportWithNoMode_MarkerCount() {
        mapsActivity.runOnUiThread { mapsActivity.resetMap() } /* remove existing markers */

        onView(withId(R.id.tb_bus)).perform(click())
        onView(withId(R.id.tb_train)).perform(click())
        onView(withId(R.id.bt_search)).perform(click())

        try {
            sleep(1000) /* give it breathing time */
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        assert(mapsActivity.markers.size == 0)
    }
}
