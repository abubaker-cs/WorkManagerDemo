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

    /**
     * Method to create output data
     */
    private fun createOutputData(): Data {

        // Return actual data
        return Data.Builder()
            .putString("outputKey", "Output Value")
            .build()

        // TODO: In normal projects, notify the user that the task is completed or update the UI.

    }

    /**
     * Companion Object
     */
    object Companion {

        // We want to get the status of our WorkManager form that logger.
        fun logger(message: String) = Log.i("WorkRequest Status", message)

    }

}