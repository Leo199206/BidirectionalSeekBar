package com.example.bidirectionalseekbar

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.bidirectionalseekbar.databinding.ActivityMainBinding
import com.plant.bidirectionalseekbar.BidirectionalSeekBar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        initAgeSeekBar()
        initHeightSeekBar()
        initWidgetSeekBar()
    }

    /**
     * 体重范围
     */
    private fun initWidgetSeekBar() {
        binding.seekbarWidget.init(30f, 180f, 60f, 80f, object :
            BidirectionalSeekBar.OnSeekBarChangeLister {

            override fun onSeekBarChange(
                view: BidirectionalSeekBar,
                startProgress: Float,
                endProgress: Float,
                startValue: Float,
                endValue: Float
            ) {
                binding.tvWidget.text = "widget: ${startValue.toInt()}kg - ${endValue.toInt()}kg"
            }

            override fun onUnEnable(view: BidirectionalSeekBar) {
            }
        })
    }

    /**
     * 年龄范围
     */
    private fun initAgeSeekBar() {
        binding.seekbarAge.init(18f, 80f, 20f, 40f, object :
            BidirectionalSeekBar.OnSeekBarChangeLister {

            override fun onSeekBarChange(
                view: BidirectionalSeekBar,
                startProgress: Float,
                endProgress: Float,
                startValue: Float,
                endValue: Float
            ) {
                binding.tvAge.text = "age: ${startValue.toInt()}岁 - ${endValue.toInt()}岁"
            }

            override fun onUnEnable(view: BidirectionalSeekBar) {
            }
        })
    }


    /**
     * 年龄范围
     */
    private fun initHeightSeekBar() {
        binding.seekbarHeight.init(120f, 200f, 170f, 180f, object :
            BidirectionalSeekBar.OnSeekBarChangeLister {

            override fun onSeekBarChange(
                view: BidirectionalSeekBar,
                startProgress: Float,
                endProgress: Float,
                startValue: Float,
                endValue: Float
            ) {
                binding.tvHeight.text = "height: ${startValue.toInt()}cm - ${endValue.toInt()}cm"
            }

            override fun onUnEnable(view: BidirectionalSeekBar) {
            }
        })
    }

}