package io.androidalatan.rx.scheduler.api.assertion

import io.androidalatan.rx.scheduler.api.SchedulerProvider
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.schedulers.TestScheduler
import java.util.concurrent.TimeUnit

class MockSchedulerProvider : SchedulerProvider {

    internal val testScheduler = TestScheduler()

    internal var immediate = true

    fun immediate(): MockSchedulerProvider {
        immediate = true
        return this
    }

    fun async(): MockSchedulerProvider {
        immediate = false
        return this
    }

    override fun computation(): Scheduler {
        return if (immediate) {
            Schedulers.trampoline()
        } else {
            testScheduler
        }
    }

    override fun ui(): Scheduler {
        return if (immediate) {
            Schedulers.trampoline()
        } else {
            testScheduler
        }
    }

    override fun io(): Scheduler {
        return if (immediate) {
            Schedulers.trampoline()
        } else {
            testScheduler
        }
    }

    fun getTestScheduler() = testScheduler

    fun advanceTimeBy(delayTime: Long, unit: TimeUnit): MockSchedulerProvider {
        testScheduler.advanceTimeBy(delayTime, unit)
        return this
    }

    fun advanceTimeTo(delayTime: Long, unit: TimeUnit): MockSchedulerProvider {
        testScheduler.advanceTimeTo(delayTime, unit)
        return this
    }

    fun triggerActions(): MockSchedulerProvider {
        testScheduler.triggerActions()
        return this
    }
}