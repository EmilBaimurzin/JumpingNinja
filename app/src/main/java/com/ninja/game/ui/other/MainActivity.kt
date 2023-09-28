package com.ninja.game.ui.other

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ninja.game.R
import com.ninja.game.ui.pre.FragmentPre

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, FragmentPre(), "main")
                .addToBackStack("main")
                .commit()
        }

        this.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (supportFragmentManager.findFragmentById(R.id.fragment)?.tag != "main") {
                    supportFragmentManager.popBackStack("main", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, FragmentPre(), "main")
                        .addToBackStack("main")
                        .commit()
                }
            }
        })
    }
    fun navigate(fragment: Fragment, name: String? = null, tag: String? = null) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment, tag)
            .addToBackStack(name)
            .commit()
    }

    fun navigateToDialog(dialog: DialogFragment) {
        dialog.show(supportFragmentManager, "dialog")
    }

    fun navigateBack(name: String = "") {
        if (name != "") {
            supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, FragmentPre(), "main")
                .addToBackStack(name)
                .commit()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}