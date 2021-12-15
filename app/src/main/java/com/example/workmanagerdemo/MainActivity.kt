package com.example.workmanagerdemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import androidx.work.WorkInfo.*
import com.example.workmanagerdemo.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO - We will put the code in normal projects, which we want to execute, i.e. downloading...

        // Event: One Time Request
        binding.btnOneTimeRequest.setOnClickListener {

            /**
             * Note:
             * By default, WorkManager does not has any of these constraints.
             *
             * SetRequires ****** (Examples of Constraints)
             * ======================
             * 1. Network Type
             * 2. Batter Not Low
             * 3. Charging
             * 4. Device Idle
             * 5. Storage Not Low
             *
             */


            // -------------------------------- CONFIGURATION --------------------------------


            /**
             * A specification of the requirements that need to be met before a WorkRequest can run.  By
             * default, WorkRequests do not have any requirements and can run immediately.  By adding
             * requirements, you can make sure that work only runs in certain situations - for example, when you
             * have an un-metered network and are charging.
             */
            val oneTimeRequestConstraints = Constraints.Builder()

                // 1. Device is NOT Charging
                .setRequiresCharging(false)

                // 2. Internet is Connected
                .setRequiredNetworkType(NetworkType.CONNECTED)

                // 3. Verify if the Batter is not on the low level (Must be > 20%)
                // Lower then < 20% - Task will be enqueued
                // Greater then > 20% - Task will be successful
                .setRequiresBatteryNotLow(true)

                // Due to our two defined constraints, this request will only work if those requirements are met.
                .build()


            // Define the input data for work manager
            // A persistable set of key/value pairs which are used as inputs and outputs for ListenableWorkers.
            val data = Data.Builder()
            data.putString("inputKey", "Input Value")


            // -------------------------------- ONE TIME REQUEST --------------------------------


            /**
             * A WorkRequest for non-repeating work.
             * OneTimeWorkRequests can be put in simple or complex graphs of work by using methods.
             */
            val sampleWork = OneTimeWorkRequest
                .Builder(OneTimeRequestWorker::class.java)
                /**
                 * Adds input Data to the work.  If a worker has prerequisites in its chain, this
                 * Data will be merged with the outputs of the prerequisites using an InputMerger.
                 *
                 * @param - key/value pairs that will be provided to the worker
                 */
                .setInputData(data.build())

                // Adds constraints to the WorkRequest.
                .setConstraints(oneTimeRequestConstraints)

                // Builds a {@link WorkRequest} based on this {@link Builder}
                .build()


            // ------

            // Enqueue for background Processing.
            // Retrieves the default singleton instance of WorkManager and Enqueues one item for background processing.
            WorkManager.getInstance(this@MainActivity).enqueue((sampleWork))

            // Gets a LiveData of the WorkInfo for a given work id.
            // sampleWork.id = Gets the unique identifier associated with this unit of work.
            WorkManager.getInstance(this@MainActivity).getWorkInfoByIdLiveData(sampleWork.id)
                .observe(this, { workInfo ->

                    // It will execute the companion object "logger" in OneTimeRequestWorker.kt file
                    OneTimeRequestWorker.Companion.logger(workInfo.state.name)

                    // If the workInfo is not null:
                    if (workInfo != null) {
                        when (workInfo.state) {

                            // Enqueued
                            State.ENQUEUED -> {
                                // Show the work state in text view
                                binding.tvOneTimeRequest.text = getString(R.string.task_enqueued)
                            }

                            // Blocked
                            State.BLOCKED -> {
                                binding.tvOneTimeRequest.text = getString(R.string.task_blocked)
                            }

                            // Running
                            State.RUNNING -> {
                                binding.tvOneTimeRequest.text = getString(R.string.task_running)
                            }

                            // Else:
                            else -> {
                                binding.tvOneTimeRequest.text = getString(R.string.task_default)
                            }

                        }
                    }

                    // When the WorkInfo is not null and work is finished:
                    if (workInfo != null && workInfo.state.isFinished) {

                        // Conditional:
                        when (workInfo.state) {

                            // Succeeded
                            State.SUCCEEDED -> {
                                binding.tvOneTimeRequest.text = getString(R.string.task_succeeded)

                                // Get the output data
                                val successOutputData = workInfo.outputData
                                val outputText = successOutputData.getString("outputKey")
                                Log.i("Worker Output", "$outputText")

                            }

                            // Failed
                            State.FAILED -> {
                                binding.tvOneTimeRequest.text = getString(R.string.task_failed)
                            }

                            // Canceled
                            State.CANCELLED -> {
                                binding.tvOneTimeRequest.text = getString(R.string.task_cancelled)
                            }

                            // Default:
                            else -> {
                                binding.tvOneTimeRequest.text =
                                    getString(R.string.task_default_finished)
                            }

                        }
                    }
                })
        }

        // -------------------------------- PERIODIC WORK Request --------------------------------
        /**
         * Periodic Work Request
         */
        binding.btnPeriodicRequest.setOnClickListener {

            /**
             * Constraints ensure that work is deferred until optimal conditions are met.
             *
             * A specification of the requirements that need to be met before a WorkRequest can run.
             * By default, WorkRequests do not have any requirements and can run immediately.
             * By adding requirements, you can make sure that work only runs in certain situations
             * - for example, when you have an un-metered network and are charging.
             */
            // For more details visit the link https://medium.com/androiddevelopers/introducing-workmanager-2083bcfc4712
            val periodicRequestConstraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            // Create an Periodic Work Request
            /**
             * You can use any of the work request builder that are available to use.
             * We will you the PeriodicWorkRequestBuilder as we want to execute the code periodically.
             *
             * The minimum time you can set is 15 minutes. You can check the same on the below link.
             * https://developer.android.com/reference/androidx/work/PeriodicWorkRequest
             *
             * You can also set the TimeUnit as per your requirement. for example SECONDS, MINUTES, or HOURS.
             */
            // setting period to >>>> 15 Minutes
            val periodicWorkRequest =
                PeriodicWorkRequest.Builder(PeriodicRequestWorker::class.java, 15, TimeUnit.MINUTES)
                    .setConstraints(periodicRequestConstraints)
                    .build()

            /* Enqueue a work, ExistingPeriodicWorkPolicy.KEEP means that if this work already exists, it will be kept
        if the value is ExistingPeriodicWorkPolicy.REPLACE, then the work will be replaced */
            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "Periodic Work Request",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )

        }
    }

}