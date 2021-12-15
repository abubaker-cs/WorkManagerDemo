package com.example.workmanagerdemo

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class OneTimeRequestWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {

        // Get the input data
        val inputValue = inputData.getString("inputKey")
        Log.i("Worker Input", "$inputValue")

        // You can add any code that you want to use as one time request. For example downloading the image from the url.

        return Result.success(createOutputData())
    }

    // Method to create output data
    private fun createOutputData(): Data {
        return Data.Builder()
            .putString("outputKey", "Output Value")
            .build()
    }

    object Companion {
        fun logger(message: String) =
            Log.i("WorkRequest Status", message)
    }
}