package com.ninja.game.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.activityViewModels
import com.ninja.game.R
import com.ninja.game.core.library.ViewBindingDialog
import com.ninja.game.databinding.DialogPauseBinding
import com.ninja.game.ui.ninja_jump.FragmentNinjaJump
import com.ninja.game.ui.other.CallBackViewModel
import com.ninja.game.ui.other.MainActivity

class DialogPause: ViewBindingDialog<DialogPauseBinding>(DialogPauseBinding::inflate) {
    private val callbackViewModel: CallBackViewModel by activityViewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                callbackViewModel.pauseCallback?.invoke()
                dialog?.cancel()
                true
            } else {
                false
            }
        }
        binding.play.setOnClickListener {
            callbackViewModel.pauseCallback?.invoke()
            dialog?.cancel()
        }
    }
}