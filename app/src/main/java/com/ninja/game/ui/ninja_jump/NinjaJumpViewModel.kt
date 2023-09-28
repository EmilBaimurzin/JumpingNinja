package com.ninja.game.ui.ninja_jump

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ninja.game.core.library.GameViewModel
import com.ninja.game.core.library.XYIMpl
import com.ninja.game.core.library.random
import com.ninja.game.domain.Platform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Random

class NinjaJumpViewModel : GameViewModel() {
    private val _platforms = MutableStateFlow<List<Platform>>(emptyList())
    val platforms = _platforms.asStateFlow()

    private val _scores = MutableLiveData(0)
    val scores: LiveData<Int> = _scores

    private val _lives = MutableLiveData(3)
    val lives: LiveData<Int> = _lives

    private val _enemyPos = MutableLiveData(XYIMpl(0f, 0f))
    val enemyPos: LiveData<XYIMpl> = _enemyPos

    private val _trigger = MutableStateFlow(true)
    val trigger = _trigger.asStateFlow()

    var touchCallback: (() -> Unit)? = null

    var speed = 5L

    private var jumpScope = CoroutineScope(Dispatchers.Default)

    var isGoingDown = true
    var isGoingLeft = false
    var isGoingRight = false
    var isGoingUp = false
    var isStopped = true
    var canUpdate = false
    var isUpdating = false

    init {
        _playerXY.postValue(XYIMpl(0f, 0f))
    }

    fun initPlatforms(
        platform1Y: Float,
        platform2Y: Float,
        platform3Y: Float,
        platform4Y: Float,
        platform5Y: Float,
        platform6Y: Float,
        maxX: Int,
        playerHeight: Int,
        playerWidth: Int
    ) {
        val newList = mutableListOf<Platform>()
        repeat(6) {
            newList.add(
                Platform(
                    (0 random maxX).toFloat(),
                    when (it + 1) {
                        1 -> platform1Y
                        2 -> platform2Y
                        3 -> platform3Y
                        4 -> platform4Y
                        5 -> platform5Y
                        else -> platform6Y
                    },
                    1 random 4,
                    (it + 1) % 2 == 0
                )
            )
        }
        newList.reverse()
        newList.first().isInitial = true
        newList.last().isNinja = true
        val player = _playerXY.value!!
        player.x = ((maxX - playerWidth) / 2).toFloat()
        player.y = newList.first().y - playerHeight
        _playerXY.postValue(player)
        _platforms.value = (newList)
        _trigger.value = !_trigger.value

    }

    fun stopAnother() {
        jumpScope.cancel()
    }

    fun removeLife() {
        _lives.postValue(_lives.value!! - 1)
    }

    fun addScores(score: Int) {
        _scores.postValue(_scores.value!! + score)
    }

    fun start(
        platformFragmentSize: Int,
        maxX: Int,
        platformHeight: Int,
        playerWidth: Int,
        playerHeight: Int,
        ninjaWidth: Int,
        ninjaHeight: Int
    ) {
        gameScope = CoroutineScope(Dispatchers.Default)

        letPlatformsMove(
            platformFragmentSize,
            maxX,
            platformHeight,
            playerWidth,
            playerHeight,
            ninjaWidth, ninjaHeight
        )
    }

    private fun letPlatformsMove(
        platformFragmentSize: Int,
        maxX: Int,
        platformHeight: Int,
        playerWidth: Int,
        playerHeight: Int,
        ninjaWidth: Int,
        ninjaHeight: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(16)
                if (!isUpdating) {
                    val currentList = _platforms.value.toMutableList()
                    val newList = mutableListOf<Platform>()
                    var shouldSet = true
                    var touchedNinja = false
                    if (currentList.find { it.isNinja } == null) {
                        currentList[currentList.indexOf(currentList.minBy { it.y })].isNinja = true
                    }
                    currentList.forEach { platform ->

                        val ninjaPlatform = currentList.find { it.isNinja } ?: currentList.minBy { it.y }

                        _enemyPos.postValue(
                            _enemyPos.value!!.copy(y = ninjaPlatform.y - ninjaHeight, x = ninjaPlatform.x + if (ninjaPlatform.isMovingLeft) -speed else speed),
                        )

                        val platformLength = when (platform.platformLength) {
                            1 -> platformFragmentSize * 2
                            2 -> platformFragmentSize * 3
                            3 -> platformFragmentSize * 4
                            else -> platformFragmentSize * 5
                        }

                        val platformX = platform.x.toInt()..(platform.x + platformLength).toInt()
                        val platformY =
                            platform.y.toInt()..(platform.y + platformHeight / 5).toInt()

                        val playerXY2 = _playerXY.value!!

                        val playerX = playerXY2.x.toInt()..(playerXY2.x + playerWidth).toInt()
                        val playerY =
                            (playerXY2.y.toInt() + playerHeight - playerHeight / 5)..(playerXY2.y + playerHeight).toInt()

                        if (platform.isMovingLeft && (platform.x + platformLength <= 0)) {
                            val x = maxX.toFloat()
                            if (playerX.any { it in platformX } && playerY.any { it in platformY } && isGoingDown) {
                                val distance = _playerXY.value!!.x - platform.x
                                _playerXY.postValue(XYIMpl(x + distance, _playerXY.value!!.y))
                            }
                            delay(2)
                            platform.x = x
                        } else if (!platform.isMovingLeft && (platform.x >= maxX)) {
                            val x = -platformLength.toFloat()
                            if (playerX.any { it in platformX } && playerY.any { it in platformY } && isGoingDown) {
                                val distance = _playerXY.value!!.x - platform.x
                                _playerXY.postValue(XYIMpl(x + distance, _playerXY.value!!.y))
                            }
                            delay(2)
                            platform.x = x
                        } else {
                            if (platform.isMovingLeft) {
                                platform.x = platform.x - speed
                            } else {
                                platform.x = platform.x + speed
                            }
                        }

                        if (platform.isInitial) {
                            platform.x = ((maxX - platformLength) / 2).toFloat()
                        }
                        ////////////

                        if (playerX.any { it in platformX } && playerY.any { it in platformY } && isGoingDown) {
                            jumpScope.cancel()
                            isStopped = true
                            val xy = _playerXY.value!!
                            if (!platform.isInitial) {
                                if (platform.isMovingLeft) xy.x = xy.x - speed else xy.x =
                                    xy.x + speed
                            }
                            if (platform.isNinja) {
                                platform.isNinja = false
                                touchedNinja = true
                            }
                            _playerXY.postValue(xy)
                            if (canUpdate) {
                                updateList(platform.y, maxX, playerHeight, touchedNinja)
                                shouldSet = false
                            }
                        }
                        ////////////
                        newList.add(platform)
                    }
                    if (shouldSet) {
                        _platforms.value = (newList)
                        _trigger.value = !_trigger.value
                    }
                }
            }
        }
    }

    private fun updateList(y: Float, maxX: Int, playerHeight: Int, touchedNinja: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            canUpdate = false
            isUpdating = true
            val currentList = _platforms.value.toMutableList()
            val listOfY = mutableListOf<Float>()
            currentList.forEach {
                listOfY.add(it.y)
            }
            val item = currentList.find { it.y == y }
            if (item != null) {
                var newList = mutableListOf<Platform>()
                val itemIndex = currentList.indexOf(item)
                repeat(itemIndex) {
                    currentList.removeAt(0)
                }
                newList = currentList

                if (6 - currentList.size != 0) {
                    _scores.postValue(_scores.value!! + 10 * (6 - currentList.size))
                }

                repeat(6 - currentList.size) {
                    newList.add(
                        Platform(
                            (0 random maxX).toFloat(),
                            0f,
                            1 random 4,
                            Random().nextBoolean()
                        )
                    )
                }
                newList.mapIndexed { index, value ->
                    value.y = listOfY[index]
                }
                if (touchedNinja) {
                    newList[newList.size - 1].isNinja = true

                    touchCallback?.invoke()
                }
                _platforms.value = (newList)
                _trigger.value = !_trigger.value
                _playerXY.postValue(
                    XYIMpl(
                        _playerXY.value!!.x,
                        listOfY[0] - playerHeight
                    )
                )
            }
            isUpdating = false
        }
    }

    fun respawnPlayer(platformFragmentSize: Int, playerWidth: Int, playerHeight: Int) {
        val currentList = _platforms.value
        val currentPlayer = _playerXY.value!!
        val platform = currentList.maxBy { it.y }
        val length = when (platform.platformLength) {
            1 -> platformFragmentSize * 2
            2 -> platformFragmentSize * 3
            3 -> platformFragmentSize * 4
            else -> platformFragmentSize * 5
        }

        _lives.postValue(_lives.value!! - 1)
        currentPlayer.y = platform.y - playerHeight
        currentPlayer.x = (platform.x + length - playerWidth) / 2
        _playerXY.postValue(currentPlayer)
    }

    fun jump() {
        jumpScope = CoroutineScope(Dispatchers.Default)
        isStopped = false
        isGoingUp = true
        isGoingDown = false
        canUpdate = true
        jumpScope.launch {
            repeat(10) { r ->
                repeat(5) {
                    delay(16)
                    val xy = _playerXY.value!!
                    xy.y = xy.y - (10 - r + 1)
                    if (isGoingLeft) {
                        xy.x = xy.x - 5
                    }
                    if (isGoingRight) {
                        xy.x = xy.x + 5
                    }
                    _playerXY.postValue(xy)
                }
            }

            isGoingDown = true

            repeat(10) { r ->
                repeat(5) {
                    delay(16)
                    val xy = _playerXY.value!!
                    xy.y = xy.y + (r + 1)
                    if (isGoingLeft) {
                        xy.x = xy.x - 5
                    }
                    if (isGoingRight) {
                        xy.x = xy.x + 5
                    }
                    _playerXY.postValue(xy)
                }
            }

            while (true) {
                delay(16)
                val xy = _playerXY.value!!
                xy.y = xy.y + (10)
                if (isGoingLeft) {
                    xy.x = xy.x - 5
                }
                if (isGoingRight) {
                    xy.x = xy.x + 5
                }
                _playerXY.postValue(xy)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stop()
        stopAnother()
    }
}