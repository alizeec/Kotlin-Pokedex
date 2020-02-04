package com.example.navigation

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf

sealed class Route

data class ScreenRoute(
    val activityType: Class<out Activity>,
    val fragmentType: Class<out Fragment>?,
    val extras: Bundle?,
    val activityTransition: Transition?,
    val fragmentTransition: Transition?
) : Route()

data class ModalRoute(
    val modalHostType: Class<out AppCompatDialogFragment>,
    val modalContentType: Class<out Fragment>?,
    val extras: Bundle?,
    val contentTransition: Transition?
) : Route()

data class Transition(
    val startAnim: Pair<Int, Int>,
    val backAnim: Pair<Int, Int>
)

inline fun <reified A : AppCompatActivity, reified F : Fragment> fragmentRoute(
    extras: Map<String, Any>? = null,
    activityTransition: Transition? = null,
    fragmentTransition: Transition? = null
) = ScreenRoute(A::class.java, F::class.java, extras?.toBundle(), activityTransition, fragmentTransition)

inline fun <reified A : AppCompatActivity> activityRoute(
    extras: Map<String, Any>? = null,
    activityTransition: Transition? = null,
    fragmentTransition: Transition? = null
) = ScreenRoute(A::class.java, null, extras?.toBundle(), activityTransition, fragmentTransition)

inline fun <reified D : AppCompatDialogFragment> modalRoute(extras: Map<String, Any>? = null) =
    ModalRoute(D::class.java, null, extras?.toBundle(), null)

inline fun <reified H : AppCompatDialogFragment, reified F : Fragment> submodalRoute(
    extras: Map<String, Any>? = null,
    contentTransition: Transition? = null
) = ModalRoute(H::class.java, F::class.java, extras?.toBundle(), contentTransition)

fun Map<String, Any>.toBundle() = bundleOf(*map { Pair(it.key, it.value) }.toTypedArray())