package dev.marcosfarias.pokedex.ui.home

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.navigation.FragmentHost
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.marcosfarias.pokedex.R

class ProfileModal : BottomSheetDialogFragment(), FragmentHost {

    override val fragmentHostId = R.id.modal_fragment_host

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_modal, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setModalAsFullscreen()
    }

    private fun setModalAsFullscreen() {
        dialog?.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheetInternal =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheetInternal?.let {
                it.layoutParams?.height = Resources.getSystem().displayMetrics.heightPixels - 120
                it.requestLayout()
                val behaviour: BottomSheetBehavior<View> = BottomSheetBehavior.from(it)
                behaviour.peekHeight = Resources.getSystem().displayMetrics.heightPixels - 120
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }
}