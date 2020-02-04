package com.example.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class DefaultNavigator(private val applicationContext: Context) : INavigator {

    override fun getFragmentBackStackCountInModal(modal: AppCompatDialogFragment) = modal.childFragmentManager.backStackEntryCount

    override fun getFragmentBackStackCount(activity: AppCompatActivity) =
        activity.supportFragmentManager.backStackEntryCount

    override fun popBackStack(activity: AppCompatActivity) {
        activity.supportFragmentManager.popBackStackImmediate()
    }

    override fun popBackStackInModal(modal: AppCompatDialogFragment) {
        modal.childFragmentManager.popBackStackImmediate()
    }

    override fun replaceFragment(
        activity: FragmentActivity,
        fragmentHostId: Int,
        fragment: Fragment,
        tag: String,
        addToBackStack: Boolean,
        backStackEntryName: String?,
        transition: Transition?
    ) {
        activity.supportFragmentManager.beginTransaction().apply {
            transition?.let {
                setCustomAnimations(it.startAnim.first, it.startAnim.second, it.backAnim.first, it.backAnim.second)
            }

            replace(fragmentHostId, fragment, tag)

            if (addToBackStack) {
                addToBackStack(backStackEntryName)
            }
        }.commit()
    }

    override fun replaceFragmentInModal(
        modal: AppCompatDialogFragment,
        fragmentHostId: Int,
        fragment: Fragment,
        tag: String,
        addToBackStack: Boolean,
        backStackEntryName: String?,
        transition: Transition?
    ) {
        modal.childFragmentManager.beginTransaction().apply {
            transition?.let {
                setCustomAnimations(it.startAnim.first, it.startAnim.second, it.backAnim.first, it.backAnim.second)
            }

            replace(fragmentHostId, fragment, tag)

            if (addToBackStack) {
                addToBackStack(backStackEntryName)
            }
        }.commit()
    }

    override fun getVisibleFragment(activity: AppCompatActivity, id: Int?) =
        activity.supportFragmentManager.findFragmentById(id ?: 0)

    override fun startActivity(context: Context, intent: Intent, transition: Pair<Int, Int>?) {
        context.startActivity(intent)
        if (context is AppCompatActivity && transition != null) {
            context.overridePendingTransition(transition.first, transition.second)
        }
    }

    override fun startActivityFromOutside(clazz: Class<out Activity>, extras: Bundle?) {
        val intent = Intent(applicationContext, clazz).apply {
            extras?.let {
                putExtras(extras)
            }
            this.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        }
        applicationContext.startActivity(intent)
    }

    override fun startActivityForResult(activity: AppCompatActivity, intent: Intent, requestCode: Int, transition: Pair<Int, Int>?) {
        activity.startActivityForResult(intent, requestCode)
        if (transition != null) {
            activity.overridePendingTransition(transition.first, transition.second)
        }
    }

    override fun startActivityForResult(fragment: Fragment, intent: Intent, requestCode: Int, transition: Pair<Int, Int>?) {
        fragment.startActivityForResult(intent, requestCode)
        val activity = fragment.activity
        if (activity is AppCompatActivity && transition != null) {
            activity.overridePendingTransition(transition.first, transition.second)
        }
    }

    override fun finishActivity(activity: Activity, resultExtras: Bundle?, resultCode: Int, transition: Pair<Int, Int>?) {
        resultExtras?.let {
            activity.setResult(resultCode, Intent().putExtras(it))
        } ?: activity.setResult(resultCode)
        activity.finish()
        if (transition != null) {
            activity.overridePendingTransition(transition.first, transition.second)
        }
    }

    override fun createFragment(activity: FragmentActivity, clazz: Class<out Fragment>, extras: Bundle?) =
        activity.supportFragmentManager.fragmentFactory.instantiate(activity.classLoader, clazz.name).apply { arguments = extras }

    override fun createFragmentInModal(modal: AppCompatDialogFragment, clazz: Class<out Fragment>, extras: Bundle?) =
        createFragment(modal.requireActivity(), clazz, extras)

    override fun createModal(activity: AppCompatActivity, clazz: Class<out AppCompatDialogFragment>, extras: Bundle?) =
        activity.supportFragmentManager.fragmentFactory.instantiate(activity.classLoader, clazz.name).apply {
            arguments = extras
        } as AppCompatDialogFragment

    override fun prepareIntent(context: Context?, clazz: Class<out Activity>, extras: Bundle?, flags: LaunchScreenFlags?) =
        Intent(context, clazz).apply {
            extras?.let {
                putExtras(extras)
            }
            flags?.let {
                when (it) {
                    LaunchScreenFlags.NO_DUPLICATE_ON_TOP -> this.flags = FLAG_ACTIVITY_SINGLE_TOP
                    LaunchScreenFlags.BACK_ON_LAST_INSTANCE_AND_CLEAR_BACKSTACK -> this.flags = FLAG_ACTIVITY_CLEAR_TOP
                    LaunchScreenFlags.CLEAR_WHOLE_BACKSTACK -> this.flags =
                        FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK
                }
            }
        }

    override fun openEmailApplication(context: Context, content: String, title: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse(content)

        val intentChooser = Intent.createChooser(emailIntent, title)
        intentChooser.addFlags(FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intentChooser)
    }

    override fun openSharingApplication(context: Context, text: String?) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        sendIntent.addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        context.startActivity(sendIntent)
    }

    override fun openPhotoGalleryApplication(fragment: Fragment, requestCode: Int) {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(fragment, pickPhoto, requestCode, null)
    }

    override fun openPhotoGalleryApplication(activity: AppCompatActivity, requestCode: Int) {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(activity, pickPhoto, requestCode, null)
    }

    override fun openCameraApplication(fragment: Fragment, pictureFileUri: String, requestCode: Int) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(pictureFileUri))
        startActivityForResult(fragment, intent, requestCode, null)
    }

    override fun openCameraApplication(activity: AppCompatActivity, pictureFileUri: String, requestCode: Int) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(pictureFileUri))
        startActivityForResult(activity, intent, requestCode, null)
    }

    override fun openLinkHandlerApplication(context: Context, data: String) {
        val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(data))
        context.startActivity(intent)
    }

    override fun encodeForUri(text: String) = Uri.encode(text)

    override fun openModal(activity: AppCompatActivity, modal: AppCompatDialogFragment, extras: Bundle?, tag: String) {
        modal.apply {
            arguments = extras
        }.show(activity.supportFragmentManager, tag)
    }

    override fun findModal(activity: AppCompatActivity, tag: String) =
        activity.supportFragmentManager.findFragmentByTag(tag) as? AppCompatDialogFragment
}