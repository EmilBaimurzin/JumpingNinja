package com.ninja.game.domain

import com.ninja.game.core.library.XY

data class Platform (
    override var x: Float,
    override var y: Float,
    val platformLength: Int,
    val isMovingLeft: Boolean,
    var isInitial: Boolean = false,
    var isNinja: Boolean = false
): XY