package io.androidalatan.rx.scheduler.api

import android.os.Looper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

interface SchedulerProvider {
    fun computation(): Scheduler
    fun ui(): Scheduler
    fun io(): Scheduler

    companion object {
        // TODO change to Coroutine
        // https://github.com/Kotlin/kotlinx.coroutines/pull/1923
        operator fun invoke(uiAsync: Boolean = true): SchedulerProvider {
            return object : SchedulerProvider {
                override fun computation(): Scheduler {
                    return Schedulers.computation()
                }

                override fun ui(): Scheduler {
                    return AndroidSchedulers.from(Looper.getMainLooper(), uiAsync)
                }

                override fun io(): Scheduler {
                    return Schedulers.io()
                }
            }
        }
    }
}