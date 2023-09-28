package com.ninja.game.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.ninja.game.R
import com.ninja.game.core.library.ViewBindingDialog
import com.ninja.game.databinding.DialogEndingBinding
import com.ninja.game.ui.ninja_jump.FragmentNinjaJump
import com.ninja.game.ui.other.MainActivity

class DialogEnding: ViewBindingDialog<DialogEndingBinding>(DialogEndingBinding::inflate) {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                (requireActivity() as MainActivity).navigateBack("main")
                dialog?.cancel()
                true
            } else {
                false
            }
        }
        binding.menu.setOnClickListener {
            (requireActivity() as MainActivity).navigateBack("main")
            dialog?.cancel()
        }
        binding.close.setOnClickListener {
            (requireActivity() as MainActivity).navigateBack("main")
            dialog?.cancel()
        }
        binding.restart.setOnClickListener {
            (requireActivity() as MainActivity).navigate(FragmentNinjaJump())
            dialog?.cancel()
        }
        binding.score.text = arguments?.getString("SCORE")
    }
}