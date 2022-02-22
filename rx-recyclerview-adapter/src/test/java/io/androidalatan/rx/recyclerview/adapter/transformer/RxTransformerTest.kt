package io.androidalatan.rx.recyclerview.adapter.transformer

import androidx.recyclerview.widget.DiffUtil
import io.androidalatan.rx.recyclerview.adapter.Person
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RxTransformerTest {

    private val person1 = Person("Hello")
    private val person2 = Person("World")
    private val person3 = Person("~!")

    private val oldData = listOf(person1, person2)
    private val newData = listOf(person1, person2, person3)

    private var getDataCount = 0
    private var setDataCountWithCallback = 0
    private var setDataCountWithoutCallback = 0
    private var setDataValue = emptyList<Person>()

    private val transformer =
        RxTransformer(object :
            RxTransformer.DataStore<Person> {
            override fun getData(): List<Person> {
                getDataCount++
                return setDataValue
            }

            override fun setData(callback: DiffUtil.DiffResult, newList: List<Person>) {
                setDataCountWithCallback++
                setDataValue = newList
            }

            override fun setData(newList: List<Person>) {
                setDataCountWithoutCallback++
                setDataValue = newList
            }
        })

    private val subject = PublishSubject.create<List<Person>>()

    @AfterEach
    fun tearDown() {
        getDataCount = 0
        setDataCountWithCallback = 0
        setDataCountWithoutCallback = 0
        setDataValue = oldData
    }

    @Test
    fun `apply Observer`() {
        val testObserver = subject
            .compose(transformer)
            .test()
            .assertNoValues()
            .assertNotComplete()
            .assertNoErrors()

        subject.onNext(oldData)
        testObserver.assertValueCount(1)
            .assertValue(oldData)
            .assertNotComplete()
            .assertNoErrors()

        Assertions.assertEquals(1, setDataCountWithoutCallback)
        Assertions.assertEquals(oldData, setDataValue)

        subject.onNext(oldData)
        testObserver.assertValueCount(1)

        subject.onNext(newData)
        testObserver.assertValueCount(2)
            .assertValueAt(1, newData)
            .assertNoErrors()
            .assertNotComplete()
            .dispose()

        Assertions.assertEquals(1, setDataCountWithCallback)
        Assertions.assertEquals(newData, setDataValue)

    }

    @Test
    fun `apply Single`() {
        Single.just(oldData)
            .compose(transformer)
            .test()
            .assertValueCount(1)
            .assertValue(oldData)
            .assertComplete()
            .assertNoErrors()
            .dispose()

        Assertions.assertEquals(1, setDataCountWithoutCallback)
        Assertions.assertEquals(oldData, setDataValue)

        Single.just(newData)
            .compose(transformer)
            .test()
            .assertValueCount(1)
            .assertValue(newData)
            .assertComplete()
            .assertNoErrors()
            .dispose()
        Assertions.assertEquals(1, setDataCountWithCallback)
        Assertions.assertEquals(newData, setDataValue)

    }

    @Test
    fun `apply Maybe`() {
        Maybe.just(oldData)
            .compose(transformer)
            .test()
            .assertValueCount(1)
            .assertValue(oldData)
            .assertComplete()
            .assertNoErrors()
            .dispose()

        Assertions.assertEquals(1, setDataCountWithoutCallback)
        Assertions.assertEquals(oldData, setDataValue)

        Maybe.just(newData)
            .compose(transformer)
            .test()
            .assertValueCount(1)
            .assertValue(newData)
            .assertComplete()
            .assertNoErrors()
            .dispose()

        Assertions.assertEquals(1, setDataCountWithCallback)
        Assertions.assertEquals(newData, setDataValue)

    }

    @Test
    fun `apply Completable`() {
        Completable.complete()
            .compose(transformer)
            .test()
            .assertNoValues()
            .assertError(UnsupportedOperationException::class.java)
            .dispose()

        Assertions.assertEquals(0, setDataCountWithCallback)
        Assertions.assertEquals(0, setDataCountWithoutCallback)
        Assertions.assertEquals(emptyList<Person>(), setDataValue)

    }

    @Test
    fun `apply Flowable`() {
        val testObserver = subject.toFlowable(BackpressureStrategy.BUFFER)
            .compose(transformer)
            .test()
            .assertNoValues()
            .assertNotComplete()
            .assertNoErrors()

        subject.onNext(oldData)
        testObserver.assertValueCount(1)
            .assertValue(oldData)
            .assertNotComplete()
            .assertNoErrors()

        Assertions.assertEquals(1, setDataCountWithoutCallback)
        Assertions.assertEquals(oldData, setDataValue)

        subject.onNext(oldData)
        testObserver.assertValueCount(1)
            .assertNoErrors()
            .assertNotComplete()

        subject.onNext(newData)
        testObserver.assertValueCount(2)
            .assertValueAt(1, newData)
            .assertNoErrors()
            .assertNotComplete()
            .cancel()

        Assertions.assertEquals(1, setDataCountWithCallback)
        Assertions.assertEquals(newData, setDataValue)

    }
}