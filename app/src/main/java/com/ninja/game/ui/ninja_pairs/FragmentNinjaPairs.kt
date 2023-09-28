package com.ninja.game.ui.ninja_pairs

import android.media.Image
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.ninja.game.R
import com.ninja.game.core.library.GameFragment
import com.ninja.game.databinding.FragmentNinjaPairsBinding
import com.ninja.game.domain.adapter.PairsAdapter
import com.ninja.game.ui.other.CallBackViewModel
import com.ninja.game.ui.other.MainActivity

class FragmentNinjaPairs: GameFragment<FragmentNinjaPairsBinding>(FragmentNinjaPairsBinding::inflate) {
    private lateinit var pairsAdapter: PairsAdapter
    private val viewModel: NinjaPairsViewModel by viewModels()
    private val callbackViewModel: CallBackViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        viewModel.winCallback = {
            end(true)
        }

        viewModel.list.observe(viewLifecycleOwner) {
            pairsAdapter.items = it.toMutableList()
            pairsAdapter.notifyDataSetChanged()
        }
        viewModel.timer.observe(viewLifecycleOwner) { totalSecs ->
            binding.timer.removeAllViews()
            repeat(totalSecs / 2) {
                val timerView = ImageView(requireContext())
                timerView.layoutParams = LinearLayout.LayoutParams(dpToPx(9), dpToPx(14))
                timerView.setImageResource(R.drawable.time03)
                binding.timer.addView(timerView)
            }

            if (totalSecs == 50 && viewModel.gameState && !viewModel.pauseState) {
                end(false)
            }
        }

        if (viewModel.gameState && !viewModel.pauseState) {
            viewModel.startTimer()
        }

    }

    private fun end(isWin: Boolean) {
        viewModel.stopTimer()
        viewModel.gameState = false
        callbackViewModel.pairsCallback?.invoke(isWin)
        (requireActivity() as MainActivity).navigateBack()
    }

    private fun initAdapter() {
        pairsAdapter = PairsAdapter {
            if (viewModel.list.value!!.find { it.closeAnimation } == null && viewModel.list.value!!.find { it.openAnimation } == null) {
                viewModel.openItem(it)
            }
        }
        with(binding.gameRV) {
            adapter = pairsAdapter
            layoutManager = GridLayoutManager(requireContext(), 6).also {
                it.orientation = GridLayoutManager.VERTICAL
            }
            setHasFixedSize(true)
            itemAnimator = null
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopTimer()
    }
}