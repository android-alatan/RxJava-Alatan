package io.androidalatan.rx.scheduler.api.assertion

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.internal.schedulers.TrampolineScheduler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class MockSchedulerProviderTest {

    private val schedulerProvider = MockSchedulerProvider()

    @Test
    fun immediate() {
        schedulerProvider.immediate()
        Assertions.assertTrue(schedulerProvider.immediate)
    }

    @Test
    fun async() {
        schedulerProvider.async()
        Assertions.assertFalse(schedulerProvider.immediate)
    }

    @Test
    fun `computation async`() {
        schedulerProvider.async()
        Assertions.assertEquals(schedulerProvider.testScheduler, schedulerProvider.computation())
    }

    @Disabled("JUnit test doesn't support Looper.getMainLooper()")
    @Test
    fun `ui async`() {
        schedulerProvider.async()
        Assertions.assertEquals(schedulerProvider.testScheduler, schedulerProvider.ui())
    }

    @Test
    fun `io async`() {
        schedulerProvider.async()
        Assertions.assertEquals(schedulerProvider.testScheduler, schedulerProvider.io())
    }

    @Test
    fun `computation immediate`() {
        schedulerProvider.immediate()
        Assertions.assertEquals(
            TrampolineScheduler::class.java,
            schedulerProvider.computation().javaClass
        )
    }

    @Test
    fun `ui immediate`() {
        schedulerProvider.immediate()
        Assertions.assertEquals(TrampolineScheduler::class.java, schedulerProvider.ui().javaClass)
    }

    @Test
    fun `io immediate`() {
        schedulerProvider.immediate()
        Assertions.assertEquals(TrampolineScheduler::class.java, schedulerProvider.io().javaClass)
    }

    @Test
    fun advanceTimeBy() {
        schedulerProvider.advanceTimeBy(10, TimeUnit.SECONDS)
        Assertions.assertEquals(10, schedulerProvider.testScheduler.now(TimeUnit.SECONDS))

        schedulerProvider.advanceTimeBy(20, TimeUnit.SECONDS)
        Assertions.assertEquals(30, schedulerProvider.testScheduler.now(TimeUnit.SECONDS))
    }

    @Test
    fun advanceTimeTo() {
        schedulerProvider.advanceTimeTo(10, TimeUnit.SECONDS)
        Assertions.assertEquals(10, schedulerProvider.testScheduler.now(TimeUnit.SECONDS))

        schedulerProvider.advanceTimeTo(5, TimeUnit.SECONDS)
        Assertions.assertEquals(5, schedulerProvider.testScheduler.now(TimeUnit.SECONDS))
    }

    @Test
    fun triggerActions() {
        schedulerProvider.async()
        val testObserver = Observable.just(3)
            .delay(10, TimeUnit.SECONDS, schedulerProvider.computation())
            .test()

        schedulerProvider.triggerActions()
        testObserver.assertNoValues()

        schedulerProvider.advanceTimeBy(11, TimeUnit.SECONDS)
        testObserver.assertValueCount(1)
            .assertValue(3)
            .dispose()

    }
}