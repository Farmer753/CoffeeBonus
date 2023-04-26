package ru.ll.coffeebonus.ui.profile

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.ll.coffeebonus.databinding.ItemEmptyBinding
import ru.ll.coffeebonus.ui.adapter.AdapterItem

fun emptyAdapterDelegate() =
    adapterDelegateViewBinding<EmptyUiItem, AdapterItem, ItemEmptyBinding>(
        { layoutInflater, parent ->
            ItemEmptyBinding.inflate(layoutInflater, parent, false)
        }) {
    }
