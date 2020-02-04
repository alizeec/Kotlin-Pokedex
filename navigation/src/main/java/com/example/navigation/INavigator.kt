package com.example.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

interface INavigator {

    fun startActivity(context: Context, intent: Intent, transition: Pair<Int, Int>?)

    fun startActivityFromOutside(clazz: Class<out Activity>, extras: Bundle?)

    fun startActivityForResult(activity: AppCompatActivity, intent: Intent, requestCode: Int, transition: Pair<Int, Int>?)

    fun startActivityForResult(fragment: Fragment, intent: Intent, requestCode: Int, transition: Pair<Int, Int>?)

    fun finishActivity(activity: Activity, resultExtras: Bundle?, resultCode: Int, transition: Pair<Int, Int>?)

    fun replaceFragment(
        activity: FragmentActivity,
        fragmentHostId: Int,
        fragment: Fragment,
        tag: String,
        addToBackStack: Boolean,
        backStackEntryName: String?,
        transition: Transition?
    )

    fun replaceFragmentInModal(
        modal: AppCompatDialogFragment,
        fragmentHostId: Int,
        fragment: Fragment,
        tag: String,
        addToBackStack: Boolean,
        backStackEntryName: String?,
        transition: Transition?
    )

    fun getFragmentBackStackCount(activity: AppCompatActivity): Int

    fun getFragmentBackStackCountInModal(modal: AppCompatDialogFragment): Int

    fun popBackStackInModal(modal: AppCompatDialogFragment)

    fun popBackStack(activity: AppCompatActivity)

    fun createFragment(activity: FragmentActivity, clazz: Class<out Fragment>, extras: Bundle?): Fragment

    fun createFragmentInModal(modal: AppCompatDialogFragment, clazz: Class<out Fragment>, extras: Bundle?): Fragment

    fun getVisibleFragment(activity: AppCompatActivity, id: Int?): Fragment?

    fun prepareIntent(context: Context?, clazz: Class<out Activity>, extras: Bundle?, flags: LaunchScreenFlags?): Intent

    fun openEmailApplication(context: Context, content: String, title: String)

    fun openSharingApplication(context: Context, text: String?)

    fun openLinkHandlerApplication(context: Context, data: String)

    fun openPhotoGalleryApplication(fragment: Fragment, requestCode: Int)

    fun openPhotoGalleryApplication(activity: AppCompatActivity, requestCode: Int)

    fun openCameraApplication(fragment: Fragment, pictureFileUri: String, requestCode: Int)

    fun openCameraApplication(activity: AppCompatActivity, pictureFileUri: String, requestCode: Int)

    fun encodeForUri(text: String): String

    fun createModal(activity: AppCompatActivity, clazz: Class<out AppCompatDialogFragment>, extras: Bundle?): AppCompatDialogFragment

    fun openModal(activity: AppCompatActivity, modal: AppCompatDialogFragment, extras: Bundle?, tag: String)

    fun findModal(activity: AppCompatActivity, tag: String): AppCompatDialogFragment?
}