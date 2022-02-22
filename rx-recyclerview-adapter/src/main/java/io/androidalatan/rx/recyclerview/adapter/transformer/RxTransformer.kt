package io.androidalatan.rx.recyclerview.adapter.transformer

import androidx.recyclerview.widget.DiffUtil
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableSource
import io.reactivex.rxjava3.core.CompletableTransformer
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableTransformer
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeSource
import io.reactivex.rxjava3.core.MaybeTransformer
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleSource
import io.reactivex.rxjava3.core.SingleTransformer
import org.reactivestreams.Publisher

class RxTransformer<DATA>(private val dataStore: DataStore<DATA>) : ObservableTransformer<List<DATA>, List<DATA>>,
    SingleTransformer<List<DATA>, List<DATA>>,
    MaybeTransformer<List<DATA>, List<DATA>>,
    CompletableTransformer,
    FlowableTransformer<List<DATA>, List<DATA>> {

    override fun apply(upstream: Observable<List<DATA>>): ObservableSource<List<DATA>> {
        return upstream.distinctUntilChanged()
            .concatMap { newList -> observeCalculateDiff(newList, dataStore.getData(), dataStore).toObservable() }
    }

    override fun apply(upstream: Single<List<DATA>>): SingleSource<List<DATA>> = upstream
        .concatMap { newList -> observeCalculateDiff(newList, dataStore.getData(), dataStore) }

    override fun apply(upstream: Maybe<List<DATA>>): MaybeSource<List<DATA>> = upstream
        .concatMap { newList -> observeCalculateDiff(newList, dataStore.getData(), dataStore).toMaybe() }

    override fun apply(upstream: Completable): CompletableSource =
        Completable.error(UnsupportedOperationException("DiffUtilAdapter doesn't support Completable"))

    override fun apply(upstream: Flowable<List<DATA>>): Publisher<List<DATA>> = upstream.distinctUntilChanged()
        .switchMap { newList -> observeCalculateDiff(newList, dataStore.getData(), dataStore).toFlowable() }

    private fun observeCalculateDiff(newList: List<DATA>, oldList: List<DATA>, dataStore: DataStore<DATA>) =
        if (oldList.isEmpty()) {
            Single.just(newList)
                .doOnSuccess { dataStore.setData(newList) }
        } else {
            Single.fromCallable { calculateDiff(newList, oldList) }
                .doOnSuccess { callback -> dataStore.setData(callback, newList) }
                .map { newList }
        }

    private fun calculateDiff(newList: List<DATA>, oldList: List<DATA>): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = oldList.size

            override fun getNewListSize() = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldList[oldItemPosition] == newList[newItemPosition]

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldList[oldItemPosition] == newList[newItemPosition]
        })
    }

    interface DataStore<DATA> {
        fun getData(): List<DATA>
        fun setData(newList: List<DATA>)
        fun setData(callback: DiffUtil.DiffResult, newList: List<DATA>)
    }
}