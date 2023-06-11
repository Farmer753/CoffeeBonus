package ru.ll.coffeebonus.ui.profile

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
import coil.load
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.R
import ru.ll.coffeebonus.databinding.FragmentProfileBinding
import ru.ll.coffeebonus.ui.BaseFragment
import ru.ll.coffeebonus.ui.adapter.AdapterItem
import ru.ll.coffeebonus.ui.coffee.CoffeeFragment
import timber.log.Timber

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding =
        FragmentProfileBinding::inflate

    private lateinit var adapter: ListDelegationAdapter<List<AdapterItem>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonRetry.setOnClickListener { viewModel.loadUser() }
        binding.buttonLogout.setOnClickListener { viewModel.logout() }

        initRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventsFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
                    when (event) {
                        is ProfileViewModel.Event.CloseScreen -> {
                            findNavController().popBackStack()
                        }
                        is ProfileViewModel.Event.NavigateToCoffee -> {
                            findNavController().navigate(
                                R.id.action_profile_to_coffee,
                                bundleOf(
                                    CoffeeFragment.ARG_COFFEESHOP to event.coffeeShop
                                )
                            )
                        }
                        is ProfileViewModel.Event.NavigateToCoffeeAll -> {
                            findNavController().navigate(
                                R.id.action_profile_to_coffeeAll
                            )
                        }
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userStateFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .filterNotNull()
                .collect {
                    Timber.d("юзер $it")
                    binding.nameTextView.text = it.name
                    binding.emailTextView.text = it.email
                    binding.iconImageView.load(it.avatarUrl)
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
                        is ProfileViewModel.State.Error -> {
                            adapter.items = listOf(ErrorUiItem(it.message))
                        }
                        is ProfileViewModel.State.Loading -> {
                            adapter.items = listOf(LoadingUiItem)
                        }
                        is ProfileViewModel.State.Success -> {
                            if (it.coffeeShopMoreThanTen){
                                adapter.items = listOf(ShowAllUiItem)
                            }
//                            TODO добавить переменную boolean в data класс Success, она try, если избранных кофеен больше 10
//                            после последней кофейни добавить еще один item с карточкой "посмотреть все" и по нажатию навигироваться на новый экран с вертикальным списком с пагинацией
                            adapter.items = it.data
                        }
                        is ProfileViewModel.State.Empty -> {
                            adapter.items = listOf(EmptyUiItem)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
        }
    }

    private fun initRecyclerView() {
        val delegateManager = AdapterDelegatesManager<List<AdapterItem>>()
        delegateManager.addDelegate(coffeeShopAdapterDelegate { viewModel.onCoffeeShopClick(it) })
        delegateManager.addDelegate(coffeeShopAllAdapterDelegate { viewModel.onCoffeeShopAllClick() })
        delegateManager.addDelegate(loadingAdapterDelegate())
        delegateManager.addDelegate(errorAdapterDelegate { viewModel.loadFavoriteCoffeeShop() })
        delegateManager.addDelegate(emptyAdapterDelegate())
        delegateManager.addDelegate(showAllAdapterDelegate())
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)
        adapter = ListDelegationAdapter(delegateManager)
        binding.recyclerView.adapter = adapter
    }
}