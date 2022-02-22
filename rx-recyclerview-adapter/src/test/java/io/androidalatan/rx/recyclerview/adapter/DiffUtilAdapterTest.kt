package io.androidalatan.rx.recyclerview.adapter

import androidx.databinding.ViewDataBinding
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class DiffUtilAdapterTest {

    private val adapter = MockDiffUtilAdapter()

    @Test
    fun `getData, setData and getItemCount`() {
        assertEquals(0, adapter.getData().size)
        assertEquals(0, adapter.itemCount)
        val person = Person("hello")
        adapter.list = listOf(person)
        assertEquals(1, adapter.itemCount)
        assertEquals(person, adapter.getData().first())
    }

    @Disabled("by DatabindingComponent")
    @Test
    fun bindViewModel() {
        val viewDataBinding = mock<ViewDataBinding>()
        val data = Person("123")
        adapter.bindViewModel(viewDataBinding, 1, data)
        verify(viewDataBinding).setVariable(MockDiffUtilAdapter.BR_ID, data)
    }

    @Disabled("by DataBindingComponent...")
    @Test
    fun onBindViewHolder() {
        val data = Person("123")
        adapter.list = listOf(data)
        val viewDataBinding = mock<ViewDataBinding>()
        val dataBindingViewHolder = DataBindingViewHolder<Person>(viewDataBinding)
        adapter.onBindViewHolder(dataBindingViewHolder, 0)
        verify(viewDataBinding).setVariable(MockDiffUtilAdapter.BR_ID, data)

    }

    @Test
    fun getLayoutIdByViewType() {
        assertEquals(MockDiffUtilAdapter.LAYOUT_ID, adapter.getLayoutIdByViewType(123))
    }

    @Test
    fun asRxTransformer() {
        assertEquals(adapter.rxTransformer, adapter.asRxTransformer())
    }

}

class MockDiffUtilAdapter : DiffUtilAdapter<Person>() {
    override fun bindViewModel(viewDataBinding: ViewDataBinding, itemViewType: Int, data: Person) {
        viewDataBinding.setVariable(BR_ID, data)
    }

    override fun getLayoutIdByViewType(viewType: Int): Int {
        return LAYOUT_ID
    }

    companion object {
        internal const val BR_ID = 301
        internal const val LAYOUT_ID = 101
    }
}