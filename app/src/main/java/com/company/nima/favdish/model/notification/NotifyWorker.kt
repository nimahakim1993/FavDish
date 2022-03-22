package com.company.nima.favdish.model.notification

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotifyWorker(context: Context, params: WorkerParameters): Worker(context, params){


    override fun doWork(): Result {
        Log.i("log_doWorker", "doWork function is called...")
        return Result.success()
    }
}