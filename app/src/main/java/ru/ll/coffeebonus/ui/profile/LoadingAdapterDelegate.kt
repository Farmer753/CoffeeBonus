package ru.ll.coffeebonus.ui.profile

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.ll.coffeebonus.databinding.ItemLoadingBinding
import ru.ll.coffeebonus.ui.adapter.AdapterItem

fun loadingAdapterDelegate() =
    adapterDelegateViewBinding<LoadingUiItem, AdapterItem, ItemLoadingBinding>(
        { layoutInflater, parent ->
            ItemLoadingBinding.inflate(layoutInflater, parent, false)
        }) {
    }
