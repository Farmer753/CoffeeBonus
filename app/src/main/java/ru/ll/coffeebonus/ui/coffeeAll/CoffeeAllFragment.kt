package ru.ll.coffeebonus.ui.coffeeAll

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.R
import ru.ll.coffeebonus.databinding.FragmentCoffeeAllBinding
import ru.ll.coffeebonus.ui.BaseFragment
import ru.ll.coffeebonus.ui.adapter.AdapterItem
import ru.ll.coffeebonus.ui.coffee.CoffeeFragment
import ru.ll.coffeebonus.ui.profile.*
import timber.log.Timber

@AndroidEntryPoint
class CoffeeAllFragment : BaseFragment<FragmentCoffeeAllBinding, CoffeeAllViewModel>() {

    override val viewModel: CoffeeAllViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCoffeeAllBinding =
        FragmentCoffeeAllBinding::inflate

    private lateinit var adapter: ListDelegationAdapter<List<AdapterItem>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonRetry.setOnClickListener { viewModel.loadFavoriteCoffeeShop() }

        initRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventsFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
                    when (event) {
                        is CoffeeAllViewModel.Event.CloseScreen -> {
                            findNavController().popBackStack()
                        }
                        is CoffeeAllViewModel.Event.NavigateToCoffee -> {
                            findNavController().navigate(
                                R.id.action_coffee_all_to_coffee,
                                bundleOf(
                                    CoffeeFragment.ARG_COFFEESHOP to event.coffeeShop
                                )
                            )
                        }
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadingStateFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    Timber.d("прогресс $it")
                    binding.progressView.visibility = if (it) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorStateFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    Timber.d("ошибка $it")
                    binding.errorView.visibility = if (it != null) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    binding.errorTextView.text = it
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.stateFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    Timber.d("state $it")
                    when (it) {
                        is CoffeeAllViewModel.State.Error -> {
                            adapter.items = listOf(ErrorUiItem(it.message))
                        }
                        is CoffeeAllViewModel.State.Loading -> {
                            adapter.items = listOf(LoadingUiItem)
                        }
                        is CoffeeAllViewModel.State.Success -> {
                            adapter.items = it.data
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
        }
    }

    private fun initRecyclerView() {
        val delegateManager = AdapterDelegatesManager<List<AdapterItem>>()
        delegateManager.addDelegate(coffeeShopAdapterDelegate { viewModel.onCoffeeShopClick(it) })
        delegateManager.addDelegate(loadingAdapterDelegate())
        delegateManager.addDelegate(errorAdapterDelegate { viewModel.loadFavoriteCoffeeShop() })

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)
        adapter = ListDelegationAdapter(delegateManager)
        binding.recyclerView.adapter = adapter
    }
}