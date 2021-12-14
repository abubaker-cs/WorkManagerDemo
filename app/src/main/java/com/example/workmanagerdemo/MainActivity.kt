package com.example.workmanagerdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.NetworkType
import com.example.workmanagerdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Event: One Time Request
        binding.btnOneTimeRequest.setOnClickListener {

            /**
             * SetRequires ****** (Examples)
             * ======================
             * 1. Network Type
             * 2. Batter Not Low
             * 3. Charging
             * 4. Device Idle
             * 5. Storage Not Low
             *
             */

            val oneTimeRequestConstraints = Constraints.Builder()

                // 1. Device is NOT Charging
                .setRequiresCharging(false)

                // 2. Internet is Connected
                .setRequiredNetworkType(NetworkType.CONNECTED)

                // Due to our two defined constraints, this request will only work if those requirements are met.
                .build()

        }

    }

}