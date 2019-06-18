package com.example.a2048c2

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.GridLayout

class GameView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : GridLayout(context, attrs, defStyleAttr) {

    companion object {
        val BOARD_SIZE = 4
    }

    private lateinit var cardsMap: Array<Array<Tile>>
    private fun Array<Array<Tile>>.deepForEach(action: (Tile) -> Unit) = this.forEach { it.forEach { action(it) } }

    init {
        setBackgroundColor(Color.parseColor(Utils.LIGHT_SCHEME_COLOR))

        post {
            removeAllViews()
            columnCount = BOARD_SIZE
            cardsMap = Array(BOARD_SIZE) { Array(BOARD_SIZE) { Tile(context) } }

            cardsMap.deepForEach { addView(it, width / BOARD_SIZE, width / BOARD_SIZE) }
            startGame()
        }
    }

    fun startGame() {
        MainActivity.mainActivity!!.score = 0
        cardsMap.deepForEach { it.num = 0 }
        addRandomCard()
        addRandomCard()
    }

    private fun addRandomCard() {
        val emptyCards = mutableListOf<Tile>()
        cardsMap.forEach { emptyCards += it.filter { it.num <= 0 } }
        emptyCards[(Math.random() * emptyCards.size).toInt()].num = 2
        checkEnd()
    }

    private fun checkEnd() {
        for (x in 0 until BOARD_SIZE) {
            for (y in 0 until BOARD_SIZE) {
                val num = cardsMap[x][y].num
                if (num == 1024) {
                    AlertDialog.Builder(context).setTitle("Congratulations").setMessage("You won").setPositiveButton("Play again") { _, _ -> startGame() }.show()
                    return
                }
                if (num <= 0 ||
                        x > 0 && num == cardsMap[x - 1][y].num ||
                        x < BOARD_SIZE - 1 && num == cardsMap[x + 1][y].num ||
                        y > 0 && num == cardsMap[x][y - 1].num ||
                        y < BOARD_SIZE - 1 && num == cardsMap[x][y + 1].num) {
                    return
                }
            }
        }

        AlertDialog.Builder(context).setTitle("Game over").setMessage("You lost").setPositiveButton("Play again") { _, _ -> startGame() }.show()
    }

    fun swipeLeft() {
        println("swipeLeft()")
        var move = false
        for (x in 0 until BOARD_SIZE) {
            var y = 0
            while (y < BOARD_SIZE) {
                for (y1 in y + 1 until BOARD_SIZE) {
                    if (cardsMap[x][y1].num > 0) {

                        val check = checkSwitch(cardsMap[x][y], cardsMap[x][y1])
                        move = move || check == 1 || check == 2
                        y -= if (check == 1) 1 else 0

                        break
                    }
                }
                y++

            }
        }
        if (move) addRandomCard()
    }


    fun swipeRight() {
        println("swipeRight()")
        var move = false
        for (x in 0 until BOARD_SIZE) {
            var y = BOARD_SIZE - 1
            while (y >= 0) {

                for (y1 in y - 1 downTo 0) {
                    if (cardsMap[x][y1].num > 0) {

                        val check = checkSwitch(cardsMap[x][y], cardsMap[x][y1])
                        move = move || check == 1 || check == 2
                        y += if (check == 1) 1 else 0

                        break
                    }
                }
                y--

            }
        }
        if (move) addRandomCard()
    }


    fun swipeUp() {
        println("swipeUp()")
        var move = false
        for (y in 0 until BOARD_SIZE) {
            var x = 0
            while (x < BOARD_SIZE) {

                for (x1 in x + 1 until BOARD_SIZE) {
                    if (cardsMap[x1][y].num > 0) {

                        val check = checkSwitch(cardsMap[x][y], cardsMap[x1][y])
                        move = move || check == 1 || check == 2
                        x -= if (check == 1) 1 else 0

                        break
                    }
                }
                x++
            }
        }

        if (move) addRandomCard()
    }

    fun swipeDown() {
        println("swipeDown()")
        var move = false
        for (y in 0 until BOARD_SIZE) {
            var x = BOARD_SIZE - 1
            while (x >= 0) {

                for (x1 in x - 1 downTo 0) {
                    if (cardsMap[x1][y].num > 0) {

                        val check = checkSwitch(cardsMap[x][y], cardsMap[x1][y])
                        move = move || check == 1 || check == 2
                        x += if (check == 1) 1 else 0
                        break
                    }
                }
                x--

            }
        }
        if (move) addRandomCard()
    }


    private fun checkSwitch(tile1: Tile, tile2: Tile): Int {
        val num1 = tile1.num
        val num2 = tile2.num

        if (num1 <= 0) {
            tile1.num = num2
            tile2.num = 0

            return 1
        } else if (num1 == num2) {
            tile1.num = num1 * 2
            tile2.num = 0
            MainActivity.mainActivity!!.score += num1 * 2

            return 2
        }
        return 0
    }
}


