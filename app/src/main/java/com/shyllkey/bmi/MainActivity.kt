package com.shyllkey.bmi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shyllkey.circular_indicator.Indicator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gauge1.setPositionValue(0)
        button.setOnClickListener {
            gauge1.setPositionValue(180)
        }




    }
}