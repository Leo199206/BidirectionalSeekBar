package com.example.bidirectionalseekbar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.example.bidirectionalseekbar.databinding.ActivityMainBinding
import com.plant.seekbar.RangeSeekBar

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
        binding.seekbarWidget.setRange(30f, 180f)
        binding.seekbarWidget.setCurrentRange(60f, 80f)
        binding.seekbarWidget.setSeekBarChangeListener(object :
            RangeSeekBar.OnSeekBarChangeLister {
            override fun onRangeSeekBarDragEnd(
                view: RangeSeekBar,
                rangeMinValue: Float,
                rangeMaxValue: Float
            ) {
                super.onRangeSeekBarDragEnd(view, rangeMinValue, rangeMaxValue)
                binding.tvWidget.text =
                    "widget: ${rangeMinValue.toInt()}kg - ${rangeMaxValue.toInt()}kg"
            }
        })
    }

    /**
     * 年龄范围
     */
    private fun initAgeSeekBar() {
        binding.seekbarAge.setRange(18f, 80f)
        binding.seekbarAge.setCurrentRange(20f, 40f)
        binding.seekbarAge.setSeekBarChangeListener(object :
            RangeSeekBar.OnSeekBarChangeLister {
            override fun onRangeSeekBarDragEnd(
                view: RangeSeekBar,
                rangeMinValue: Float,
                rangeMaxValue: Float
            ) {
                super.onRangeSeekBarDragEnd(view, rangeMinValue, rangeMaxValue)
                binding.tvAge.text =
                    "widget: ${rangeMinValue.toInt()}岁 - ${rangeMaxValue.toInt()}岁"
            }
        })
    }


    /**
     * 身高范围
     */
    private fun initHeightSeekBar() {
        binding.seekbarHeight.setEnable(false)
        binding.seekbarAge.setRange(120f, 200f)
        binding.seekbarAge.setCurrentRange(170f, 180f)
        binding.seekbarAge.setSeekBarChangeListener(object :
            RangeSeekBar.OnSeekBarChangeLister {
            override fun onRangeSeekBarDragEnd(
                view: RangeSeekBar,
                rangeMinValue: Float,
                rangeMaxValue: Float
            ) {
                super.onRangeSeekBarDragEnd(view, rangeMinValue, rangeMaxValue)
                binding.tvHeight.text =
                    "height: ${rangeMinValue.toInt()}cm - ${rangeMaxValue.toInt()}cm"
            }

            override fun onDisableClick(view: RangeSeekBar) {
                super.onDisableClick(view)
                Toast.makeText(this@MainActivity, "SeekBar Disable", Toast.LENGTH_LONG).show()
            }
        })

    }

}