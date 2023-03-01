package ru.ll.coffeebonus.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.ll.coffeebonus.R
import ru.ll.coffeebonus.databinding.FragmentProfileBinding
import ru.ll.coffeebonus.ui.BaseFragment
import timber.log.Timber

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding =
        FragmentProfileBinding::inflate


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonMap.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_map)
        }
        binding.buttonLogin.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_login)
        }
        binding.buttonLogout.setOnClickListener { viewModel.logout() }
    }

}