package com.example.a2048c2

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView


class Tile constructor(context: Context) : FrameLayout(context) {

    var num = 0
        set(num) {
            field = num
            label.text = if (num <= 0) "" else "$num"
            label.textSize = 32.toFloat()
            label.setTextColor(if (num >= 8) 0xfff9f6f2.toInt() else 0xff776e65.toInt())

            val background = GradientDrawable()
            background.cornerRadius = RADIUS

            when (num) {
                0 -> background.setColor(Color.parseColor("#ffcdc1b4"))
                2 -> background.setColor(Color.parseColor("#ffeee4da"))
                4 -> background.setColor(Color.parseColor("#ffede0c8"))
                8 -> background.setColor(Color.parseColor("#fff2b179"))
                16 -> background.setColor(Color.parseColor("#fff59563"))
                32 -> background.setColor(Color.parseColor("#fff67c5f"))
                64 -> background.setColor(Color.parseColor("#fff65e3b"))
                128 -> background.setColor(Color.parseColor("#ffedcf72"))
                256 -> background.setColor(Color.parseColor("#ffedcc61"))
                512 -> background.setColor(Color.parseColor("#ffedc850"))
                1024 -> background.setColor(Color.parseColor("#ffedc53f"))
                2048 -> background.setColor(Color.parseColor("#ffedc22e"))
            }
            label.background = background
        }
    private val label = TextView(getContext())

    init {
        label.gravity = Gravity.CENTER

        val lp = FrameLayout.LayoutParams(-1, -1)
        lp.setMargins(10, 10, 10, 10)
        addView(label, lp)
    }

    companion object {
        val RADIUS = 25f
    }

}
