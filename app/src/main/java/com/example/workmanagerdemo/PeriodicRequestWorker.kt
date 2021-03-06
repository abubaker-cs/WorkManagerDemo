package com.example.workmanagerdemo

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.*

// Create a worker class for Periodic Work Request.
class PeriodicRequestWorker(context: Context, params: WorkerParameters) : Worker(context, params) {


    override fun doWork(): Result {

        // Get the current date in milliseconds.
        val date = getDate(System.currentTimeMillis())

        // Print the date in log when the function is called.
        Log.i("Periodic WorkRequest", "doWork Execution DateTime: $date")

        // Return result, without passing anything as it is optional as we recently did in the OneTimeRequestWorker.kt
        return Result.success()

    }

    /**
     * A function to get the date from the Milliseconds.
     *
     * @param milliSeconds
     */
    private fun getDate(milliSeconds: Long): String {

        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS", Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()

        //
        calendar.timeInMillis = milliSeconds

        //
        return formatter.format(calendar.time)

    }

}