package ru.ll.coffeebonus.ui.profile

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.ll.coffeebonus.databinding.ItemEmptyBinding
import ru.ll.coffeebonus.databinding.ItemShowAllBinding
import ru.ll.coffeebonus.ui.adapter.AdapterItem

fun showAllAdapterDelegate() =
    adapterDelegateViewBinding<ShowAllUiItem, AdapterItem, ItemShowAllBinding>(
        { layoutInflater, parent ->
            ItemShowAllBinding.inflate(layoutInflater, parent, false)
        }) {
    }
