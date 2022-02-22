package io.androidalatan.rx.scheduler.api

import android.os.Build
import io.reactivex.rxjava3.internal.schedulers.ComputationScheduler
import io.reactivex.rxjava3.internal.schedulers.IoScheduler
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O])
@RunWith(RobolectricTestRunner::class)
class SchedulerProviderTest {

    private val schedulerProvider = SchedulerProvider(true)

    @Test
    fun computation() {
        Assertions.assertEquals(
            ComputationScheduler::class.java,
            schedulerProvider.computation().javaClass
        )
    }

    @Test
    fun io() {
        Assertions.assertEquals(IoScheduler::class.java, schedulerProvider.io().javaClass)
    }

    @Test
    fun ui() {
        Assertions.assertEquals("HandlerScheduler", schedulerProvider.ui().javaClass.simpleName)
    }
}