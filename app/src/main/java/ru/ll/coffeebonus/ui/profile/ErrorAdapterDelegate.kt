package ru.ll.coffeebonus.ui.profile

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.ll.coffeebonus.databinding.ItemErrorBinding
import ru.ll.coffeebonus.ui.adapter.AdapterItem

fun errorAdapterDelegate(clickListener: () -> Unit) =
    adapterDelegateViewBinding<ErrorUiItem, AdapterItem, ItemErrorBinding>(
        { layoutInflater, parent ->
            ItemErrorBinding.inflate(layoutInflater, parent, false)
        }) {
        bind {
            with(binding) {
                errorTextView.text = item.error
                retryButton.setOnClickListener { clickListener() }
            }
        }
    }
