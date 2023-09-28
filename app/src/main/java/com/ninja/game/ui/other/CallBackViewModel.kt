package com.ninja.game.ui.other

import androidx.lifecycle.ViewModel

class CallBackViewModel : ViewModel() {
    var pairsCallback: ((isWin: Boolean) -> Unit)? = null
    var pauseCallback: (() -> Unit)? = null
}