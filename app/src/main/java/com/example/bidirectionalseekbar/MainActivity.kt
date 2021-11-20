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

        binding.seekBar.setValue(30f, 130f, 60f, 80f)
        binding.seekBar.setEnable(true)
        binding.seekBar.setSeekBarChangeListener(object :
            BidirectionalSeekBar.OnSeekBarChangeLister {

            override fun onSeekBarChange(
                view: BidirectionalSeekBar,
                startProgress: Float,
                endProgress: Float,
                startValue: Float,
                endValue: Float
            ) {
            }

            override fun onUnEnable(view: BidirectionalSeekBar) {
            }
        })
    }

}