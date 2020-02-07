package dev.marcosfarias.pokedex.ui.newsdetail

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.marcosfarias.pokedex.R
import dev.marcosfarias.pokedex.ui.home.ThemeViewModel
import dev.marcosfarias.pokedex.utils.PokemonColorUtil
import kotlinx.android.synthetic.main.fragment_news_detail.*

class NewsDetailModal : BottomSheetDialogFragment() {

    private lateinit var newsViewModel: NewsDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsViewModel = ViewModelProviders.of(this).get(NewsDetailViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_news_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.statusBarColor =
            PokemonColorUtil(view.context).convertColor(R.color.white)

        makeModalFullscreen()

        shareButton.setOnClickListener {
            newsViewModel.onShareClicked()
        }
    }

    private fun makeModalFullscreen() {
        dialog?.setOnShowListener { dialog ->
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheetInternal =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheetInternal?.let {
                bottomSheetInternal.maximizeLayoutHeight()

                val behaviour = BottomSheetBehavior.from(bottomSheetInternal)
                behaviour.peekHeight =
                    Resources.getSystem().displayMetrics.heightPixels
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun View.maximizeLayoutHeight() {
        layoutParams?.height = Resources.getSystem().displayMetrics.heightPixels
        requestLayout()
    }

}
