package com.example.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import timber.log.Timber
import java.util.ArrayDeque
import java.util.Deque

@SuppressLint("StaticFieldLeak")
class DefaultRouter(
    private val routeParser: IRouteParser,
    private val navigator: INavigator
) : IRouter, LifecycleObserver {

    @VisibleForTesting
    internal var currentActivityWrapper: ActivityWrapper? = null
    internal var destinationFragment: FragmentWrapper? = null
    @VisibleForTesting
    internal var nextBackTransition: Pair<Int, Int>? = null
    var navigateBackListener: () -> Boolean = { false }

    @VisibleForTesting
    internal val onNavigationChangedListeners = mutableListOf<OnNavigationChangedListener>()
    @VisibleForTesting
    internal val destinationStack: Deque<Pair<String, Route>> = ArrayDeque()

    @VisibleForTesting
    internal val viewDelegateList = mutableListOf<ViewDelegate>()

    override val currentActivity: AppCompatActivity?
        get() = currentActivityWrapper?.activity

    override val currentFragment: Fragment?
        get() = currentActivityWrapper?.let { currentActivityWrapper ->
            return@let navigator.getVisibleFragment(
                currentActivityWrapper.activity,
                currentActivityWrapper.fragmentHostId
            )
        }

    internal val currentModal: AppCompatDialogFragment?
        get() = currentActivityWrapper?.let { navigator.findModal(it.activity, MODAL_TAG) }

    override fun navigateTo(destination: String, extras: Map<String, Any>?, flags: LaunchScreenFlags?) {
        Timber.i("[router] Navigate to $destination")

        when (val route = routeParser.parse(destination, extras) ?: throw UnknownDestinationException(destination)) {
            is ScreenRoute -> navigateToScreen(destination, route, flags, route.extras)
            is ModalRoute -> navigateToModal(destination, route)
        }
    }

    @VisibleForTesting
    fun navigateToScreen(destination: String, route: ScreenRoute, flags: LaunchScreenFlags?, extras: Bundle?) {
        nextBackTransition = route.activityTransition?.backAnim

        currentActivityWrapper?.let { activityWrapper ->
            activityWrapper.activity.runOnUiThread {
                if (activityWrapper.activity.javaClass != route.activityType) {
                    val intent = navigator.prepareIntent(activityWrapper.activity, route.activityType, route.extras, flags)
                    navigator.startActivity(activityWrapper.activity, intent, route.activityTransition?.startAnim)
                    destinationFragment = route.fragmentType?.let { FragmentWrapper(it, extras, route.fragmentTransition) }

                    addRouteToBackStack(destination, route)
                } else {
                    route.fragmentType?.let { fragmentType ->
                        navigateToFragment(fragmentType, route.extras, route.fragmentTransition)
                        addRouteToBackStack(destination, route)
                    }
                }
            }
        } ?: run {
            navigator.startActivityFromOutside(route.activityType, route.extras)
            destinationFragment = route.fragmentType?.let { FragmentWrapper(it, extras, route.fragmentTransition) }

            addRouteToBackStack(destination, route)
        }
    }

    @VisibleForTesting
    fun navigateToModal(destination: String, route: ModalRoute) {
        currentActivityWrapper?.let { activityWrapper ->
            val currentModal = currentModal
            if (route.modalContentType != null && currentModal != null && currentModal.javaClass == route.modalHostType) {
                navigateToSubModal(destination, currentModal, route.modalContentType, route.extras, route.contentTransition)
            } else {
                currentModal?.dismiss()
                val modal = navigator.createModal(activityWrapper.activity, route.modalHostType, route.extras)

                navigator.openModal(activityWrapper.activity, modal, route.extras, MODAL_TAG)
                if (route.modalContentType != null) {
                    modal.lifecycle.addObserver(object : LifecycleObserver {

                        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
                        fun onActivityCreated() {
                            navigateToSubModal(destination, modal, route.modalContentType, route.extras, null)
                        }
                    })
                }

                Timber.i("[router] Modal ${modal.javaClass.simpleName} open")
            }
        }
    }

    @VisibleForTesting
    fun navigateToSubModal(
        destination: String,
        host: AppCompatDialogFragment,
        modalContentType: Class<out Fragment>,
        extras: Bundle?,
        transition: Transition?
    ) {
        val fragmentHostId = (host as? FragmentHost)?.fragmentHostId ?: noFragmentHost(destination, host.javaClass)
        val fragment = navigator.createFragmentInModal(host, modalContentType, extras)

        navigator.replaceFragmentInModal(host, fragmentHostId, fragment, FRAGMENT_TAG, true, fragment.javaClass.name, transition)
    }

    override fun navigateToForResult(
        destination: String,
        requestCode: Int,
        extras: Map<String, Any>?,
        flags: LaunchScreenFlags?
    ) {
        Timber.i("[router] Navigate to $destination with result for request code $requestCode")
        val route = routeParser.parse(destination, extras) ?: throw UnknownDestinationException(destination)

        (route as? ScreenRoute)?.let {
            navigateToScreenForResult(destination, it, flags, requestCode)
        }
    }

    @VisibleForTesting
    fun navigateToScreenForResult(destination: String, route: ScreenRoute, flags: LaunchScreenFlags?, requestCode: Int) {
        nextBackTransition = route.activityTransition?.backAnim

        currentActivityWrapper?.let { activityWrapper ->
            activityWrapper.activity.runOnUiThread {
                if (activityWrapper.activity.javaClass != route.activityType) {
                    val currentFragment = navigator.getVisibleFragment(activityWrapper.activity, activityWrapper.fragmentHostId)
                    currentFragment?.let {
                        startActivityForResultFromFragment(it, route, flags, requestCode)
                    } ?: startActivityForResultFromActivity(activityWrapper, route, flags, requestCode)

                    destinationFragment = route.fragmentType?.let { FragmentWrapper(it, route.extras, route.fragmentTransition) }
                    addRouteToBackStack(destination, route)
                }
            }
        }
    }

    private fun startActivityForResultFromActivity(
        activityWrapper: ActivityWrapper,
        route: ScreenRoute,
        flags: LaunchScreenFlags?,
        requestCode: Int
    ) {
        val intent = navigator.prepareIntent(activityWrapper.activity, route.activityType, route.extras, flags)
        navigator.startActivityForResult(activityWrapper.activity, intent, requestCode, route.activityTransition?.startAnim)
    }

    private fun startActivityForResultFromFragment(
        currentFragment: Fragment,
        route: ScreenRoute,
        flags: LaunchScreenFlags?,
        requestCode: Int
    ) {
        val intent = navigator.prepareIntent(currentFragment.context, route.activityType, route.extras, flags)
        navigator.startActivityForResult(currentFragment, intent, requestCode, route.activityTransition?.startAnim)
    }

    private fun navigateToFragment(fragmentType: Class<out Fragment>, extras: Bundle?, transition: Transition?) {
        currentActivityWrapper?.let { activityWrapper ->
            activityWrapper.fragmentHostId?.let { fragmentHostId ->
                val fragment = navigator.createFragment(activityWrapper.activity, fragmentType, extras)

                navigator.replaceFragment(
                    activityWrapper.activity, fragmentHostId, fragment, FRAGMENT_TAG,
                    true, fragment.javaClass.name, transition
                )
            }
        }
    }

    override fun navigateBack() {
        val shouldBlock = navigateBackListener.invoke()

        if (shouldBlock.not()) {
            Timber.i("[router] navigateBack()")

            val currentModal = currentModal
            if (currentModal != null) {
                currentActivityWrapper?.let { activityWrapper ->
                    activityWrapper.activity.runOnUiThread {
                        if (navigator.getFragmentBackStackCountInModal(currentModal) > 1) {
                            navigator.popBackStackInModal(currentModal)
                        } else {
                            currentModal.dismiss()
                        }
                    }
                }
            } else {
                currentActivityWrapper?.let { activityWrapper ->
                    activityWrapper.activity.runOnUiThread {
                        if (navigator.getFragmentBackStackCount(activityWrapper.activity) > 1) {
                            navigator.popBackStack(activityWrapper.activity)
                            popRouteBackStack()
                        } else {
                            navigator.finishActivity(
                                activityWrapper.activity,
                                null,
                                Activity.RESULT_CANCELED,
                                nextBackTransition
                            )
                            nextBackTransition = null
                            popRouteBackStack()
                        }
                    }
                }
            }
        }
    }

    override fun finishWithResult(resultExtras: Bundle?, resultCode: Int) {
        currentActivityWrapper?.let { activityWrapper ->
            activityWrapper.activity.runOnUiThread {
                navigator.finishActivity(activityWrapper.activity, resultExtras, resultCode, nextBackTransition)
                popRouteBackStack()
                nextBackTransition = null
            }
        }
    }

    override fun bindActivity(activity: AppCompatActivity, fragmentHostId: Int?) {
        val activityWrapper = ActivityWrapper(activity, fragmentHostId)
        currentActivityWrapper = activityWrapper
        Timber.i("[router] Activity ${activity.javaClass.simpleName} binded")

        activity.lifecycle.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onActivityCreated() {
                destinationFragment?.let { fragmentWrapper ->
                    navigateToFragment(fragmentWrapper.fragmentType, fragmentWrapper.extras, fragmentWrapper.transition)
                    destinationFragment = null
                }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onActivityDestroyed() {
                activity.lifecycle.removeObserver(this)
                Timber.i("[router] Activity ${activity.javaClass.simpleName} unbinded")
                if (currentActivityWrapper == activityWrapper) {
                    currentActivityWrapper = null
                    Timber.i("[router] No more current activity")
                }
            }
        })
    }

    override fun addOnNavigationChangedListener(listener: OnNavigationChangedListener) {
        onNavigationChangedListeners.add(listener)
    }

    override fun removeOnNavigationChangedListener(listener: OnNavigationChangedListener) {
        onNavigationChangedListeners.remove(listener)
    }

    override fun openExternalApplication(appType: ExternalAppType, block: ExternalAppContent.() -> Unit) {
        val externalAppContent = ExternalAppContent(appType).apply(block)
        when (externalAppContent.appType) {
            ExternalAppType.EMAIL -> openEmailApp(
                externalAppContent.toEmails,
                externalAppContent.emailSubject,
                externalAppContent.text,
                externalAppContent.emailTitle
            )
            ExternalAppType.SHARE -> openSharingTextPanel(externalAppContent.text)
            ExternalAppType.PHOTO_GALLERY -> openPhotoGalleryApp(requireNotNull(externalAppContent.requestCode))
            ExternalAppType.CAMERA -> openCameraApp(
                requireNotNull(externalAppContent.requestCode),
                requireNotNull(externalAppContent.tempPictureFileUri)
            )
            ExternalAppType.LINK_HANDLER_APP -> externalAppContent.text?.let { openLinkHandlerApplication(it) }
        }
    }

    private fun addRouteToBackStack(newDestination: String, route: Route) {
        destinationStack.addLast(newDestination to route)
        dispatchNavigationChanged(newDestination)
    }

    private fun popRouteBackStack() {
        destinationStack.pollLast()
        destinationStack.peekLast()?.let { newDestination ->
            dispatchNavigationChanged(newDestination.first)
        }
    }

    private fun dispatchNavigationChanged(newDestination: String) {
        Timber.i("[router] dispatchNavigationChanged($newDestination)")
        onNavigationChangedListeners.forEach {
            it.invoke(newDestination)
        }
    }

    private fun openEmailApp(toEmails: Array<out String>?, subject: String?, text: String?, title: String?) {
        if (toEmails != null && title != null) {
            val mailtoSB = StringBuilder().append("mailto:${toEmails[0]}")
            subject?.let {
                mailtoSB.append("&subject=" + navigator.encodeForUri(subject))
            }
            text?.let {
                mailtoSB.append("&body=" + navigator.encodeForUri(text))
            }
            currentActivityWrapper?.let { activityWrapper ->
                activityWrapper.activity.runOnUiThread {
                    navigator.openEmailApplication(activityWrapper.activity, mailtoSB.toString(), title)
                }
            }
        }
    }

    private fun openSharingTextPanel(text: String?) {
        currentActivityWrapper?.let { activityWrapper ->
            activityWrapper.activity.runOnUiThread {
                navigator.openSharingApplication(activityWrapper.activity, text)
            }
        }
    }

    private fun openLinkHandlerApplication(data: String) {
        currentActivityWrapper?.let { activityWrapper ->
            activityWrapper.activity.runOnUiThread {
                navigator.openLinkHandlerApplication(activityWrapper.activity, data)
            }
        }
    }

    @VisibleForTesting
    fun openPhotoGalleryApp(requestCode: Int) {
        currentActivityWrapper?.activity?.runOnUiThread {
            currentModal?.let { modal ->
                navigator.openPhotoGalleryApplication(modal, requestCode)

            } ?: currentFragment?.let { fragment ->
                navigator.openPhotoGalleryApplication(fragment, requestCode)

            } ?: currentActivity?.let {
                navigator.openPhotoGalleryApplication(it, requestCode)
            }
        }
    }

    @VisibleForTesting
    fun openCameraApp(requestCode: Int, pictureFileUri: String) {
        currentActivityWrapper?.activity?.runOnUiThread {
            currentModal?.let { modal ->
                navigator.openCameraApplication(modal, pictureFileUri, requestCode)

            } ?: currentFragment?.let { fragment ->
                navigator.openCameraApplication(fragment, pictureFileUri, requestCode)

            } ?: currentActivity?.let {
                navigator.openCameraApplication(it, pictureFileUri, requestCode)
            }
        }
    }

    override fun bindViewDelegate(delegate: ViewDelegate) {
        viewDelegateList.add(delegate)
        Timber.i("[router] View Delegate ${delegate.javaClass.simpleName} binded")
    }

    override fun unbindViewDelegate(delegate: ViewDelegate) {
        viewDelegateList.remove(delegate)
        Timber.i("[router] View Delegate ${delegate.javaClass.simpleName} unbinded")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewDelegate> findViewDelegateByType(onFound: (delegate: T) -> Unit) {
        viewDelegateList.toList().forEach { delegate ->
            delegate.callIfAssignable<T> {
                onFound.invoke(it)
            }
        }
    }

    override fun shouldBlockNavigateBack(meetCondition: Boolean, action: () -> Unit) {
        navigateBackListener = {
            if (meetCondition) {
                currentActivityWrapper?.let { activityWrapper ->
                    activityWrapper.activity.runOnUiThread { action.invoke() }
                }
            }
            meetCondition
        }
    }

    override fun shouldAllowNavigateBack() {
        navigateBackListener = { false }
    }

    internal data class ActivityWrapper(
        val activity: AppCompatActivity,
        val fragmentHostId: Int?
    )

    internal data class FragmentWrapper(
        val fragmentType: Class<out Fragment>,
        val extras: Bundle?,
        val transition: Transition?
    )

    companion object {
        const val FRAGMENT_TAG = "current_fragment"
        const val MODAL_TAG = "current_modal"
    }
}

@Suppress("UNCHECKED_CAST", "BLACK_MAGIC")
private fun <T> Any.callIfAssignable(block: (T) -> Unit) {
    try {
        (this as T).let(block)
    } catch (e: ClassCastException) {
    }
}