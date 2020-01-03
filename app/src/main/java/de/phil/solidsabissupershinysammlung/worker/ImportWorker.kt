package de.phil.solidsabissupershinysammlung.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ImportWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    override fun doWork(): Result {



        return Result.success()
    }

}