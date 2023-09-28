package com.ninja.game.ui.ninja_pairs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ninja.game.domain.ninja_pairs.NinjaPairsItem
import com.ninja.game.domain.ninja_pairs.NinjaPairsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NinjaPairsViewModel: ViewModel() {
        private val repository = NinjaPairsRepository()
        private var gameScope = CoroutineScope(Dispatchers.Default)
        var gameState = true
        var winCallback: (() -> Unit)? = null
        var pauseState = false
        private val _list = MutableLiveData(
            repository.generateList()
        )
        val list: LiveData<List<NinjaPairsItem>> = _list


        private val _timer = MutableLiveData(60)
        val timer: LiveData<Int> = _timer


        fun startTimer() {
            gameScope = CoroutineScope(Dispatchers.Default)
            gameScope.launch {
                while (true) {
                    delay(1000)
                    _timer.postValue(_timer.value!! - 1)
                }
            }
        }

        fun stopTimer() {
            gameScope.cancel()
        }

        fun openItem(index: Int) {
            viewModelScope.launch {
                val newList = _list.value!!
                newList[index].openAnimation = true
                _list.postValue(newList)
                delay(410)
                newList[index].openAnimation = false
                newList[index].isOpened = true
                newList[index].lastOpened = true
                val filteredList = newList.filter { it.lastOpened }
                if (filteredList.size == 2) {
                    val item1 = filteredList[0]
                    val item2 = filteredList[1]
                    if (item1.value == item2.value) {
                        newList.map {
                            it.lastOpened = false
                        }
                        _list.postValue(newList)
                    } else {

                        newList[newList.indexOf(item1)].closeAnimation = true
                        newList[newList.indexOf(item1)].lastOpened = false

                        newList[newList.indexOf(item2)].closeAnimation = true
                        newList[newList.indexOf(item2)].lastOpened = false

                        _list.postValue(newList)
                        delay(410)
                        newList[newList.indexOf(item1)].closeAnimation = false
                        newList[newList.indexOf(item1)].isOpened = false

                        newList[newList.indexOf(item2)].closeAnimation = false
                        newList[newList.indexOf(item2)].isOpened = false

                        _list.postValue(newList)
                    }
                }
            }
        }

        override fun onCleared() {
            super.onCleared()
            gameScope.cancel()
        }
    }
