package com.example.pwctest.test


import androidx.test.espresso.IdlingResource

import java.util.concurrent.atomic.AtomicBoolean

class EspressoIdlingResource : IdlingResource {

    override fun getName(): String {
        return this.javaClass.name
    }

    override fun isIdleNow(): Boolean {
        return mIsIdleNow.get()
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        mCallback = callback
    }

    companion object {

        @Volatile
        private var mCallback: IdlingResource.ResourceCallback? = null

        // Idleness is controlled with this boolean.
        private val mIsIdleNow = AtomicBoolean(true)

        /**
         * Sets the new idle state, if isIdleNow is true, it pings the [Resource Callback].
         *
         * @param isIdleNow false if there are pending operations, true if idle.
         */
        fun setIdleState(isIdleNow: Boolean) {
            mIsIdleNow.set(isIdleNow)
            if (isIdleNow && mCallback != null) {
                mCallback!!.onTransitionToIdle()
            }
        }
    }
}