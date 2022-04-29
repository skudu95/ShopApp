package com.kudu.shopapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kudu.shopapp.databinding.ActivityMyOrderDetailsBinding

class MyOrderDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyOrderDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}