package com.ninja.game.ui.pre

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.ninja.game.databinding.FragmentPreBinding
import com.ninja.game.core.library.GameFragment
import com.ninja.game.ui.ninja_jump.FragmentNinjaJump
import com.ninja.game.ui.other.MainActivity

class FragmentPre : GameFragment<FragmentPreBinding>(FragmentPreBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.play.setOnClickListener {
            (requireActivity() as MainActivity).navigate(FragmentNinjaJump())
        }

        binding.exit.setOnClickListener {
            requireActivity().finish()
        }

        binding.privacyText.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("https://www.google.com")
                )
            )
        }
    }
}