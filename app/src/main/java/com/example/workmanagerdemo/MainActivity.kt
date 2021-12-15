package com.example.workmanagerdemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import androidx.work.WorkInfo.*
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


            /**
             * A specification of the requirements that need to be met before a WorkRequest can run.  By
             * default, WorkRequests do not have any requirements and can run immediately.  By adding
             * requirements, you can make sure that work only runs in certain situations - for example, when you
             * have an unmetered network and are charging.
             */
            val oneTimeRequestConstraints = Constraints.Builder()

                // 1. Device is NOT Charging
                .setRequiresCharging(false)

                // 2. Internet is Connected
                .setRequiredNetworkType(NetworkType.CONNECTED)

                // Due to our two defined constraints, this request will only work if those requirements are met.
                .build()

            // Define the input data for work manager
            // A persistable set of key/value pairs which are used as inputs and outputs for ListenableWorkers.
            val data = Data.Builder()
            data.putString("inputKey", "Input Value")

            // Create an one time work request
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
                 * @param inputData key/value pairs that will be provided to the worker
                 */
                .setInputData(data.build())
                /**
                 * Adds constraints to the WorkRequest.
                 */
                .setConstraints(oneTimeRequestConstraints)
                // Builds a {@link WorkRequest} based on this {@link Builder}
                .build()

            // Retrieves the default singleton instance of WorkManager and Enqueues one item for background processing.
            WorkManager.getInstance(this@MainActivity).enqueue((sampleWork))

            // Gets a LiveData of the WorkInfo for a given work id.
            WorkManager.getInstance(this@MainActivity).getWorkInfoByIdLiveData(sampleWork.id)
                .observe(this, Observer { workInfo ->
                    OneTimeRequestWorker.Companion.logger(workInfo.state.name)
                    if (workInfo != null) {
                        when (workInfo.state) {
                            State.ENQUEUED -> {
                                // Show the work state in text view
                                binding.tvOneTimeRequest.text = "Task enqueued."
                            }
                            State.BLOCKED -> {
                                binding.tvOneTimeRequest.text = "Task blocked."
                            }
                            State.RUNNING -> {
                                binding.tvOneTimeRequest.text = "Task running."
                            }
                            else -> {
                                binding.tvOneTimeRequest.text = "Task state else part."
                            }
                        }
                    }

                    // When work finished
                    if (workInfo != null && workInfo.state.isFinished) {
                        when (workInfo.state) {
                            State.SUCCEEDED -> {
                                binding.tvOneTimeRequest.text = "Task successful."

                                // Get the output data
                                val successOutputData = workInfo.outputData
                                val outputText = successOutputData.getString("outputKey")
                                Log.i("Worker Output", "$outputText")
                            }
                            State.FAILED -> {
                                binding.tvOneTimeRequest.text = "Task Failed."
                            }
                            State.CANCELLED -> {
                                binding.tvOneTimeRequest.text = "Task cancelled."
                            }
                            else -> {
                                binding.tvOneTimeRequest.text = "Task state isFinished else part."
                            }
                        }
                    }
                })
        }

    }

}