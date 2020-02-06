package com.example.navigation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import com.example.navigation.DefaultRouter.Companion.FRAGMENT_TAG
import com.example.navigation.DefaultRouter.Companion.MODAL_TAG
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString

class DefaultRouterTest {

    private lateinit var router: DefaultRouter
    private val routeParser: IRouteParser = mock()
    private val navigator: INavigator = mock()

    private lateinit var lifecycleFirst: LifecycleRegistry
    private lateinit var mockActivityFirst: FirstActivity

    private lateinit var lifecycleSecond: LifecycleRegistry
    private lateinit var mockActivitySecond: SecondActivity

    private lateinit var routeA2F2: ScreenRoute

    @Before
    fun setUp() {
        router = DefaultRouter(routeParser, navigator)

        lifecycleFirst = LifecycleRegistry(mock())
        mockActivityFirst = mock()
        whenever(mockActivityFirst.lifecycle).thenReturn(lifecycleFirst)
        whenever(mockActivityFirst.finish()).then {
            lifecycleFirst.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            lifecycleFirst.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        }

        lifecycleSecond = LifecycleRegistry(mock())
        mockActivitySecond = mock()

        whenever(mockActivitySecond.lifecycle).thenReturn(lifecycleSecond)
        whenever(mockActivitySecond.finish()).then {
            lifecycleSecond.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            lifecycleSecond.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        }

        routeA2F2 = ScreenRoute(
            mockActivitySecond.javaClass,
            FirstFragment::class.java,
            Bundle(),
            null,
            null
        )


        whenever(mockActivityFirst.runOnUiThread(any())).then {
            it.getArgument<Runnable>(0).run()
        }
        whenever(mockActivitySecond.runOnUiThread(any())).then {
            it.getArgument<Runnable>(0).run()
        }
    }

    @Test
    fun navigateTo_BetweenActivities() {
        //Given
        val listener: OnNavigationChangedListener = mock()

        val extras = mapOf(KEY to VALUE)
        whenever(routeParser.parse(eq(destination), anyOrNull())).thenReturn(routeA2F2)
        val flags = FLAGS

        val intent: Intent = mock()

        whenever(
            navigator.prepareIntent(
                eq(mockActivityFirst),
                eq(routeA2F2.activityType),
                anyOrNull(),
                eq(FLAGS)
            )
        ).thenReturn(intent)

        //When
        router.onNavigationChangedListeners.add(listener)
        router.bindActivity(mockActivityFirst)
        router.navigateTo(destination, extras, flags)

        //Then
        verify(navigator).startActivity(mockActivityFirst, intent, null)
        assertNull(router.nextBackTransition)
        assertEquals(destination, router.destinationStack.last.first)
        assertEquals(routeA2F2, router.destinationStack.last.second)
        verify(listener).invoke(destination)
    }

    @Test
    fun navigateTo_SameActivity() {
        //Given
        val listener: OnNavigationChangedListener = mock()

        val extras = mapOf(KEY to VALUE)
        whenever(routeParser.parse(eq(destination), anyOrNull())).thenReturn(routeA2F2)
        val flags = FLAGS

        //When
        router.onNavigationChangedListeners.add(listener)
        router.bindActivity(mockActivitySecond)
        router.navigateTo(destination, extras, flags)

        //Then
        verify(navigator, never()).startActivity(any(), any(), eq(null))
        assertNull(router.nextBackTransition)
        assertEquals(destination, router.destinationStack.last.first)
        assertEquals(routeA2F2, router.destinationStack.last.second)
        verify(listener).invoke(destination)
    }

    @Test
    fun navigateTo_NoExtras() {
        //Given
        val listener: OnNavigationChangedListener = mock()

        whenever(routeParser.parse(any(), anyOrNull())).thenReturn(routeA2F2)
        val intent: Intent = mock()

        whenever(
            navigator.prepareIntent(
                eq(mockActivityFirst),
                eq(routeA2F2.activityType),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(intent)

        //When
        router.onNavigationChangedListeners.add(listener)
        router.bindActivity(mockActivityFirst)
        router.navigateTo(destination)

        //Then
        verify(navigator).startActivity(mockActivityFirst, intent, null)
        assertNull(router.nextBackTransition)
        assertEquals(destination, router.destinationStack.last.first)
        assertEquals(routeA2F2, router.destinationStack.last.second)
        verify(listener).invoke(destination)
    }

    @Test
    fun navigateTo_NoCurrentActivity() {
        whenever(routeParser.parse(eq(destination), anyOrNull())).thenReturn(routeA2F2)
        router.navigateTo(destination)
        verify(navigator, never()).startActivity(any(), any(), eq(null))
    }

    @Test
    fun navigateTo_BetweenFragments() {
        val listener: OnNavigationChangedListener = mock()
        val fragmentSecond: SecondFragment = mock()
        val fragmentHostId = 100

        whenever(routeParser.parse(eq(destination), anyOrNull())).thenReturn(routeA2F2)
        whenever(navigator.createFragment(any(), any(), anyOrNull())).thenReturn(fragmentSecond)

        router.addOnNavigationChangedListener(listener)
        router.bindActivity(mockActivitySecond, fragmentHostId)
        router.navigateTo(destination)

        verify(navigator, never()).startActivity(any(), any(), eq(null))
        verify(navigator).replaceFragment(
            eq(mockActivitySecond),
            eq(fragmentHostId),
            eq(fragmentSecond),
            eq("current_fragment"),
            eq(true),
            anyString(),
            anyOrNull()
        )
        assertEquals(destination, router.destinationStack.last.first)
        assertEquals(routeA2F2, router.destinationStack.last.second)
        verify(listener).invoke(destination)
    }

    @Test
    fun navigateTo_BetweenFragments_InvalidFragment() {
        val fragmentSecond: SecondFragment = mock()
        val fragmentHostId = 100

        whenever(routeParser.parse(eq(destination), anyOrNull())).thenReturn(
            ScreenRoute(
                mockActivitySecond.javaClass,
                null,
                Bundle(),
                null,
                null
            )
        )
        whenever(navigator.createFragment(any(), any(), anyOrNull())).thenReturn(fragmentSecond)

        router.bindActivity(mockActivitySecond, fragmentHostId)
        router.navigateTo(destination)

        verify(navigator, never()).startActivity(any(), any(), eq(null))
        verify(navigator, never()).replaceFragment(
            any<FragmentActivity>(),
            any(),
            any(),
            any(),
            any(),
            anyString(),
            anyOrNull()
        )
    }

    @Test(expected = UnknownDestinationException::class)
    fun navigateTo_NoRoute() {
        val extras = mapOf(KEY to VALUE)
        val flags = FLAGS

        router.navigateTo(destination, extras, flags)
    }

    @Test
    fun navigateTo_withTransition() {
        //Given
        val listener: OnNavigationChangedListener = mock()

        val extras = mapOf(KEY to VALUE)
        val route = routeA2F2.copy(
            activityTransition = Transition(
                Pair(1, 1),
                Pair(1, 1)
            )
        )
        whenever(
            routeParser.parse(
                eq(destination),
                anyOrNull()
            )
        ).thenReturn(
            route
        )
        val flags = FLAGS
        val intent: Intent = mock()

        whenever(
            navigator.prepareIntent(
                eq(mockActivityFirst),
                eq(routeA2F2.activityType),
                anyOrNull(),
                eq(FLAGS)
            )
        ).thenReturn(intent)

        //When
        router.onNavigationChangedListeners.add(listener)
        router.bindActivity(mockActivityFirst)
        router.navigateTo(destination, extras, flags)

        //Then
        verify(navigator).startActivity(
            mockActivityFirst, intent, Pair(1, 1)
        )

        assertEquals(Pair(1, 1), router.nextBackTransition)
        assertEquals(destination, router.destinationStack.last.first)
        assertEquals(route, router.destinationStack.last.second)
        verify(listener).invoke(destination)
    }

    @Test
    fun navigateTo_fromOutside() {
        val extras = mapOf(KEY to VALUE)
        whenever(routeParser.parse(destination, extras)).thenReturn(routeA2F2)

        router.navigateTo(destination, extras, null)

        verify(navigator).startActivityFromOutside(routeA2F2.activityType, routeA2F2.extras)
        assertNull(router.nextBackTransition)
    }

    @Test
    fun navigateToForResult_fromActivity() {
        //Given
        val listener: OnNavigationChangedListener = mock()

        val extras = mapOf(KEY to VALUE)
        whenever(routeParser.parse(eq(destination), anyOrNull())).thenReturn(routeA2F2)
        val flags = FLAGS

        val intent: Intent = mock()

        whenever(
            navigator.prepareIntent(
                eq(mockActivityFirst),
                eq(routeA2F2.activityType),
                anyOrNull(),
                eq(FLAGS)
            )
        ).thenReturn(intent)
        doReturn(null).`when`(navigator).getVisibleFragment(mockActivityFirst, FRAGMENT_HOST_ID)

        //When
        router.onNavigationChangedListeners.add(listener)
        router.bindActivity(mockActivityFirst, FRAGMENT_HOST_ID)
        router.navigateToForResult(destination, 2, extras, flags)

        //Then
        verify(navigator).startActivityForResult(mockActivityFirst, intent, 2, null)
        assertEquals(FirstFragment::class.java, router.destinationFragment?.fragmentType)
        assertEquals(destination, router.destinationStack.last.first)
        assertEquals(routeA2F2, router.destinationStack.last.second)
        verify(listener).invoke(destination)
    }

    @Test
    fun navigateToForResult_fromSameActivity() {
        val extras = mapOf(KEY to VALUE)
        whenever(routeParser.parse(eq(destination), anyOrNull())).thenReturn(routeA2F2)
        val flags = FLAGS

        val intent: Intent = mock()

        whenever(
            navigator.prepareIntent(
                eq(mockActivityFirst),
                eq(routeA2F2.activityType),
                anyOrNull(),
                eq(FLAGS)
            )
        ).thenReturn(intent)
        doReturn(null).`when`(navigator).getVisibleFragment(mockActivityFirst, FRAGMENT_HOST_ID)

        router.bindActivity(mockActivitySecond, FRAGMENT_HOST_ID)
        router.navigateToForResult(destination, 2, extras, flags)

        verify(navigator, never()).startActivityForResult(
            any<AppCompatActivity>(),
            anyOrNull(),
            anyInt(),
            anyOrNull()
        )
    }

    @Test
    fun navigateToForResult_fromFragment() {
        //Given
        val listener: OnNavigationChangedListener = mock()

        val extras = mapOf(KEY to VALUE)
        whenever(routeParser.parse(eq(destination), anyOrNull())).thenReturn(routeA2F2)
        val flags = FLAGS

        val fragment: SecondFragment = mock()
        doReturn(fragment).`when`(navigator).getVisibleFragment(mockActivityFirst, FRAGMENT_HOST_ID)

        val intent: Intent = mock()
        whenever(
            navigator.prepareIntent(
                eq(fragment.context),
                eq(routeA2F2.activityType),
                anyOrNull(),
                eq(FLAGS)
            )
        ).thenReturn(intent)

        //When
        router.onNavigationChangedListeners.add(listener)
        router.bindActivity(mockActivityFirst, FRAGMENT_HOST_ID)
        router.navigateToForResult(destination, 2, extras, flags)

        //Then
        verify(navigator).startActivityForResult(fragment, intent, 2, null)
        assertEquals(FirstFragment::class.java, router.destinationFragment?.fragmentType)
        assertEquals(destination, router.destinationStack.last.first)
        assertEquals(routeA2F2, router.destinationStack.last.second)
        verify(listener).invoke(destination)
    }

    @Test
    fun navigateToForResult_fromFragment_withTransition() {
        val extras = mapOf(KEY to VALUE)
        whenever(routeParser.parse(eq(destination), anyOrNull())).thenReturn(
            routeA2F2.copy(
                activityTransition = Transition(
                    Pair(1, 1),
                    Pair(1, 1)
                )
            )
        )
        val flags = FLAGS

        val fragment: SecondFragment = mock()
        doReturn(fragment).`when`(navigator).getVisibleFragment(mockActivityFirst, FRAGMENT_HOST_ID)

        val intent: Intent = mock()
        whenever(
            navigator.prepareIntent(
                eq(fragment.context),
                eq(routeA2F2.activityType),
                anyOrNull(),
                eq(FLAGS)
            )
        ).thenReturn(intent)


        router.bindActivity(mockActivityFirst, FRAGMENT_HOST_ID)
        router.navigateToForResult(destination, 2, extras, flags)

        verify(navigator).startActivityForResult(
            fragment, intent, 2,  Pair(1, 1)
        )

        assertEquals(FirstFragment::class.java, router.destinationFragment?.fragmentType)
    }

    @Test(expected = UnknownDestinationException::class)
    fun navigateToForResult_NoRoute() {
        val extras = mapOf(KEY to VALUE)
        val flags = FLAGS

        router.navigateToForResult(destination, 2, extras, flags)
    }

    @Test
    fun navigateToForResult_NoCurrentActivity() {
        whenever(routeParser.parse(eq(destination), anyOrNull())).thenReturn(routeA2F2)
        router.navigateToForResult(destination, 3)
        verify(navigator, never()).startActivityForResult(
            eq(mockActivityFirst),
            any(),
            any(),
            eq(null)
        )
    }

    @Test
    fun navigateToForResult_fromActivity_withTransition() {
        //Gievn
        val extras = mapOf(KEY to VALUE)
        whenever(routeParser.parse(eq(destination), anyOrNull())).thenReturn(
            routeA2F2.copy(
                activityTransition = Transition(
                    Pair(1, 1),
                    Pair(1, 1)
                )
            )
        )
        val flags = FLAGS

        val intent: Intent = mock()

        whenever(
            navigator.prepareIntent(
                eq(mockActivityFirst),
                eq(routeA2F2.activityType),
                anyOrNull(),
                eq(FLAGS)
            )
        ).thenReturn(intent)
        doReturn(null).`when`(navigator).getVisibleFragment(mockActivityFirst, FRAGMENT_HOST_ID)

        //When
        router.bindActivity(mockActivityFirst, FRAGMENT_HOST_ID)
        router.navigateToForResult(destination, 2, extras, flags)

        //Then
        verify(navigator).startActivityForResult(
            mockActivityFirst, intent, 2,  Pair(1, 1)
        )
        assertEquals(FirstFragment::class.java, router.destinationFragment?.fragmentType)
        assertEquals(
            Pair(1,  1), router.nextBackTransition
        )
    }

    @Test
    fun navigateBack() {
        whenever(routeParser.parse(eq(destination), anyOrNull())).thenReturn(routeA2F2)
        whenever(navigator.getFragmentBackStackCount(mockActivityFirst)).thenReturn(0)

        router.bindActivity(mockActivityFirst)
        router.navigateTo(destination)
        router.navigateBack()

        verify(navigator).finishActivity(mockActivityFirst, null, Activity.RESULT_CANCELED, null)
    }

    @Test
    fun popRouteBackStack() {
        whenever(routeParser.parse(eq(destination), anyOrNull())).thenReturn(routeA2F2)
        whenever(routeParser.parse(eq(destination2), anyOrNull())).thenReturn(
            ScreenRoute(
                mockActivityFirst.javaClass,
                FirstFragment::class.java,
                Bundle(),
                null,
                null
            )
        )
        whenever(navigator.getFragmentBackStackCount(mockActivityFirst)).thenReturn(0)

        router.bindActivity(mockActivityFirst)
        router.navigateTo(destination)
        router.navigateTo(destination2)
        router.navigateBack()

        verify(navigator).finishActivity(mockActivityFirst, null, Activity.RESULT_CANCELED, null)
    }

    @Test
    fun navigateBack_FragmentBackStack() {
        whenever(navigator.getFragmentBackStackCount(mockActivityFirst)).thenReturn(9)
        router.bindActivity(mockActivityFirst)
        router.navigateBack()

        verify(navigator, never()).finishActivity(any(), any(), any(), eq(null))
        verify(navigator).popBackStack(mockActivityFirst)
    }

    @Test
    fun navigateBack_NoCurrentActivity() {
        router.navigateBack()

        verify(navigator, never()).finishActivity(any(), any(), any(), eq(null))
    }

    @Test
    fun navigateBack_Modal() {
        val currentModal: HostModal = mock()
        router.bindActivity(mockActivityFirst)
        whenever(navigator.findModal(mockActivityFirst, MODAL_TAG)).thenReturn(currentModal)
        whenever(navigator.getFragmentBackStackCountInModal(currentModal)).thenReturn(0)

        router.navigateBack()

        verify(currentModal).dismiss()
        verify(navigator, never()).finishActivity(any(), any(), any(), eq(null))
    }

    @Test
    fun navigateBack_ModalChild() {
        val currentModal: HostModal = mock()
        router.bindActivity(mockActivityFirst)
        whenever(navigator.findModal(mockActivityFirst, MODAL_TAG)).thenReturn(currentModal)
        whenever(navigator.getFragmentBackStackCountInModal(currentModal)).thenReturn(2)

        router.shouldAllowNavigateBack()
        router.navigateBack()

        verify(navigator).popBackStackInModal(currentModal)
        verify(currentModal, never()).dismiss()
        verify(navigator, never()).finishActivity(any(), any(), any(), eq(null))
    }

    @Test
    fun navigateBack_ModalChild_notAllowed() {
        val currentModal: HostModal = mock()
        val action: () -> Unit = mock()
        router.bindActivity(mockActivityFirst)
        whenever(navigator.findModal(mockActivityFirst, FRAGMENT_TAG)).thenReturn(currentModal)
        whenever(navigator.getFragmentBackStackCountInModal(currentModal)).thenReturn(2)

        router.shouldBlockNavigateBack(true, action)
        router.navigateBack()

        verify(navigator, never()).popBackStackInModal(currentModal)
        verify(currentModal, never()).dismiss()
        verify(navigator, never()).finishActivity(any(), any(), any(), eq(null))
        verify(action).invoke()
    }

    @Test
    fun finishWithResult() {
        val bundle: Bundle = mock()
        router.bindActivity(mockActivityFirst)
        router.finishWithResult(bundle, Activity.RESULT_OK)

        verify(navigator).finishActivity(mockActivityFirst, bundle, Activity.RESULT_OK, null)
    }

    @Test
    fun finishWithResult_NoCurrentActivity() {
        val bundle: Bundle = mock()
        router.finishWithResult(bundle, Activity.RESULT_OK)

        verify(navigator, never()).finishActivity(any(), any(), any(), eq(null))
    }

    @Test
    fun bindActivity() {
        router.bindActivity(mockActivityFirst)

        assertEquals(mockActivityFirst, router.currentActivityWrapper!!.activity)
        lifecycleFirst.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        verify(navigator, never()).replaceFragment(
            any<FragmentActivity>(),
            anyInt(),
            any(),
            anyString(),
            eq(true),
            anyString(),
            anyOrNull()
        )

        mockActivityFirst.finish()

        assertNull(router.currentActivityWrapper)
    }

    @Test
    fun bindActivity_NavigateToFragment() {
        val fragmentSecond: SecondFragment = mock()

        whenever(routeParser.parse(eq(destination), anyOrNull())).thenReturn(routeA2F2)
        whenever(navigator.createFragment(any(), any(), anyOrNull())).thenReturn(fragmentSecond)

        router.destinationFragment =
            DefaultRouter.FragmentWrapper(SecondFragment::class.java, null, null)
        router.bindActivity(mockActivityFirst, FRAGMENT_HOST_ID)
        lifecycleFirst.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        assertEquals(mockActivityFirst, router.currentActivityWrapper!!.activity)

        verify(navigator).replaceFragment(
            eq(mockActivityFirst),
            eq(FRAGMENT_HOST_ID),
            eq(fragmentSecond),
            eq("current_fragment"),
            eq(true),
            anyString(),
            anyOrNull()
        )
    }

    @Test
    fun bindActivity_NotBinded() {
        //Activity 1 binds
        router.bindActivity(mockActivityFirst)

        //Activity 2 binds
        router.bindActivity(mockActivitySecond)

        //Activity 1 tries to unbind
        mockActivityFirst.finish()

        assertEquals(mockActivitySecond, router.currentActivityWrapper!!.activity)
    }

    @Test
    fun getCurrentActivity() {
        router.bindActivity(mockActivitySecond, FRAGMENT_HOST_ID)
        assertEquals(mockActivitySecond, router.currentActivity)
    }

    @Test
    fun getCurrentActivity_Null() {
        assertNull(router.currentActivity)
    }

    @Test
    fun getCurrentFragment() {
        val fragment: SecondFragment = mock()
        whenever(navigator.getVisibleFragment(mockActivitySecond, FRAGMENT_HOST_ID)).thenReturn(
            fragment
        )
        router.bindActivity(mockActivitySecond, FRAGMENT_HOST_ID)

        assertEquals(fragment, router.currentFragment)
    }

    @Test
    fun getCurrentFragment_Null() {
        assertNull(router.currentFragment)
    }

    @Test
    fun getCurrentFragment_NoFragment() {
        router.bindActivity(mockActivitySecond, null)

        assertNull(router.currentFragment)
    }

    @Test
    fun openExternalApplication_other() {
        router.bindActivity(mockActivityFirst)
        router.openExternalApplication(ExternalAppType.SHARE) {
            text = "hello"
        }

        verify(navigator).openSharingApplication(mockActivityFirst, "hello")
    }

    @Test
    fun openExternalApplication_share() {
        router.bindActivity(mockActivityFirst)
        router.openExternalApplication(ExternalAppType.SHARE) {
            text = "hello"
        }

        verify(navigator).openSharingApplication(mockActivityFirst, "hello")
    }

    @Test
    fun openExternalApplication_PhotoGallery() {
        whenever(mockActivityFirst.runOnUiThread(any())).then {
            it.getArgument<Runnable>(0).run()
        }
        router.bindActivity(mockActivityFirst)
        router.openExternalApplication(ExternalAppType.PHOTO_GALLERY) {
            requestCode = 1244
        }

        verify(navigator).openPhotoGalleryApplication(mockActivityFirst, 1244)
    }

    @Test
    fun openPhotoGalleryApp_Modal() {
        val mockRequestCode = 1234
        val modal: HostModal = mock()
        whenever(mockActivityFirst.runOnUiThread(any())).then {
            it.getArgument<Runnable>(0).run()
        }
        whenever(navigator.findModal(mockActivityFirst, MODAL_TAG)).thenReturn(modal)
        router.bindActivity(mockActivityFirst, FRAGMENT_HOST_ID)

        router.openPhotoGalleryApp(mockRequestCode)
        verify(navigator).openPhotoGalleryApplication(modal, mockRequestCode)
    }

    @Test
    fun openPhotoGalleryApp_Fragment() {
        val mockRequestCode = 1234
        val fragment: FirstFragment = mock()
        whenever(mockActivityFirst.runOnUiThread(any())).then {
            it.getArgument<Runnable>(0).run()
        }
        whenever(navigator.getVisibleFragment(mockActivityFirst, FRAGMENT_HOST_ID)).thenReturn(
            fragment
        )
        router.bindActivity(mockActivityFirst, FRAGMENT_HOST_ID)


        router.openPhotoGalleryApp(mockRequestCode)
        verify(navigator).openPhotoGalleryApplication(fragment, mockRequestCode)
    }

    @Test
    fun openPhotoGalleryApp_Activity() {
        val mockRequestCode = 1234
        whenever(mockActivityFirst.runOnUiThread(any())).then {
            it.getArgument<Runnable>(0).run()
        }
        router.bindActivity(mockActivityFirst)


        router.openPhotoGalleryApp(mockRequestCode)
        verify(navigator).openPhotoGalleryApplication(mockActivityFirst, mockRequestCode)
    }

    @Test
    fun openExternalApplication_Camera() {
        whenever(mockActivityFirst.runOnUiThread(any())).then {
            it.getArgument<Runnable>(0).run()
        }
        router.bindActivity(mockActivityFirst)
        router.openExternalApplication(ExternalAppType.CAMERA) {
            requestCode = 1244
            tempPictureFileUri = "foo"
        }

        verify(navigator).openCameraApplication(mockActivityFirst, "foo", 1244)
    }

    @Test
    fun openCameraApp_Modal() {
        val mockRequestCode = 1234
        val modal: HostModal = mock()
        whenever(mockActivityFirst.runOnUiThread(any())).then {
            it.getArgument<Runnable>(0).run()
        }
        whenever(navigator.findModal(mockActivityFirst, MODAL_TAG)).thenReturn(modal)
        router.bindActivity(mockActivityFirst, FRAGMENT_HOST_ID)

        router.openCameraApp(mockRequestCode, "foo")
        verify(navigator).openCameraApplication(modal, "foo", mockRequestCode)
    }

    @Test
    fun openCameraApp_Fragment() {
        val mockRequestCode = 1234
        val fragment: FirstFragment = mock()
        whenever(mockActivityFirst.runOnUiThread(any())).then {
            it.getArgument<Runnable>(0).run()
        }
        whenever(navigator.getVisibleFragment(mockActivityFirst, FRAGMENT_HOST_ID)).thenReturn(
            fragment
        )
        router.bindActivity(mockActivityFirst, FRAGMENT_HOST_ID)


        router.openCameraApp(mockRequestCode, "foo")
        verify(navigator).openCameraApplication(fragment, "foo", mockRequestCode)
    }

    @Test
    fun openCameraApp_Activity() {
        val mockRequestCode = 1234
        whenever(mockActivityFirst.runOnUiThread(any())).then {
            it.getArgument<Runnable>(0).run()
        }
        router.bindActivity(mockActivityFirst)


        router.openCameraApp(mockRequestCode, "foo")
        verify(navigator).openCameraApplication(mockActivityFirst, "foo", mockRequestCode)
    }

    @Test
    fun openExternalApplication_share_NoActivityBinded() {
        router.openExternalApplication(ExternalAppType.SHARE) {
            text = "hello"
        }

        verify(navigator, never()).openSharingApplication(any(), anyString())
    }

    @Test
    fun openExternalApplication_email() {
        //When
        router.bindActivity(mockActivityFirst)
        whenever(navigator.encodeForUri("hello")).thenReturn("hello")
        whenever(navigator.encodeForUri("Subject")).thenReturn("Subject")

        //When
        router.openExternalApplication(ExternalAppType.EMAIL) {
            text = "hello"
            toEmails = arrayOf("toto@gmail.com")
            emailSubject = "Subject"
            emailTitle = "title"
        }

        //Then
        verify(navigator).openEmailApplication(
            eq(mockActivityFirst),
            eq("mailto:toto@gmail.com&subject=Subject&body=hello"),
            eq("title")
        )
    }

    @Test
    fun openExternalApplication_email_NoActivityBinded() {
        whenever(navigator.encodeForUri("hello")).thenReturn("hello")
        whenever(navigator.encodeForUri("Subject")).thenReturn("Subject")

        //When
        router.openExternalApplication(ExternalAppType.EMAIL) {
            text = "hello"
            toEmails = arrayOf("toto@gmail.com")
            emailSubject = "Subject"
            emailTitle = "title"
        }

        //Then
        verify(navigator, never()).openEmailApplication(any(), any(), any())
    }

    @Test
    fun openExternalApplication_email_no_title() {
        //When
        router.bindActivity(mockActivityFirst)

        //When
        router.openExternalApplication(ExternalAppType.EMAIL) {
            text = "hello"
            toEmails = arrayOf("toto@gmail.com")
            emailSubject = "Subject"
            //NO TITLE
        }

        //Then
        verify(navigator, never()).openEmailApplication(any(), any(), any())
    }

    @Test
    fun openExternalApplication_email_no_recipients() {
        //When
        router.bindActivity(mockActivityFirst)

        //When
        router.openExternalApplication(ExternalAppType.EMAIL) {
            text = "hello"
            //NO EMAILS
            emailSubject = "Subject"
            emailTitle = "title"
        }

        //Then
        verify(navigator, never()).openEmailApplication(any(), any(), any())
    }

    @Test
    fun openExternalApplication_email_no_subject_no_text() {
        //When
        router.bindActivity(mockActivityFirst)
        whenever(navigator.encodeForUri("hello")).thenReturn("hello")
        whenever(navigator.encodeForUri("Subject")).thenReturn("Subject")

        //When
        router.openExternalApplication(ExternalAppType.EMAIL) {
            toEmails = arrayOf("toto@gmail.com")
            emailTitle = "title"
        }

        //Then
        verify(navigator).openEmailApplication(
            eq(mockActivityFirst),
            eq("mailto:toto@gmail.com"),
            eq("title")
        )
    }

    @Test
    fun addOnNavigationChangedListener() {
        val listener: OnNavigationChangedListener = mock()
        router.addOnNavigationChangedListener(listener)

        assertEquals(1, router.onNavigationChangedListeners.size)
        assertEquals(listener, router.onNavigationChangedListeners[0])
    }

    @Test
    fun removeOnNavigationChangedListener() {
        val listener: OnNavigationChangedListener = mock()
        router.addOnNavigationChangedListener(listener)
        router.removeOnNavigationChangedListener(listener)

        assertEquals(0, router.onNavigationChangedListeners.size)
    }

    @Test
    fun bindEventDelegate() {
        val eventDelegate: ViewDelegate = mock()
        router.bindViewDelegate(eventDelegate)
        assertEquals(eventDelegate, router.viewDelegateList.firstOrNull())
    }

    @Test
    fun unBindEventDelegate() {
        val eventDelegate: ViewDelegate = mock()
        router.bindViewDelegate(eventDelegate)

        router.unbindViewDelegate(eventDelegate)
        assertEquals(0, router.viewDelegateList.size)
    }

    @Test
    fun findEventDelegateByType() {
        val eventDelegate: ValidDelegate = mock()
        val invalidDelegate: InvalidDelegate = mock()

        router.bindViewDelegate(eventDelegate)
        router.bindViewDelegate(invalidDelegate)

        router.findViewDelegateByType<ValidDelegate> {
            it.foobar()
        }

        verify(eventDelegate).foobar()
        verify(invalidDelegate, never()).foobar()
    }

    @Test
    fun navigateToModal() {
        router.bindActivity(mockActivityFirst, FRAGMENT_HOST_ID)

        val newModal: HostModal = mock()
        val destination = "FoobarModal"
        val extras = mapOf(KEY to VALUE)
        whenever(routeParser.parse(destination, extras)).thenReturn(ModalRoute(HostModal::class.java, null, bundleOf(), null))
        whenever(navigator.createModal(eq(mockActivityFirst), eq(HostModal::class.java), anyOrNull())).thenReturn(newModal)

        router.bindActivity(mockActivityFirst, FRAGMENT_HOST_ID)

        router.navigateTo(destination, extras)

        verify(navigator).openModal(eq(mockActivityFirst), eq(newModal), anyOrNull(), eq(MODAL_TAG))
    }

    @Test
    fun navigateToModal_Dismiss() {
        val oldModal: AppCompatDialogFragment = mock()
        val newModal: HostModal = mock()
        whenever(navigator.findModal(mockActivityFirst, MODAL_TAG)).thenReturn(oldModal)
        router.bindActivity(mockActivityFirst, FRAGMENT_HOST_ID)

        val destination = "FoobarModal"
        val extras = mapOf(KEY to VALUE)
        whenever(routeParser.parse(destination, extras)).thenReturn(ModalRoute(HostModal::class.java, null, bundleOf(), null))
        whenever(navigator.createModal(eq(mockActivityFirst), eq(HostModal::class.java), anyOrNull())).thenReturn(newModal)

        router.bindActivity(mockActivityFirst, FRAGMENT_HOST_ID)

        router.navigateTo(destination, extras)

        verify(oldModal).dismiss()
        verify(navigator).openModal(eq(mockActivityFirst), eq(newModal), anyOrNull(), eq(MODAL_TAG))
    }

    @Test
    fun navigateTo_SubModal() {
        val fragmentHostId = 854
        val currentModal: HostModal = mock()
        val modalChild: FirstFragment = mock()

        val destination = "FoobarModal"
        val extras = mapOf(KEY to VALUE)
        whenever(navigator.findModal(mockActivityFirst, MODAL_TAG)).thenReturn(currentModal)
        whenever(routeParser.parse(destination, extras)).thenReturn(
            ModalRoute(
                currentModal.javaClass,
                modalChild.javaClass,
                bundleOf(),
                null
            )
        )
        whenever(currentModal.fragmentHostId).thenReturn(fragmentHostId)
        whenever(navigator.createFragmentInModal(eq(currentModal), eq(modalChild.javaClass), anyOrNull())).thenReturn(modalChild)
        router.bindActivity(mockActivityFirst, FRAGMENT_HOST_ID)

        router.navigateTo(destination, extras)

        verify(navigator).replaceFragmentInModal(
            eq(currentModal),
            eq(fragmentHostId),
            eq(modalChild),
            eq(FRAGMENT_TAG),
            eq(true),
            eq(modalChild.javaClass.name),
            eq(null)
        )
    }

    @Test
    fun getCurrentModal() {
        val modal: AppCompatDialogFragment = mock()
        whenever(navigator.findModal(mockActivitySecond, MODAL_TAG)).thenReturn(modal)
        router.bindActivity(mockActivitySecond, FRAGMENT_HOST_ID)

        assertEquals(modal, router.currentModal)
    }

    @Test
    fun shouldBlockNavigateBack_meetCondition() {
        //Given
        val action: () -> Unit = mock()
        router.bindActivity(mockActivityFirst)

        //When
        router.shouldBlockNavigateBack(true, action)
        router.navigateBackListener.invoke()

        //Then
        verify(action).invoke()
    }

    @Test
    fun shouldBlockNavigateBack_dontMeetCondition() {
        //Given
        val action: () -> Unit = mock()
        router.bindActivity(mockActivityFirst)

        //When
        router.shouldBlockNavigateBack(false, action)
        router.navigateBackListener.invoke()

        //Then
        verify(action, never()).invoke()
    }

    private companion object {
        const val destination = "app://mock.io"
        const val destination2 = "app://mock2.io"
        const val KEY = "foo"
        const val VALUE = 42
        val FLAGS = LaunchScreenFlags.NO_DUPLICATE_ON_TOP

        const val FRAGMENT_HOST_ID = 9631
    }

    private abstract class FirstActivity : AppCompatActivity()

    private abstract class FirstFragment : Fragment()

    private abstract class SecondActivity : AppCompatActivity()

    private abstract class SecondFragment : Fragment()

    private abstract class HostModal : AppCompatDialogFragment(), FragmentHost

    private interface ValidDelegate : ViewDelegate {
        fun foobar()
    }

    private interface InvalidDelegate : ViewDelegate {
        fun foobar()
    }
}