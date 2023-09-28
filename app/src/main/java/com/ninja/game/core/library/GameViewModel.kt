package com.ninja.game.core.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class GameViewModel : ViewModel() {
    var gameState = true
    var pauseState = false
    protected var gameScope = CoroutineScope(Dispatchers.Default)

    protected val _playerXY = MutableLiveData<XY>()
    val playerXY: LiveData<XY> = _playerXY

    private suspend fun moveSomethingDown(
        maxY: Int,
        objHeight: Int,
        objWidth: Int,
        playerWidth: Int,
        playerHeight: Int,
        oldList: MutableList<XY>,
        onIntersect: ((XY) -> Unit) = {},
        onOutOfScreen: ((XY) -> Unit) = {},
        distance: Int
    ): MutableList<XY> {
        return suspendCoroutine { cn ->
            val newList = mutableListOf<XY>()
            oldList.forEach { obj ->
                if (obj.y <= maxY) {
                    val objX = obj.x.toInt()..obj.x.toInt() + objWidth
                    val objY = obj.y.toInt()..obj.y.toInt() + objHeight
                    val playerX =
                        _playerXY.value!!.x.toInt().._playerXY.value!!.x.toInt() + playerWidth
                    val playerY =
                        _playerXY.value!!.y.toInt().._playerXY.value!!.y.toInt() + playerHeight
                    if (playerX.any { it in objX } && playerY.any { it in objY }) {
                        onIntersect.invoke(obj)
                    } else {
                        obj.y = obj.y + distance
                        obj.x = obj.x
                        newList.add(obj)
                    }
                } else {
                    onOutOfScreen.invoke(obj)
                }
            }
            cn.resume(newList)
        }
    }

    private suspend fun moveSomethingLeft(
        objHeight: Int,
        objWidth: Int,
        playerWidth: Int,
        playerHeight: Int,
        oldList: MutableList<XY>,
        onIntersect: ((XY) -> Unit) = {},
        onOutOfScreen: ((XY) -> Unit) = {},
        distance: Int
    ): MutableList<XY> {
        return suspendCoroutine { cn ->
            val newList = mutableListOf<XY>()
            oldList.forEach { obj ->
                if (obj.x <= 0f) {
                    val objX = obj.x.toInt()..obj.x.toInt() + objWidth
                    val objY = obj.y.toInt()..obj.y.toInt() + objHeight
                    val playerX =
                        _playerXY.value!!.x.toInt().._playerXY.value!!.x.toInt() + playerWidth
                    val playerY =
                        _playerXY.value!!.y.toInt().._playerXY.value!!.y.toInt() + playerHeight
                    if (playerX.any { it in objX } && playerY.any { it in objY }) {
                        onIntersect.invoke(obj)
                    } else {
                        obj.y = obj.y
                        obj.x = obj.x - distance
                        newList.add(obj)
                    }
                } else {
                    onOutOfScreen.invoke(obj)
                }
            }
            cn.resume(newList)
        }
    }

    private suspend fun moveSomethingRight(
        maxX: Int,
        objHeight: Int,
        objWidth: Int,
        playerWidth: Int,
        playerHeight: Int,
        oldList: MutableList<XY>,
        onIntersect: ((XY) -> Unit) = {},
        onOutOfScreen: ((XY) -> Unit) = {},
        distance: Int
    ): MutableList<XY> {
        return suspendCoroutine { cn ->
            val newList = mutableListOf<XY>()
            oldList.forEach { obj ->
                if (obj.x >= maxX) {
                    val objX = obj.x.toInt()..obj.x.toInt() + objWidth
                    val objY = obj.y.toInt()..obj.y.toInt() + objHeight
                    val playerX =
                        _playerXY.value!!.x.toInt().._playerXY.value!!.x.toInt() + playerWidth
                    val playerY =
                        _playerXY.value!!.y.toInt().._playerXY.value!!.y.toInt() + playerHeight
                    if (playerX.any { it in objX } && playerY.any { it in objY }) {
                        onIntersect.invoke(obj)
                    } else {
                        obj.y = obj.y
                        obj.x = obj.x + distance
                        newList.add(obj)
                    }
                } else {
                    onOutOfScreen.invoke(obj)
                }
            }
            cn.resume(newList)
        }
    }

    private suspend fun moveSomethingUp(
        objHeight: Int,
        objWidth: Int,
        playerWidth: Int,
        playerHeight: Int,
        oldList: MutableList<XY>,
        onIntersect: ((XY) -> Unit) = {},
        onOutOfScreen: ((XY) -> Unit) = {},
        distance: Int
    ): MutableList<XY> {
        return suspendCoroutine { cn ->
            val newList = mutableListOf<XY>()
            oldList.forEach { obj ->
                if (obj.y <= 0) {
                    val objX = obj.x.toInt()..obj.x.toInt() + objWidth
                    val objY = obj.y.toInt()..obj.y.toInt() + objHeight
                    val playerX =
                        _playerXY.value!!.x.toInt().._playerXY.value!!.x.toInt() + playerWidth
                    val playerY =
                        _playerXY.value!!.y.toInt().._playerXY.value!!.y.toInt() + playerHeight
                    if (playerX.any { it in objX } && playerY.any { it in objY }) {
                        onIntersect.invoke(obj)
                    } else {
                        obj.y = obj.y - distance
                        obj.x = obj.x
                        newList.add(obj)
                    }
                } else {
                    onOutOfScreen.invoke(obj)
                }
            }
            cn.resume(newList)
        }
    }

    fun stop() {
        gameScope.cancel()
    }
}