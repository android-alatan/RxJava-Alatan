package io.androidalatan.rx.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.VisibleForTesting
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.androidalatan.rx.recyclerview.adapter.transformer.RxTransformer

abstract class DiffUtilAdapter<DATA>(private val autoUpdate: Boolean = true) : RecyclerView.Adapter<DataBindingViewHolder<DATA>>(),
    RxTransformer.DataStore<DATA> {

    @VisibleForTesting
    internal var list: List<DATA> = emptyList()

    @VisibleForTesting
    internal val rxTransformer by lazy {
        RxTransformer(this)
    }

    override fun getData() = list

    override fun setData(newList: List<DATA>) {
        this.list = newList
        notifyDataSetChangedForAdapter()
    }

    fun notifyDataSetChangedForAdapter() {
        if (autoUpdate) {
            notifyDataSetChanged()
        }
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        if (autoUpdate) {
            super.setHasStableIds(hasStableIds)
        }
    }

    open fun bindViewModel(viewDataBinding: ViewDataBinding, itemViewType: Int, data: DATA) {

    }

    override fun onBindViewHolder(holder: DataBindingViewHolder<DATA>, position: Int) {
        val data = getData()[position]
        bindViewModel(holder.viewDataBinding, holder.itemViewType, data)
        holder.setData(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<DATA> {
        val viewDataBinding =
            DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), getLayoutIdByViewType(viewType), parent, false)
        return newViewHolder(viewDataBinding, viewType)
    }

    open fun newViewHolder(viewDataBinding: ViewDataBinding, viewType: Int): DataBindingViewHolder<DATA> {
        return DataBindingViewHolder(viewDataBinding)
    }

    @LayoutRes
    abstract fun getLayoutIdByViewType(viewType: Int): Int

    override fun setData(callback: DiffUtil.DiffResult, newList: List<DATA>) {
        this.list = newList
        if (autoUpdate) {
            callback.dispatchUpdatesTo(this)
        }
    }

    fun asRxTransformer() = this.rxTransformer

    override fun getItemCount() = getData().size
}