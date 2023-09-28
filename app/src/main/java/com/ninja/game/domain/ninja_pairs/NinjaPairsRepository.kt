package com.ninja.game.domain.ninja_pairs

class NinjaPairsRepository {
    fun generateList(): List<NinjaPairsItem> {
        val listToReturn = mutableListOf<NinjaPairsItem>()

        repeat(2) {
            repeat(15) { ind ->
                listToReturn.add(NinjaPairsItem(value = ind + 1))
            }
        }

        listToReturn.shuffle()
        return listToReturn
    }
}