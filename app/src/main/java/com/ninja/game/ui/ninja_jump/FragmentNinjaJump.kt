package com.ninja.game.ui.ninja_jump

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ninja.game.R
import com.ninja.game.core.library.GameFragment
import com.ninja.game.databinding.FragmentNunjaJumpBinding
import com.ninja.game.ui.dialogs.DialogEnding
import com.ninja.game.ui.dialogs.DialogPause
import com.ninja.game.ui.ninja_pairs.FragmentNinjaPairs
import com.ninja.game.ui.other.CallBackViewModel
import com.ninja.game.ui.other.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentNinjaJump :
    GameFragment<FragmentNunjaJumpBinding>(FragmentNunjaJumpBinding::inflate) {
    private val viewModel: NinjaJumpViewModel by viewModels()
    private val callbackViewModel: CallBackViewModel by activityViewModels()
    private var moveScope = CoroutineScope(Dispatchers.Default)
    private val pauseCallback: (() -> Unit) by lazy {
        {
            lifecycleScope.launch {
                delay(20)
                if (viewModel.gameState) {
                    viewModel.pauseState = false
                    viewModel.start(
                        dpToPx(50),
                        xy.x.toInt(),
                        dpToPx(50),
                        binding.player.width,
                        binding.player.height,
                        binding.enemy.width,
                        binding.enemy.height,
                    )
                }
            }
        }
    }
    private val pairsCallback: ((isWin: Boolean) -> Unit) by lazy {
        {
            lifecycleScope.launch {
                delay(20)
                if (!it) {
                    viewModel.removeLife()
                } else {
                    viewModel.addScores(100)
                }
                delay(5)
                if (viewModel.gameState) {
                    viewModel.pauseState = false
                    viewModel.start(
                        dpToPx(50),
                        xy.x.toInt(),
                        dpToPx(50),
                        binding.player.width,
                        binding.player.height,
                        binding.enemy.width,
                        binding.enemy.height,
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtons()

        binding.menu.setOnClickListener {
            childFragmentManager.popBackStack()
        }

        callbackViewModel.pauseCallback = pauseCallback
        callbackViewModel.pairsCallback = pairsCallback

        viewModel.touchCallback = {
            viewModel.pauseState = true
            viewModel.stop()
            viewModel.stopAnother()
            (requireActivity() as MainActivity).navigate(FragmentNinjaPairs())
        }

        binding.pause.setOnClickListener {
            viewModel.pauseState = true
            viewModel.stop()
            viewModel.stopAnother()
            (requireActivity() as MainActivity).navigateToDialog(DialogPause())
        }

        viewModel.lives.observe(viewLifecycleOwner) {
            binding.livesLayout.removeAllViews()
            repeat(it) {
                val liveView = ImageView(requireContext())
                liveView.layoutParams = LinearLayout.LayoutParams(dpToPx(30), dpToPx(30)).apply {
                    marginStart = dpToPx(3)
                    marginEnd = dpToPx(3)
                }
                liveView.setImageResource(R.drawable.life)
                binding.livesLayout.addView(liveView)
            }

            if (it == 0 && viewModel.gameState) {
                viewModel.stop()
                viewModel.stopAnother()
                viewModel.gameState = false
                (requireActivity() as MainActivity).navigateToDialog(DialogEnding().apply {
                    arguments = Bundle().apply {
                        putString("SCORE", viewModel.scores.value.toString())
                    }
                })
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trigger.collect {
                    binding.platformsLayout.removeAllViews()
                    viewModel.platforms.value.forEach { platform ->
                        val platformContainer = LinearLayout(requireContext())
                        platformContainer.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        platformContainer.x = platform.x
                        platformContainer.y = platform.y
                        repeat(platform.platformLength + 1) {
                            val platformView = ImageView(requireContext())
                            val layoutParams = ViewGroup.LayoutParams(dpToPx(50), dpToPx(50))
                            platformView.layoutParams = layoutParams
                            platformView.setImageResource(R.drawable.platform)
                            platformContainer.addView(platformView)
                        }
                        binding.platformsLayout.addView(platformContainer)
                    }
                }
            }
        }
        viewModel.playerXY.observe(viewLifecycleOwner) {
            binding.player.x = it.x
            binding.player.y = it.y

            if (it.y >= xy.y && viewModel.gameState) {
                viewModel.respawnPlayer(dpToPx(50), binding.player.width, binding.player.height)
            }
        }

        viewModel.scores.observe(viewLifecycleOwner) {
            binding.score.text = it.toString()
        }

        viewModel.enemyPos.observe(viewLifecycleOwner) {
            binding.enemy.x = it.x
            binding.enemy.y = it.y
        }

        startAction = {
            lifecycleScope.launch {
                binding.apply {
                    if (viewModel.platforms.value.isEmpty()) {
                        viewModel.initPlatforms(
                            centerView.y,
                            centerView.y + dpToPx(100),
                            centerView.y + dpToPx(100) * 2,
                            centerView.y + dpToPx(100) * 3,
                            centerView.y + dpToPx(100) * 4,
                            centerView.y + dpToPx(100) * 5,
                            xy.x.toInt(),
                            binding.player.height,
                            binding.player.width
                        )
                    }
                }
                if (viewModel.gameState && !viewModel.pauseState) {
                    viewModel.start(
                        dpToPx(50),
                        xy.x.toInt(),
                        dpToPx(50),
                        binding.player.width,
                        binding.player.height,
                        binding.enemy.width,
                        binding.enemy.height,
                    )
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setButtons() {
        binding.buttonLeft.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    moveScope.launch {
                        while (true) {
                            viewModel.isGoingLeft = true
                            delay(2)
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            viewModel.isGoingLeft = true
                            delay(2)
                        }
                    }
                    true
                }

                else -> {
                    viewModel.isGoingLeft = false
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    false
                }
            }
        }

        binding.buttonRight.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    moveScope.launch {
                        while (true) {
                            viewModel.isGoingRight = true
                            delay(2)
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            viewModel.isGoingRight = true
                            delay(2)
                        }
                    }
                    true
                }

                else -> {
                    viewModel.isGoingRight = false
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    false
                }
            }
        }

        binding.buttonUp.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    moveScope.launch {
                        while (true) {
                            if (viewModel.isStopped) {
                                viewModel.jump()
                            }
                            delay(2)
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            if (viewModel.isStopped) {
                                viewModel.jump()
                            }
                            delay(2)
                        }
                    }
                    true
                }

                else -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    false
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopAnother()
    }
}