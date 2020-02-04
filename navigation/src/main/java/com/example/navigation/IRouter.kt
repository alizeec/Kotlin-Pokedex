package com.example.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

interface IRouter {

    val currentActivity: AppCompatActivity?

    val currentFragment: Fragment?

    fun navigateTo(
        destination: String,
        extras: Map<String, Any>? = null,
        flags: LaunchScreenFlags? = null
    )

    fun navigateToForResult(
        destination: String,
        requestCode: Int,
        extras: Map<String, Any>? = null,
        flags: LaunchScreenFlags? = null
    )

    fun navigateBack()

    fun finishWithResult(resultExtras: Bundle?, resultCode: Int)

    /**
     * Call this method in each of your Activity.onCreate() and Activity.onRestart()
     */
    fun bindActivity(activity: AppCompatActivity, fragmentHostId: Int? = null)

    fun openExternalApplication(appType: ExternalAppType, block: ExternalAppContent.() -> Unit)

    fun addOnNavigationChangedListener(listener: OnNavigationChangedListener)

    fun removeOnNavigationChangedListener(listener: OnNavigationChangedListener)

    fun bindViewDelegate(delegate: ViewDelegate)

    fun unbindViewDelegate(delegate: ViewDelegate)

    fun <T : ViewDelegate> findViewDelegateByType(onFound: (delegate: T) -> Unit)

    fun shouldBlockNavigateBack(meetCondition: Boolean, action: () -> Unit)

    fun shouldAllowNavigateBack()
}