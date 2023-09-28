package com.ninja.game.domain.ninja_pairs

import java.util.Random

data class NinjaPairsItem(
    val id: Int = Random().nextInt(),
    val value: Int,
    var isOpened: Boolean = false,
    var lastOpened: Boolean = false,
    var openAnimation: Boolean = false,
    var closeAnimation: Boolean = false
)