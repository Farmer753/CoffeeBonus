package ru.ll.coffeebonus.ui.coffee

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
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.ll.coffeebonus.R
import ru.ll.coffeebonus.databinding.FragmentCoffeeBinding
import ru.ll.coffeebonus.domain.CoffeeShop
import ru.ll.coffeebonus.ui.BaseFragment
import ru.ll.coffeebonus.ui.login.LoginFragment.Companion.ARG_OPEN_PROFILE
import ru.ll.coffeebonus.util.DrawableImageProvider
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CoffeeFragment : BaseFragment<FragmentCoffeeBinding, CoffeeViewModel>() {

    companion object {
        const val ARG_COFFEESHOP = "ARG_COFFEESHOP"
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCoffeeBinding =
        FragmentCoffeeBinding::inflate

    @Inject
    lateinit var viewModelAssistedFactory: CoffeeViewModel.Factory

    override val viewModel: CoffeeViewModel by viewModels {
        CoffeeViewModel.provideFactory(
            viewModelAssistedFactory,
            requireArguments().getSerializable(ARG_COFFEESHOP) as CoffeeShop
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.nameTextView.text = "Название ${viewModel.coffeeShop.name}"
        binding.addressTextView.text = "Адрес ${viewModel.coffeeShop.address}"
        binding.mapView.setNoninteractive(true)
        binding.favoriteImageView.setOnClickListener { viewModel.toggleFavorite() }
        val imageProvider = DrawableImageProvider(
            requireContext(),
            R.drawable.ic_action_name
        )
        binding.mapView.map.mapObjects.addPlacemark(
            Point(
                viewModel.coffeeShop.latitude.toDouble(),
                viewModel.coffeeShop.longitude.toDouble()
            ),
            imageProvider
        )
        binding.mapView.map.move(
            CameraPosition(
                Point(
                    viewModel.coffeeShop.latitude.toDouble(),
                    viewModel.coffeeShop.longitude.toDouble()
                ),
                15.0f, 0.0f, 0.0f
            )
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventsFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
                    Timber.d("$event event")
                    when (event) {
                        is CoffeeViewModel.Event.ShowNeedAuthorisationMessage -> {
                            val snackbar = Snackbar.make(
                                binding.root.rootView,
                                R.string.need_authorisation,
                                Snackbar.LENGTH_LONG
                            )
                            snackbar.anchorView = binding.anchorView
                            snackbar.setAction(R.string.login) { viewModel.onLoginClick() }
                            snackbar.show()

                        }
                        is CoffeeViewModel.Event.NavigationToLogin -> {
                            findNavController().navigate(
                                R.id.action_coffee_to_login,
                                bundleOf(ARG_OPEN_PROFILE to false)

                            )
                        }
                        is CoffeeViewModel.Event.ShowMessage -> showMessage(event.message)
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadingFavoriteStateFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    if (it) {
                        binding.progressFavoritesView.visibility = View.VISIBLE
                    } else {
                        binding.progressFavoritesView.visibility = View.GONE
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadingStateFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    if (it) {
                        binding.progressView.visibility = View.VISIBLE
                    } else {
                        binding.progressView.visibility = View.GONE
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteCoffeeShopStateFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    if (it) {
                        Timber.d("кофейня в избранном")
                        binding.favoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_24)
                    } else {
                        Timber.d("кофейня не в избранном")
                        binding.favoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                    }
                }
        }
        //                        TODO подписка на ошибку
        //                        TODO подписка на ашкуыещку кофейню
    }

}