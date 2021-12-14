package com.example.workmanagerdemo

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class OneTimeRequestWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        TODO("Not yet implemented")
    }

}